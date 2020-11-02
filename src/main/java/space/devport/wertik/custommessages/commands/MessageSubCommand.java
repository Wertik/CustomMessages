package space.devport.wertik.custommessages.commands;

import org.jetbrains.annotations.Nullable;
import space.devport.utils.commands.SubCommand;
import space.devport.utils.commands.struct.ArgumentRange;
import space.devport.wertik.custommessages.MessagePlugin;

public abstract class MessageSubCommand extends SubCommand {

    protected final MessagePlugin plugin;

    public MessageSubCommand(MessagePlugin plugin, String name) {
        super(name);
        setPermissions();
        this.plugin = plugin;
    }

    @Override
    public @Nullable
    abstract String getDefaultUsage();

    @Override
    public @Nullable
    abstract String getDefaultDescription();

    @Override
    public @Nullable
    abstract ArgumentRange getRange();
}
