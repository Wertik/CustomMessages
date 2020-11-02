package space.devport.wertik.custommessages.system;

import com.google.common.base.Strings;
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

    /**
     * Fetch requested message, try to return the default.
     */
    @Nullable
    public MessageStorage getStorage(@NotNull MessageType type) {
        return this.loadedMessages.get(type);
    }

    public void load() {
        if (this.messageConfiguration == null)
            messageConfiguration = new Configuration(plugin, "messages");
        else
            messageConfiguration.load();

        for (MessageType type : MessageType.values()) {
            String typeName = type.toString().toLowerCase();

            if (messageConfiguration.getFileConfiguration().contains(typeName)) {
                String format = plugin.getConfiguration().getString("formats." + typeName);

                if (Strings.isNullOrEmpty(format))
                    continue;

                MessageStorage storage = MessageStorage.from(messageConfiguration, typeName, format);

                if (storage == null)
                    continue;

                this.loadedMessages.put(type, storage);
            }
        }

        plugin.getConsoleOutput().info("Loaded " + this.loadedMessages.values().stream().map(m -> m.getMessages().values()).count() + " message(s)...");
    }

    @Nullable
    public String getFormattedMessage(@Nullable Player player, @NotNull MessageType type, @Nullable String messageName) {

        MessageStorage storage = getStorage(type);

        if (storage == null)
            return null;

        String format = storage.getFormat();

        if (Strings.isNullOrEmpty(format))
            return null;

        if (!storage.has(messageName))
            messageName = "default";

        Message message = storage.get(messageName);

        if (message == null || message.isEmpty())
            return null;

        format = format.replaceAll("(?i)%message%", message.toString());

        return MessageUtil.formatMessage(format, player);
    }

    @Nullable
    public String getFormattedMessage(Player player, MessageType type) {
        User user = plugin.getUserManager().getOrCreateUser(player);
        return getFormattedMessage(player, type, user.getMessage(type));
    }

    public List<String> getMessages(MessageType type) {
        MessageStorage storage = this.loadedMessages.get(type);
        return storage == null ? new ArrayList<>() : new ArrayList<>(storage.getMessages().keySet());
    }

    public Map<MessageType, MessageStorage> getLoadedMessages() {
        return Collections.unmodifiableMap(this.loadedMessages);
    }
}