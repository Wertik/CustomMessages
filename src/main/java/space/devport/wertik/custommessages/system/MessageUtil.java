package space.devport.wertik.custommessages.system;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import space.devport.utils.text.StringUtil;

import java.util.UUID;

@UtilityClass
public class MessageUtil {

    public String formatMessage(String message, UUID uniqueID) {
        return formatMessage(message, Bukkit.getPlayer(uniqueID));
    }

    public String formatMessage(String message, Player player) {

        if (Strings.isNullOrEmpty(message)) return message;

        if (player != null) message = message.replaceAll("(?i)%player%", player.getName());

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
            message = PlaceholderAPI.setPlaceholders(player, message);

        return StringUtil.color(message);
    }
}