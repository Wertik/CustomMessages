package space.devport.wertik.custommessages.system.message.type;

import org.bukkit.OfflinePlayer;
import space.devport.utils.text.message.Message;

public interface ExtraParser {

    // Apply extra to the output message.
    Message parse(OfflinePlayer player, Message message, Object[] extra);
}
