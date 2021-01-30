package space.devport.wertik.custommessages.storage;

import space.devport.wertik.custommessages.system.user.storage.UserStorage;

import java.util.concurrent.CompletableFuture;

public interface IStorage extends UserStorage {

    CompletableFuture<Boolean> initialize();

    CompletableFuture<Boolean> finish();
}
