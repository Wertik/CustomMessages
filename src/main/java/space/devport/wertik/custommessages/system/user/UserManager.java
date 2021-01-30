package space.devport.wertik.custommessages.system.user;

import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.utility.ParseUtil;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.storage.IStorage;
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

    private final Map<UUID, User> loadedUsers = new ConcurrentHashMap<>();

    private IStorage storage;

    private int errThreshold;

    public UserManager(MessagePlugin plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Void> initializeStorage() {
        return initializeStorage(null);
    }

    public CompletableFuture<Void> initializeStorage(StorageType force) {
        StorageType type = force == null ? ParseUtil.parseEnumHandled(plugin.getConfiguration().getString("storage.type", "json"),
                StorageType.class, StorageType.JSON, e -> log.warning(String.format("Invalid storage type %s, using JSON as default.", e.getInput()))) : force;

        switch (type) {
            case JSON:
                storage = new JsonStorage(plugin, plugin.getConfiguration().getString("storage.json.file", "data.json"));
                break;
            case MYSQL:
                ConnectionInfo connectionInfo = ConnectionInfo.load(plugin.getConfig().getConfigurationSection("storage.mysql"));

                if (connectionInfo == null) {
                    log.warning("Failed to load MySQL connection info from config.yml, using JSON.");
                    initializeStorage(StorageType.JSON);
                    return CompletableFuture.completedFuture(null);
                }

                storage = new MysqlStorage(connectionInfo, plugin.getConfiguration().getString("storage.mysql.table", "users"));
                break;
        }

        return storage.initialize().thenAcceptAsync(res -> {
            if (!res) {
                if (force == null) {
                    log.warning("Failed to initialize storage, using JSON as default.");
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
        checkInitialized();
        return this.loadedUsers.get(uniqueID);
    }

    @NotNull
    public User getOrCreateUser(UUID uniqueID) {
        checkInitialized();
        return this.loadedUsers.containsKey(uniqueID) ? this.loadedUsers.get(uniqueID) : createUser(uniqueID);
    }

    @NotNull
    public User getOrCreateUser(OfflinePlayer offlinePlayer) {
        return getOrCreateUser(offlinePlayer.getUniqueId());
    }

    @NotNull
    public User createUser(UUID uniqueID) {
        checkInitialized();
        User user = new User(uniqueID);
        this.loadedUsers.put(uniqueID, user);
        return user;
    }

    public void load() {
        checkInitialized();
        storage.load(Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet()))
                .thenAcceptAsync(loaded -> {
                    if (loaded == null)
                        loaded = new HashSet<>();

                    this.loadedUsers.putAll(loaded.parallelStream()
                            .collect(Collectors.toMap(User::getUniqueID, u -> u)));
                    log.info("Loaded " + loaded.size() + " user(s)...");
                })
                .exceptionally(e -> {
                    log.severe("Could not load users: " + e.getMessage());
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

    public void save() {
        purgeEmpty();

        checkInitialized();
        storage.save(loadedUsers.values())
                .thenRunAsync(() -> log.info(String.format("Saved %d user(s)...", loadedUsers.size())))
                .exceptionally(e -> {
                    log.severe(String.format("Could not save %d users: %s", loadedUsers.size(), e.getMessage()));
                    e.printStackTrace();
                    return null;
                });
    }
}