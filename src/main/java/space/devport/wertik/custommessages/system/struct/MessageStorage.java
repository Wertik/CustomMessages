package space.devport.wertik.custommessages.system.struct;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.ConsoleOutput;
import space.devport.utils.configuration.Configuration;
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
        return messages.get(key);
    }

    @Nullable
    public static MessageStorage from(Configuration configuration, String path) {

        ConfigurationSection section = configuration.getFileConfiguration().getConfigurationSection(path);

        if (section == null) {
            ConsoleOutput.getInstance().warn("Could not load messages from " + configuration.getFile().getName() + "@" + path + ", the section is invalid.");
            return null;
        }

        MessageStorage storage = new MessageStorage();

        for (String key : section.getKeys(false)) {
            storage.add(key, configuration.getMessage(section.getCurrentPath() + "." + key));
            ConsoleOutput.getInstance().debug("Loaded message " + section.getCurrentPath() + "." + key);
        }

        return storage;
    }
}