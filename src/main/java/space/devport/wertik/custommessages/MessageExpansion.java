package space.devport.wertik.custommessages;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.callbacks.ExceptionCallback;
import space.devport.utils.utility.ParseUtil;
import space.devport.wertik.custommessages.system.message.type.MessageType;
import space.devport.wertik.custommessages.system.user.User;

@RequiredArgsConstructor
public class MessageExpansion extends PlaceholderExpansion {

    private final MessagePlugin plugin;

    /*
     * %custommessages_<type>_(formatted)%
     * */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        if (player == null)
            return "no-player";

        String[] args = params.split("_");

        if (args.length < 1)
            return "not-enough-params";

        MessageType type = ParseUtil.parseEnumHandled(args[0], MessageType.class, ExceptionCallback.IGNORE);

        if (type == null)
            return "invalid-type";

        User user = plugin.getUserManager().getUser(player);

        if (user == null) {
            plugin.getUserManager().getOrLoadUser(player.getUniqueId());
            return "no-record";
        }

        if (args.length < 2)
            return user.getMessage(type);

        if (args[1].equalsIgnoreCase("formatted")) {
            String msg = plugin.getMessageManager().getFormattedMessage(player, type);
            return msg != null ? msg : "none";
        }

        return "invalid_params";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "custommessages";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
}