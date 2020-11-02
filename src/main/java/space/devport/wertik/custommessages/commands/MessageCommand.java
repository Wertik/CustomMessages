package space.devport.wertik.custommessages.commands;

import org.bukkit.command.CommandSender;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.subcommands.MenuSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.PreviewSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.ReloadSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.SetSubCommand;

public class MessageCommand extends MainCommand {

    public MessageCommand(MessagePlugin plugin) {
        super("custommessages");

        addSubCommand(new ReloadSubCommand(plugin));
        addSubCommand(new SetSubCommand(plugin));
        addSubCommand(new PreviewSubCommand(plugin));
        addSubCommand(new MenuSubCommand(plugin));
    }

    @Override
    protected CommandResult perform(CommandSender sender, String label, String[] args) {
        return super.perform(sender, label, args);
    }

    @Override
    public String getDefaultUsage() {
        return "/%label%";
    }

    @Override
    public String getDefaultDescription() {
        return "Displays this.";
    }
}