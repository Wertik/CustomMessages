package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.CommandParser;
import space.devport.wertik.custommessages.commands.MessageSubCommand;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PreviewSubCommand extends MessageSubCommand {

    public PreviewSubCommand(MessagePlugin plugin) {
        super(plugin, "preview");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        MessageType type = plugin.getCommandParser().parseType(sender, args[0]);

        Player target;
        if (args.length > 1) {
            target = plugin.getCommandParser().parsePlayer(sender, args[1]);

            if (target == null) return CommandResult.FAILURE;

            if (!sender.hasPermission("custommessages.preview.others")) return CommandResult.NO_PERMISSION;
        } else {
            if (!(sender instanceof Player)) return CommandResult.NO_CONSOLE;

            target = (Player) sender;
        }

        String message = plugin.getMessageManager().getFormattedMessage(target, type);
        language.getPrefixed("Commands.Preview.Done")
                .replace("%player%", target.getName())
                .replace("%type%", type.toString().toLowerCase())
                .replace("%message%", message == null ? "&cNone" : type.parseDefaults(message, plugin.getCommandParser().obtainDefaults(type)))
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public @Nullable List<String> requestTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 1)
            return Arrays.stream(MessageType.values())
                    .map(t -> t.toString().toLowerCase())
                    .collect(Collectors.toList());
        else if (args.length == 2 && sender.hasPermission("custommmessages.preview.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% preview <type> (player)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Preview a message.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(1, 2);
    }
}