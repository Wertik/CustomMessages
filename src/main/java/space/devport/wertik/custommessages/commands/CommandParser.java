package space.devport.wertik.custommessages.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.util.ParseUtil;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {

    private final MessagePlugin plugin;
    private final LanguageManager language;

    private final Map<MessageType, Map<String, String>> cachedDefaults = new HashMap<>();

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

    public Map<String, String> obtainDefaults(MessageType type) {

        if (cachedDefaults.containsKey(type))
            return cachedDefaults.get(type);

        ConfigurationSection section = language.getLanguage().getFileConfiguration().getConfigurationSection(String.format("Type-Defaults.%s", type.toString().toLowerCase()));

        Map<String, String> out = new HashMap<>();

        if (section == null)
            return out;

        for (String key : section.getKeys(false)) {
            out.put(key, section.getString(key));
        }

        cachedDefaults.put(type, out);
        return out;
    }

    public void emptyCache() {
        cachedDefaults.clear();
    }
}