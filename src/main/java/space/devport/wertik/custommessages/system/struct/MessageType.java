package space.devport.wertik.custommessages.system.struct;

import lombok.Getter;
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
                message.getPlaceholders()
                        .add("killer", player.getName())
                        .add("killerHealth", player.getHealth());
            }
        }
        return message;
    });

    @Getter
    private final ExtraParser parser;

    MessageType() {
        this.parser = null;
    }

    MessageType(ExtraParser parser) {
        this.parser = parser;
    }

    @Contract("!null,_ -> !null")
    public Message parseExtra(Message message, Object[] extra) {
        if (parser == null)
            return message;

        return parser.parse(message, extra);
    }
}