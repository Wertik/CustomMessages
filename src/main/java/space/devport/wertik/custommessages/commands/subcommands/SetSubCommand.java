package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.callbacks.ExceptionCallback;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.utility.ParseUtil;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.CommandUtils;
import space.devport.wertik.custommessages.commands.MessageSubCommand;
import space.devport.wertik.custommessages.system.struct.MessageType;
import space.devport.wertik.custommessages.system.struct.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetSubCommand extends MessageSubCommand {

    public SetSubCommand(MessagePlugin plugin) {
        super(plugin, "set");
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        MessageType type = CommandUtils.parseType(sender, args[0]);

        if (type == null) return CommandResult.FAILURE;

        if (!plugin.getMessageManager().getMessages(type).contains(args[1])) {
            language.getPrefixed("Commands.Invalid-Type")
                    .replace("%param%", args[1])
                    .replace("%type%", type.toString().toLowerCase())
                    .send(sender);
            return CommandResult.FAILURE;
        }

        Player target;
        if (args.length > 2) {
            target = CommandUtils.parsePlayer(sender, args[2]);

            if (target == null) return CommandResult.FAILURE;

            if (!sender.hasPermission("custommessages.others")) return CommandResult.NO_PERMISSION;
        } else {
            if (!(sender instanceof Player)) return CommandResult.NO_CONSOLE;

            target = (Player) sender;
        }

        User user = plugin.getUserManager().getOrCreateUser(target);
        user.setMessage(type, args[1]);

        language.getPrefixed(target == sender ? "Commands.Set.Done" : "Commands.Set.Done-Others")
                .replace("%type%", type.toString().toLowerCase())
                .replace("%message%", args[1])
                .replace("%player%", target == sender ? "you" : target.getName())
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Arrays.stream(MessageType.values())
                    .map(t -> t.toString().toLowerCase())
                    .collect(Collectors.toList());
        } else if (args.length == 1) {
            MessageType type = ParseUtil.parseEnumHandled(args[0], MessageType.class, ExceptionCallback.IGNORE);

            if (type == null)
                return Collections.emptyList();

            return plugin.getMessageManager().getMessages(type);
        } else if (args.length == 2 && sender.hasPermission("custommmessages.set.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% set <type> <message> (player)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Set a message.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(2, 3);
    }
}