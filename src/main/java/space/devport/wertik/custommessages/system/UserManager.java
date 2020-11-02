package space.devport.wertik.custommessages.system;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.utility.json.GsonHelper;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.system.struct.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final MessagePlugin plugin;

    private final GsonHelper gsonHelper = new GsonHelper();

    private final Map<UUID, User> loadedUsers = new HashMap<>();

    public UserManager(MessagePlugin plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public User getUser(OfflinePlayer player) {
        return getUser(player.getUniqueId());
    }

    @Nullable
    public User getUser(UUID uniqueID) {
        return this.loadedUsers.get(uniqueID);
    }

    @NotNull
    public User getOrCreateUser(UUID uniqueID) {
        return this.loadedUsers.containsKey(uniqueID) ? this.loadedUsers.get(uniqueID) : createUser(uniqueID);
    }

    @NotNull
    public User getOrCreateUser(OfflinePlayer offlinePlayer) {
        return getOrCreateUser(offlinePlayer.getUniqueId());
    }

    @NotNull
    public User createUser(UUID uniqueID) {
        User user = new User(uniqueID);
        this.loadedUsers.put(uniqueID, user);
        return user;
    }

    public void load() {
        gsonHelper.loadMapAsync(plugin.getDataFolder().getPath() + "/data.json", UUID.class, User.class).thenAcceptAsync(users -> {

            if (users == null)
                users = new HashMap<>();

            this.loadedUsers.clear();
            this.loadedUsers.putAll(users);

            ConsoleOutput.getInstance().info("Loaded " + this.loadedUsers.size() + " user(s)...");
        }).exceptionally(e -> {
            ConsoleOutput.getInstance().err("Could not load users: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
    }

    private void purgeEmpty() {
        int count = 0;
        for (User user : new HashSet<>(this.loadedUsers.values())) {
            if (user.getMessages().isEmpty()) {
                this.loadedUsers.remove(user.getUniqueID());
                count++;
            }
        }
        plugin.getConsoleOutput().info("Purged " + count + " empty account(s)...");
    }

    public void save() {
        purgeEmpty();

        final Map<UUID, User> finalCache = new HashMap<>(this.loadedUsers);

        gsonHelper.save(finalCache, plugin.getDataFolder().getPath() + "/data.json").thenRunAsync(() -> {
            ConsoleOutput.getInstance().info("Saved " + finalCache.size() + " user(s)...");
        }).exceptionally(e -> {
            ConsoleOutput.getInstance().err("Could not save users: " + e.getMessage());
            e.printStackTrace();
            return null;
        });
    }
}