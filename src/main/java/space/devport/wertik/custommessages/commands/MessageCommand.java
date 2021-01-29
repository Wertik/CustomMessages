package space.devport.wertik.custommessages.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.commands.MainCommand;
import space.devport.utils.commands.struct.CommandResult;
import space.devport.utils.text.language.LanguageManager;
import space.devport.utils.text.message.Message;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.subcommands.MenuSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.PreviewSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.ReloadSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.SetSubCommand;
import space.devport.wertik.custommessages.system.struct.MessageType;
import space.devport.wertik.custommessages.system.struct.User;

import java.util.ArrayList;
import java.util.Collections;

public class MessageCommand extends MainCommand {

    @SuppressWarnings("deprecation")
    public MessageCommand(MessagePlugin plugin) {
        super(plugin, "custommessages");

        withSubCommand(new ReloadSubCommand(plugin));
        withSubCommand(new SetSubCommand(plugin));
        withSubCommand(new PreviewSubCommand(plugin));
        withSubCommand(new MenuSubCommand(plugin));

        withSubCommand(plugin.buildSubCommand("show")
                .withRange(0, 1)
                .withDefaultUsage("/%label% show (player)")
                .withDefaultDescription("Show what messages the player has selected.")
                .withExecutor((sender, label, args) -> {
                    Message header = plugin.getManager(LanguageManager.class).get("Commands.Show.Header");
                    Message lineFormat = plugin.getManager(LanguageManager.class).get("Commands.Show.Line-Format");

                    OfflinePlayer target;
                    if (args.length > 0)
                        target = Bukkit.getOfflinePlayer(args[0]);
                    else {
                        if (!(sender instanceof Player))
                            return CommandResult.NO_CONSOLE;

                        target = (Player) sender;
                    }

                    User user = plugin.getUserManager().getOrCreateUser(target);

                    for (MessageType type : plugin.getMessageManager().getEnabledTypes()) {
                        header.append(lineFormat
                                .replace("type", type.toString().toLowerCase())
                                .replace("message", user.getMessage(type))
                                .replace("preview", plugin.getMessageManager().getFormattedMessage(target, type)));
                    }

                    header.send(sender);
                    return CommandResult.SUCCESS;
                }));
    }

    @Override
    protected @NotNull CommandResult perform(@NotNull CommandSender sender, @NotNull String label, String[] args) {
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