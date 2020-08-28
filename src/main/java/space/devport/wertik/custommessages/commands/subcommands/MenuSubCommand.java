package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.wertik.custommessages.commands.CommandUtils;
import space.devport.wertik.custommessages.gui.MessageMenu;
import space.devport.wertik.custommessages.system.struct.MessageType;

public class MenuSubCommand extends SubCommand {

    public MenuSubCommand() {
        super("menu");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {

        MessageType type = CommandUtils.parseType(sender, args[0]);

        if (type == null) return CommandResult.FAILURE;

        new MessageMenu((Player) sender, type).open((Player) sender);
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