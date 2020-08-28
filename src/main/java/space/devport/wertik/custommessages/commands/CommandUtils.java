package space.devport.wertik.custommessages.commands;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.devport.wertik.custommessages.CustomMessagesPlugin;
import space.devport.wertik.custommessages.system.struct.MessageType;

@UtilityClass
public class CommandUtils {

    public MessageType parseType(CommandSender sender, String arg) {
        try {
            return MessageType.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        CustomMessagesPlugin.getInstance().getLanguageManager().getPrefixed("Commands.Invalid-Type")
                .replace("%param%", arg)
                .send(sender);
        return null;
    }

    public Player parsePlayer(CommandSender sender, String arg) {
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            CustomMessagesPlugin.getInstance().getLanguageManager().getPrefixed("Commands.Invalid-Player")
                    .replace("%param%", arg)
                    .send(sender);
            return null;
        }
        return player;
    }
}