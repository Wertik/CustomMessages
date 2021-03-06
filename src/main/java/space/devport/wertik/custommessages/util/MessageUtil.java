package space.devport.wertik.custommessages.util;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.util.DependencyUtil;
import space.devport.dock.util.StringUtil;

@UtilityClass
public class MessageUtil {

    public String formatMessage(String message, @Nullable OfflinePlayer player) {

        if (Strings.isNullOrEmpty(message))
            return message;

        if (player != null && player.getName() != null)
            message = message.replaceAll("(?i)%player%", player.getName());

        if (DependencyUtil.isEnabled("PlaceholderAPI"))
            message = PlaceholderAPI.setPlaceholders(player, message);

        return StringUtil.color(message);
    }
}