package space.devport.wertik.custommessages.system.struct;

import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.text.message.Message;

import java.util.HashMap;
import java.util.Map;

@Log
public class MessageStorage {

    @Getter
    private final String format;

    @Getter
    private final Map<String, Message> messages = new HashMap<>();

    public MessageStorage(String format) {
        this.format = format;
    }

    public void add(String name, Message message) {
        this.messages.put(name, message);
    }

    public Message get(String name) {
        return messages.get(name);
    }

    public boolean has(String name) {
        return messages.containsKey(name);
    }

    @Nullable
    public static MessageStorage load(Configuration configuration, String path, String format) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            log.warning("Could not load messages from " + configuration.getFile().getName() + "@" + path + ", the section is invalid.");
            return null;
        }

        MessageStorage storage = new MessageStorage(format);

        for (String key : section.getKeys(false)) {
            storage.add(key, configuration.getMessage(section.getCurrentPath() + "." + key));
            log.log(DebugLevel.DEBUG, "Loaded message " + section.getCurrentPath() + "." + key);
        }

        return storage;
    }
}