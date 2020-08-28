package space.devport.wertik.custommessages.system;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.OfflinePlayer;
import space.devport.wertik.custommessages.CustomMessagesPlugin;
import space.devport.wertik.custommessages.system.struct.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class UserManager {

    private final CustomMessagesPlugin plugin;

    private final Gson gson = new GsonBuilder()
            // .setPrettyPrinting()
            .create();

    private final Map<UUID, User> loadedUsers = new HashMap<>();

    public UserManager(CustomMessagesPlugin plugin) {
        this.plugin = plugin;
    }

    public User getUser(UUID uniqueID) {
        if (!this.loadedUsers.containsKey(uniqueID)) {
            plugin.getConsoleOutput().debug("Creating a new user");
            return createUser(uniqueID);
        }
        return this.loadedUsers.getOrDefault(uniqueID, null);
    }

    public User getUser(OfflinePlayer offlinePlayer) {
        return getUser(offlinePlayer.getUniqueId());
    }

    public User createUser(UUID uniqueID) {
        User user = new User(uniqueID);
        this.loadedUsers.put(uniqueID, user);
        return user;
    }

    public void load() {
        this.loadedUsers.clear();

        Path path = Paths.get(plugin.getDataFolder().getPath() + "/data.json");

        if (!Files.exists(path)) return;

        String input;
        try {
            input = String.join("", Files.readAllLines(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (Strings.isNullOrEmpty(input)) return;

        this.loadedUsers.putAll(gson.fromJson(input, new TypeToken<Map<UUID, User>>() {
        }.getType()));

        plugin.getConsoleOutput().info("Loaded " + this.loadedUsers.size() + " user(s)...");
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

        plugin.getConsoleOutput().info("Saving " + finalCache.size() + " user(s)...");

        String output = gson.toJson(finalCache, new TypeToken<Map<UUID, User>>() {
        }.getType());

        plugin.getConsoleOutput().debug("JSON: " + output);

        Path path = Paths.get(plugin.getDataFolder().getPath() + "/data.json");

        try {
            Files.write(path, output.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}