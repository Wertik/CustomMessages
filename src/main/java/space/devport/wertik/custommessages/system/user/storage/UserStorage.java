package space.devport.wertik.custommessages.system.user.storage;

import space.devport.wertik.custommessages.system.user.User;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserStorage {

    CompletableFuture<User> load(UUID uniqueID);

    CompletableFuture<Set<User>> load(Set<UUID> uuids);

    CompletableFuture<Boolean> save(User user);

    CompletableFuture<Void> save(Collection<User> users);

    CompletableFuture<Boolean> delete(UUID uniqueID);
}
