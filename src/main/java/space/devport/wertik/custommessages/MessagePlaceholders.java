package space.devport.wertik.custommessages;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.custommessages.system.struct.MessageType;

@RequiredArgsConstructor
public class MessagePlaceholders extends PlaceholderExpansion {

    private final MessagePlugin plugin;

    /*
     * %custommessages_message_(formatted)%
     * */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        if (player == null) return "no_player";

        String[] args = params.split("_");

        if (args.length < 2) return "not_enough_params";

        MessageType type = MessageType.fromString(args[0]);

        if (type == null) return "invalid_type";

        if (args[0].toLowerCase().equals("message")) {
            if (args.length > 2)
                if (args[2].equalsIgnoreCase("formatted"))
                    return plugin.getMessageManager().parseMessage(player, type) != null ? plugin.getMessageManager().parseMessage(player, type) : "none";
                else return "invalid_param";
            return plugin.getUserManager().getUser(player).getMessage(type);
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