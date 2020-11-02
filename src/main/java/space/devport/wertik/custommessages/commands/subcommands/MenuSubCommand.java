package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.language.LanguageManager;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.CommandUtils;
import space.devport.wertik.custommessages.commands.MessageSubCommand;
import space.devport.wertik.custommessages.gui.MessageMenu;
import space.devport.wertik.custommessages.system.struct.MessageType;

public class MenuSubCommand extends MessageSubCommand {

    public MenuSubCommand(MessagePlugin plugin) {
        super(plugin, "menu");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        MessageType type = CommandUtils.parseType(sender, args[0]);

        if (type == null)
            return CommandResult.FAILURE;

        Player target;
        if (args.length > 1) {
            target = CommandUtils.parsePlayer(sender, args[1]);

            if (target == null) return CommandResult.FAILURE;

            if (!sender.hasPermission("custommessages.preview.others")) return CommandResult.NO_PERMISSION;
        } else {
            if (!(sender instanceof Player)) return CommandResult.NO_CONSOLE;

            target = (Player) sender;
        }

        new MessageMenu(target, type).open(target);
        if (target != sender)
            plugin.getManager(LanguageManager.class).getPrefixed("Commands.Menu.Done-Others")
                    .replace("%type%", type.toString())
                    .replace("%player%", target.getName());
        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull String getDefaultUsage() {
        return "/%label% menu <type> (player)";
    }

    @Override
    public @NotNull String getDefaultDescription() {
        return "Display message overview menu.";
    }

    @Override
    public @NotNull ArgumentRange getRange() {
        return new ArgumentRange(1, 2);
    }
}