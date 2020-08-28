package space.devport.wertik.custommessages.system.struct;

import lombok.Getter;
import lombok.Setter;
import space.devport.utils.text.message.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageStorage {

    @Getter
    @Setter
    private Map<String, Message> messages = new HashMap<>();

    public void add(String key, Message value) {
        this.messages.put(key, value);
    }

    public Message get(String key) {
        return messages.getOrDefault(key, null);
    }
}