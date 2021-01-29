package space.devport.wertik.custommessages.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.utility.ParseUtil;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandParser {

    private final MessagePlugin plugin;
    private final LanguageManager language;

    public CommandParser(MessagePlugin plugin) {
        this.plugin = plugin;
        this.language = plugin.getManager(LanguageManager.class);
    }

    public MessageType parseType(CommandSender sender, String arg) {
        return ParseUtil.parseEnumHandled(arg, MessageType.class,
                e -> language.getPrefixed("Commands.Invalid-Type")
                        .replace("%param%", arg)
                        .send(sender));
    }

    public Player parsePlayer(CommandSender sender, String arg) {
        Player player = Bukkit.getPlayer(arg);
        if (player == null)
            language.getPrefixed("Commands.Invalid-Player")
                    .replace("%param%", arg)
                    .send(sender);
        return player;
    }

    public String[] obtainDefaults(MessageType type) {
        ConfigurationSection section = language.getLanguage().getFileConfiguration().getConfigurationSection(String.format("Type-Defaults.%s", type.toString().toLowerCase()));
        if (section == null)
            return new String[0];

        List<String> defaults = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            defaults.add(section.getString(key));
        }

        Collections.reverse(defaults);

        return defaults.toArray(new String[0]);
    }
}