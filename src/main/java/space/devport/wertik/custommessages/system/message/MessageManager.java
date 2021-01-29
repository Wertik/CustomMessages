package space.devport.wertik.custommessages.system.message;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.configuration.Configuration;
import space.devport.utils.text.message.Message;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.util.MessageUtil;
import space.devport.wertik.custommessages.system.message.type.MessageType;
import space.devport.wertik.custommessages.system.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Log
public class MessageManager {

    private final MessagePlugin plugin;

    private final Map<MessageType, MessageStorage> loadedMessages = new HashMap<>();

    private final Configuration messageConfiguration;

    @Getter
    private MessagePosition position;

    public MessageManager(MessagePlugin plugin) {
        this.plugin = plugin;
        this.messageConfiguration = new Configuration(plugin, "messages");
    }

    public void loadOptions() {
        this.position = MessagePosition.fromString(plugin.getConfig().getString("message-position"), MessagePosition.TEXT);
    }

    /**
     * Fetch requested message, try to return the default.
     */
    @Nullable
    public MessageStorage getStorage(@NotNull MessageType type) {
        return this.loadedMessages.get(type);
    }

    public void load() {
        messageConfiguration.load();

        for (MessageType type : MessageType.values()) {
            String typeName = type.toString().toLowerCase();

            if (messageConfiguration.getFileConfiguration().contains(typeName)) {
                String format = plugin.getConfiguration().getString("formats." + typeName);

                if (Strings.isNullOrEmpty(format))
                    continue;

                MessageStorage storage = MessageStorage.load(messageConfiguration, typeName, format);

                if (storage == null)
                    continue;

                this.loadedMessages.put(type, storage);
            }
        }

        log.info("Loaded " + this.loadedMessages.values().stream().map(m -> m.getMessages().values()).count() + " message(s)...");
    }

    @Nullable
    public String getFormattedMessage(@Nullable OfflinePlayer player, @NotNull MessageType type, @Nullable String messageName, Object... extra) {

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

        message = type.parseExtra(message.clone(), extra);

        format = format.replaceAll("(?i)%message%", message.toString());

        return MessageUtil.formatMessage(format, player);
    }

    @Nullable
    public String getFormattedMessage(OfflinePlayer player, MessageType type) {
        return getFormattedMessage(player, type, new Object[0]);
    }

    @Nullable
    public String getFormattedMessage(OfflinePlayer player, MessageType type, Object... extra) {
        User user = plugin.getUserManager().getOrCreateUser(player);
        return getFormattedMessage(player, type, user.getMessage(type), extra);
    }

    public List<String> getMessages(MessageType type) {
        MessageStorage storage = this.loadedMessages.get(type);
        return storage == null ? new ArrayList<>() : new ArrayList<>(storage.getMessages().keySet());
    }

    // Everything's enabled by default.
    public boolean isEnabled(MessageType type) {
        return plugin.getConfig().getBoolean(String.format("actions.%s", type.toString().toLowerCase()), true);
    }

    public Set<MessageType> getEnabledTypes() {
        return Arrays.stream(MessageType.values())
                .filter(this::isEnabled)
                .collect(Collectors.toSet());
    }

    public Map<MessageType, MessageStorage> getLoadedMessages() {
        return Collections.unmodifiableMap(this.loadedMessages);
    }
}