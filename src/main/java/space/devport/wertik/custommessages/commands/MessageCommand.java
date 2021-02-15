package space.devport.wertik.custommessages.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.commands.MainCommand;
import space.devport.dock.commands.struct.CommandResult;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.text.message.Message;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.commands.subcommands.MenuSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.PreviewSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.ReloadSubCommand;
import space.devport.wertik.custommessages.commands.subcommands.SetSubCommand;
import space.devport.wertik.custommessages.system.message.type.MessageType;

public class MessageCommand extends MainCommand {

    @SuppressWarnings("deprecation")
    public MessageCommand(MessagePlugin plugin) {
        super(plugin, "custommessages");

        withSubCommand(new ReloadSubCommand(plugin));
        withSubCommand(new SetSubCommand(plugin));
        withSubCommand(new PreviewSubCommand(plugin));
        withSubCommand(new MenuSubCommand(plugin));

        withSubCommand(plugin.buildSubCommand("show")
                .withDefaultUsage("/%label% show (player)")
                .withDefaultDescription("Show what messages the player has selected.")
                .withRange(0, 1)
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

                    plugin.getUserManager().getOrCreateUser(target)
                            .thenAcceptAsync(user -> {
                                for (MessageType type : plugin.getMessageManager().getEnabledTypes()) {
                                    header.append(lineFormat.clone()
                                            .replace("%type%", type.toString().toLowerCase())
                                            .replace("%message%", user.getMessage(type))
                                            .replace("%preview%", type.parseDefaults(plugin.getMessageManager().getFormattedMessage(target, type),
                                                    plugin.getCommandParser().obtainDefaults(type))));
                                }

                                header.replace("%player%", target.getName())
                                        .send(sender);
                            });
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