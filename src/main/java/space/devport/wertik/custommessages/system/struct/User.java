package space.devport.wertik.custommessages.system.struct;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {

    @Getter
    private final UUID uniqueID;

    private final Map<MessageType, String> messages = new HashMap<>();

    public User(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    @NotNull
    public String getMessage(MessageType type) {
        return messages.getOrDefault(type, "default");
    }

    public void setMessage(MessageType type, String message) {
        this.messages.put(type, message);
    }

    public void clearMessages() {
        this.messages.clear();
    }

    public Map<MessageType, String> getMessages() {
        return Collections.unmodifiableMap(messages);
    }
}