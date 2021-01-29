package space.devport.wertik.custommessages.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.commands.struct.Preconditions;
import space.devport.utils.text.language.LanguageManager;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.MessageSubCommand;
import space.devport.wertik.custommessages.gui.MessageMenu;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MenuSubCommand extends MessageSubCommand {

    public MenuSubCommand(MessagePlugin plugin) {
        super(plugin, "menu");
        this.preconditions = new Preconditions()
                .playerOnly();
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {

        MessageType type = plugin.getCommandParser().parseType(sender, args[0]);

        if (type == null)
            return CommandResult.FAILURE;

        Player target;
        if (args.length > 1) {
            target = plugin.getCommandParser().parsePlayer(sender, args[1]);

            if (target == null) return CommandResult.FAILURE;

            if (!sender.hasPermission("custommessages.preview.others")) return CommandResult.NO_PERMISSION;
        } else {
            if (!(sender instanceof Player)) return CommandResult.NO_CONSOLE;

            target = (Player) sender;
        }

        new MessageMenu(plugin, target, type).open(target);
        if (target != sender)
            plugin.getManager(LanguageManager.class).getPrefixed("Commands.Menu.Done-Others")
                    .replace("%type%", type.toString())
                    .replace("%player%", target.getName());
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> requestTabComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getMessageManager().getEnabledTypes().stream()
                    .map(t -> t.toString().toLowerCase())
                    .collect(Collectors.toList());
        } else if (args.length == 2 && sender.hasPermission("custommmessages.preview.others")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
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