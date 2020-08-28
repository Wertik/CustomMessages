package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.custommessages.CustomMessagesPlugin;
import space.devport.wertik.custommessages.commands.CommandUtils;
import space.devport.wertik.custommessages.system.struct.MessageType;
import space.devport.wertik.custommessages.system.struct.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetSubCommand extends SubCommand {

    private final CustomMessagesPlugin plugin;

    public SetSubCommand() {
        super("set");
        this.plugin = CustomMessagesPlugin.getInstance();
        this.preconditions = new Preconditions()
                .permissions("custommessages.set");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
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

        User user = plugin.getUserManager().getUser(target);
        user.setMessage(type, args[1]);

        language.getPrefixed(target == sender ? "Commands.Set.Done" : "Commands.Set.Done-Others")
                .replace("%type%", type.toString().toLowerCase())
                .replace("%message%", args[1])
                .replace("%player%", target == sender ? "you" : target.getName())
                .send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return Arrays.stream(MessageType.values())
                    .map(t -> t.toString().toLowerCase())
                    .collect(Collectors.toList());
        } else if (args.length == 1) {
            return plugin.getMessageManager().getMessages(MessageType.fromString(args[0]));
        } else if (args.length == 2 && sender.hasPermission("custommmessages.set.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
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