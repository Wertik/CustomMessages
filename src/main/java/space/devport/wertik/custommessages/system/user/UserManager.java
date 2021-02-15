package space.devport.wertik.custommessages.system.user;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.callbacks.ExceptionCallback;
import space.devport.dock.util.ParseUtil;
import space.devport.dock.util.ThreadUtil;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.storage.IStorage;
import space.devport.wertik.custommessages.storage.LoadCache;
import space.devport.wertik.custommessages.storage.StorageType;
import space.devport.wertik.custommessages.storage.json.JsonStorage;
import space.devport.wertik.custommessages.storage.mysql.ConnectionInfo;
import space.devport.wertik.custommessages.storage.mysql.MysqlStorage;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log
public class UserManager {

    private final MessagePlugin plugin;

    private final LoadCache<UUID, User> loadCache = new LoadCache<>();

    private final Map<UUID, User> loadedUsers = new ConcurrentHashMap<>();

    @Getter
    private StorageType storageType;

    private IStorage storage;

    private int errThreshold;

    private Thread autoSave;

    public UserManager(MessagePlugin plugin) {
        this.plugin = plugin;
    }

    public void stopAutoSave() {
        if (autoSave == null)
            return;

        autoSave.interrupt();
        this.autoSave = null;
    }

    public void startAutoSave() {
        stopAutoSave();

        if (!plugin.getConfig().getBoolean("auto-save.enabled", false))
            return;

        this.autoSave = ThreadUtil.createDelayedRepeatingTask(this::save,
                plugin.getConfig().getInt("auto-save.interval", 300),
                "Messages Auto Save");

        autoSave.start();
    }

    public CompletableFuture<Void> initializeStorage() {
        return initializeStorage(null);
    }

    public StorageType loadStorageType() {
        return ParseUtil.parseEnumHandled(plugin.getConfiguration().getString("storage.type", "json"), StorageType.class, StorageType.JSON, ExceptionCallback.IGNORE);
    }

    public CompletableFuture<Void> initializeStorage(StorageType force) {
        this.storageType = force == null ? ParseUtil.parseEnumHandled(plugin.getConfiguration().getString("storage.type", "json"),
                StorageType.class, StorageType.JSON, e -> log.warning(() -> "Invalid storage type " + e.getInput() + ", using JSON as default.")) : force;

        switch (storageType) {
            case JSON:
                storage = new JsonStorage(plugin, plugin.getConfiguration().getString("storage.json.file", "data.json"));
                log.info("Initialized JSON storage.");
                break;
            case MYSQL:
                ConnectionInfo connectionInfo = ConnectionInfo.load(plugin.getConfig().getConfigurationSection("storage.mysql"));

                if (connectionInfo == null) {
                    log.warning(() -> "Failed to load MySQL connection info from config.yml, using JSON.");
                    initializeStorage(StorageType.JSON);
                    return CompletableFuture.completedFuture(null);
                }

                storage = new MysqlStorage(connectionInfo, plugin.getConfiguration().getString("storage.mysql.table", "users"));
                log.info("Initialized MySQL storage.");
                break;
        }

        return storage.initialize().thenAcceptAsync(res -> {
            if (!res) {
                if (force == null) {
                    log.warning(() -> "Failed to initialize storage, using JSON as default.");
                    initializeStorage(StorageType.JSON);
                } else
                    log.severe("Failed to initialize the default (JSON) storage type. Cannot function properly.");
            }
        });
    }

    public boolean isInitialized() {
        return storage != null;
    }

    private void checkInitialized() {
        if (!isInitialized() && errThreshold > 5) {
            errThreshold = 0;
            log.severe("Storage is not initialized. User data will be lost.");
        } else errThreshold++;
    }

    @Nullable
    public User getUser(OfflinePlayer player) {
        return getUser(player.getUniqueId());
    }

    @Nullable
    public User getUser(UUID uniqueID) {
        return this.loadedUsers.get(uniqueID);
    }

    public CompletableFuture<User> getOrLoadUser(UUID uniqueID) {
        checkInitialized();
        if (loadedUsers.containsKey(uniqueID))
            return CompletableFuture.supplyAsync(() -> loadedUsers.get(uniqueID));
        return loadUser(uniqueID);
    }

    @NotNull
    public CompletableFuture<User> getOrCreateUser(UUID uniqueID) {
        checkInitialized();
        return getOrLoadUser(uniqueID).thenApplyAsync(user -> user == null ? createUser(uniqueID) : user);
    }

    @NotNull
    public CompletableFuture<User> getOrCreateUser(OfflinePlayer offlinePlayer) {
        return getOrCreateUser(offlinePlayer.getUniqueId());
    }

    @NotNull
    public User createUser(UUID uniqueID) {
        checkInitialized();
        User user = new User(uniqueID);
        this.loadedUsers.put(uniqueID, user);
        storage.save(user);
        log.fine(() -> "Created user " + uniqueID.toString());
        return user;
    }

    public CompletableFuture<User> loadUser(UUID uniqueID) {
        if (loadCache.isLoading(uniqueID))
            return loadCache.getLoading(uniqueID);

        CompletableFuture<User> future = storage.load(uniqueID);

        loadCache.setLoading(uniqueID, future);

        return future.thenApplyAsync(user -> {
            loadCache.setLoaded(uniqueID);
            if (user != null) {
                loadedUsers.put(uniqueID, user);
                log.fine(() -> "Loaded user " + uniqueID.toString());
            }
            return user;
        });
    }

    public void saveUser(UUID uniqueID) {
        User user = getUser(uniqueID);
        if (user != null) {
            storage.save(user);
            log.fine(() -> "Saved used " + uniqueID.toString());
        }
    }

    public void load() {
        checkInitialized();
        storage.load(Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet()))
                .thenAcceptAsync(loaded -> {
                    if (loaded == null)
                        loaded = new HashSet<>();

                    this.loadedUsers.putAll(loaded.stream().collect(Collectors.toMap(User::getUniqueID, u -> u)));
                    log.info("Loaded " + loaded.size() + " user(s)...");
                })
                .exceptionally(e -> {
                    log.severe(() -> "Could not load users due to: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    private void purgeEmpty() {
        int count = 0;
        for (User user : loadedUsers.values()) {
            if (user.getMessages().isEmpty()) {
                this.loadedUsers.remove(user.getUniqueID());
                storage.delete(user.getUniqueID());
                count++;
            }
        }
        log.info(String.format("Purged %d empty account(s)...", count));
    }

    public CompletableFuture<Void> save() {
        purgeEmpty();

        checkInitialized();
        return storage.save(loadedUsers.values())
                .thenRunAsync(() -> log.info(() -> "Saved " + loadedUsers.size() + " user(s)..."))
                .exceptionally(e -> {
                    log.severe(() -> "Could not save " + loadedUsers.size() + " users: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
    }

    // Join the statements to ensure it runs before the plugin unloads.
    public void finish() {
        if (storage instanceof JsonStorage)
            storage.save(loadedUsers.values()).thenRun(() -> {
                storage.finish().thenAcceptAsync(res -> {
                    if (!res)
                        log.warning(() -> "Failed to save users.");
                    else
                        log.info(() -> "Saved " + loadedUsers.size() + " user(s)...");
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            }).join();
        else
            storage.save(loadedUsers.values())
                    .thenAcceptAsync(res -> {
                        log.info(String.format("Saved %d user(s)...", loadedUsers.size()));
                        storage.finish();
                    })
                    .exceptionally(e -> {
                        e.printStackTrace();
                        return null;
                    }).join();
    }
}