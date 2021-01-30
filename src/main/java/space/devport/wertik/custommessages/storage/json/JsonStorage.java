package space.devport.wertik.custommessages.storage.json;

import lombok.extern.java.Log;
import space.devport.utils.utility.json.GsonHelper;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.storage.IStorage;
import space.devport.wertik.custommessages.system.user.User;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Log
public class JsonStorage implements IStorage {

    private final MessagePlugin plugin;

    private final Map<UUID, User> storedUsers = new HashMap<>();

    private final GsonHelper gsonHelper = new GsonHelper();

    private final String fileName;

    public JsonStorage(MessagePlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    @Override
    public CompletableFuture<Boolean> initialize() {
        return gsonHelper.loadMapAsync(plugin.getDataFolder() + "/" + fileName, UUID.class, User.class).thenApplyAsync(loaded -> {
            if (loaded == null)
                loaded = new HashMap<>();

            storedUsers.clear();
            storedUsers.putAll(loaded);
            return true;
        }).exceptionally(e -> {
            log.warning(String.format("Failed to load users: %s", e.getMessage()));
            e.printStackTrace();
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> finish() {
        return saveToFile();
    }

    private CompletableFuture<Boolean> saveToFile() {
        return gsonHelper.save(storedUsers, plugin.getDataFolder() + "/" + fileName).thenApplyAsync(ignored -> true)
                .exceptionally(e -> {
                    log.warning(String.format("Failed to save users: %s", e.getMessage()));
                    e.printStackTrace();
                    return false;
                });
    }

    @Override
    public CompletableFuture<User> load(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> storedUsers.get(uniqueID));
    }

    @Override
    public CompletableFuture<Boolean> save(User user) {
        return CompletableFuture.supplyAsync(() -> {
            storedUsers.put(user.getUniqueID(), user);
            return true;
        });
    }

    @Override
    public CompletableFuture<Void> save(Collection<User> users) {
        return CompletableFuture.runAsync(() -> {
            for (User user : users) {
                storedUsers.put(user.getUniqueID(), user);
            }
            saveToFile();
        });
    }

    @Override
    public CompletableFuture<Set<User>> load(Set<UUID> uuids) {
        return CompletableFuture.supplyAsync(() -> {
            Set<User> out = new HashSet<>();
            for (User user : storedUsers.values()) {
                if (uuids.contains(user.getUniqueID()))
                    out.add(user);
            }
            return out;
        });
    }

    @Override
    public CompletableFuture<Boolean> delete(UUID uniqueID) {
        return CompletableFuture.supplyAsync(() -> {
            storedUsers.remove(uniqueID);
            return true;
        });
    }
}
