package space.devport.wertik.custommessages.system;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.text.message.Message;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.system.struct.MessageStorage;
import space.devport.wertik.custommessages.system.struct.MessageType;
import space.devport.wertik.custommessages.system.struct.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {

    private final MessagePlugin plugin;

    private final Map<MessageType, MessageStorage> loadedMessages = new HashMap<>();

    private Configuration messageConfiguration;

    public MessageManager(MessagePlugin plugin) {
        this.plugin = plugin;
    }

    public Message getDefault(MessageType type) {
        return getMessage(type, "default");
    }

    @NotNull
    public Message getMessage(MessageType type, String key) {

        if (key == null) return getDefault(type);

        MessageStorage storage = this.loadedMessages.getOrDefault(type, null);

        if (storage == null)
            return key.equalsIgnoreCase("default") ? new Message() : getDefault(type);
        Message message = storage.get(key);
        if (message == null)
            return key.equalsIgnoreCase("default") ? new Message() : getDefault(type);

        return new Message(message);
    }

    public Message getMessage(MessageType type, OfflinePlayer player) {
        return getMessage(type, plugin.getUserManager().getUser(player).getMessage(type));
    }

    public void load() {
        if (this.messageConfiguration == null)
            messageConfiguration = new Configuration(plugin, "messages");
        else
            messageConfiguration.load();

        for (MessageType type : MessageType.values()) {
            String typeName = type.toString().toLowerCase();

            if (messageConfiguration.getFileConfiguration().contains(typeName)) {
                MessageStorage storage = MessageStorage.from(messageConfiguration, typeName);

                if (storage == null)
                    continue;

                this.loadedMessages.put(type, storage);
            }
        }

        plugin.getConsoleOutput().info("Loaded " + this.loadedMessages.values().stream().map(m -> m.getMessages().values()).count() + " message(s)...");
    }

    @Nullable
    public String parseMessage(Player player, MessageType type, String messageName) {

        if (!plugin.getConfiguration().section("formats").contains(type.toString().toLowerCase()))
            return null;

        String format = plugin.getConfiguration().getString("formats." + type.toString().toLowerCase());

        if (format == null)
            return null;

        Message message = getMessage(type, messageName)
                .replace("%player%", player.getName());

        if (message.isEmpty())
            return null;

        message = parseAdditional(message, player);

        format = format.replaceAll("(?i)%message%", message.toString());

        return MessageUtil.formatMessage(format, player);
    }

    public Message parseAdditional(Message message, Player player) {
        return message.replace("%message_leave%", getMessage(MessageType.LEAVE, player))
                .replace("%message_join%", getMessage(MessageType.JOIN, player))
                .replace("%message_kick%", getMessage(MessageType.KICK, player));
    }

    @Nullable
    public String parseMessage(Player player, MessageType type) {
        User user = plugin.getUserManager().getUser(player);
        return parseMessage(player, type, user.getMessage(type));
    }

    public List<String> getMessages(MessageType type) {
        MessageStorage storage = this.loadedMessages.getOrDefault(type, null);
        return storage == null ? new ArrayList<>() : new ArrayList<>(storage.getMessages().keySet());
    }

    public Map<MessageType, MessageStorage> getLoadedMessages() {
        return Collections.unmodifiableMap(this.loadedMessages);
    }
}