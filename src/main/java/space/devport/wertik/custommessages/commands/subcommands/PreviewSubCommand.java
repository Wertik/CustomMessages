package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.CommandUtils;
import space.devport.wertik.custommessages.system.struct.MessageType;

public class PreviewSubCommand extends SubCommand {

    private final MessagePlugin plugin;

    public PreviewSubCommand() {
        super("preview");
        this.plugin = MessagePlugin.getInstance();
        this.preconditions = new Preconditions()
                .permissions("custommessages.preview");
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        MessageType type = CommandUtils.parseType(sender, args[0]);

        Player target;
        if (args.length > 1) {
            target = CommandUtils.parsePlayer(sender, args[1]);

            if (target == null) return CommandResult.FAILURE;

            if (!sender.hasPermission("custommessages.preview.others")) return CommandResult.NO_PERMISSION;
        } else {
            if (!(sender instanceof Player)) return CommandResult.NO_CONSOLE;

            target = (Player) sender;
        }

        language.getPrefixed("Commands.Preview.Done")
                .replace("%player%", target.getName())
                .replace("%type%", type.toString().toLowerCase())
                .replace("%message%", plugin.getMessageManager().parseMessage(target, type))
                .send(sender);
        return CommandResult.SUCCESS;
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