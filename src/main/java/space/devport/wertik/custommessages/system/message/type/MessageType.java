package space.devport.wertik.custommessages.system.message.type;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import space.devport.utils.text.message.Message;

public enum MessageType {

    JOIN,
    LEAVE,
    KILL((message, extra) -> {
        for (Object obj : extra) {
            if (obj instanceof Player) {
                Player player = (Player) obj;
                message.replace("%killer%", player.getName())
                        .replace("%killerHealth%", player.getHealth());
            }
        }
        return message;
    }, (message, defaults) -> {
        if (defaults.length < 2)
            return message;

        return message.replaceAll("(?i)%killer%", defaults[0])
                .replaceAll("(?i)%killerHealth%", defaults[1]);
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

    public String parseDefaults(String message, String... defaults) {
        if (!hasExtra() || defaults.length == 0)
            return message;

        return defaultParser.parse(message, defaults);
    }

    @Contract("!null,_ -> !null")
    public Message parseExtra(Message message, Object[] extra) {
        if (parser == null || extra.length == 0)
            return message;

        return parser.parse(message, extra);
    }

    public boolean hasExtra() {
        return parser != null && defaultParser != null;
    }
}