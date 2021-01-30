package space.devport.wertik.custommessages.system.message.type;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import space.devport.utils.text.message.Message;
import space.devport.wertik.custommessages.MessagePlugin;

import java.util.Map;

public enum MessageType {

    JOIN,
    LEAVE,
    KILL((player, message, extra) -> {
        for (Object obj : extra) {
            if (obj instanceof Player) {
                Player victim = (Player) obj;
                message.replace("%victim%", victim.getName());
            }
        }

        if (player.getPlayer() != null)
            message.replace("%killerHealth%", MessagePlugin.getInstance().getNumberFormat().format(player.getPlayer().getHealth()));

        return message;
    }, (message, defaults) -> {
        if (defaults.size() < 2)
            return message;

        return message.replaceAll("(?i)%victim%", defaults.get("Victim"))
                .replaceAll("(?i)%killerHealth%", defaults.get("Health"));
    });

    private final ExtraParser parser;

    private final DefaultParser defaultParser;

    MessageType() {
        this.parser = null;
        this.defaultParser = null;
    }

    MessageType(ExtraParser parser, DefaultParser defaultParser) {
        this.parser = parser;
        this.defaultParser = defaultParser;
    }

    public String parseDefaults(String message, Map<String, String> defaults) {
        if (!hasExtra() || defaults.size() == 0)
            return message;

        return defaultParser.parse(message, defaults);
    }

    @Contract("_,!null,_ -> !null")
    public Message parseExtra(OfflinePlayer player, Message message, Object[] extra) {
        if (parser == null || extra.length == 0)
            return message;

        return parser.parse(player, message, extra);
    }

    public boolean hasExtra() {
        return parser != null && defaultParser != null;
    }
}