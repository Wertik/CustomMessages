package space.devport.wertik.custommessages;

import lombok.Getter;
import lombok.extern.java.Log;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.utility.DependencyUtil;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.custommessages.commands.CommandParser;
import space.devport.wertik.custommessages.commands.MessageCommand;
import space.devport.wertik.custommessages.listeners.ListenerRegistry;
import space.devport.wertik.custommessages.listeners.PlayerListener;
import space.devport.wertik.custommessages.system.message.MessageManager;
import space.devport.wertik.custommessages.system.user.UserManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Log
public class MessagePlugin extends DevportPlugin {

    @Getter
    private static MessagePlugin instance;

    @Getter
    private final MessageManager messageManager = new MessageManager(this);

    @Getter
    private final UserManager userManager = new UserManager(this);

    @Getter
    private final ListenerRegistry listenerRegistry = new ListenerRegistry(this);

    @Getter
    private CommandParser commandParser;

    private MessageExpansion expansion;

    @Getter
    private NumberFormat numberFormat;

    @Override
    public void onPluginEnable() {
        MessagePlugin.instance = this;

        this.commandParser = new CommandParser(this);

        messageManager.load();
        messageManager.loadOptions();

        loadOptions();

        new MessageLanguage(this).register();
        addListener(new PlayerListener(this));

        userManager.initializeStorage().thenRun(() -> {
            userManager.load();

            listenerRegistry.registerListeners();

            registerMainCommand(new MessageCommand(this));

            registerPlaceholders();
        });
    }

    private void unregisterPlaceholders() {

        if (this.expansion == null)
            return;

        // Attempt to unregister expansion
        if (VersionUtil.compareVersions("2.10.9", PlaceholderAPIPlugin.getInstance().getDescription().getVersion()) > -1 &&
                expansion.isRegistered()) {

            expansion.unregister();
            this.expansion = null;
            log.log(DebugLevel.DEBUG, "Unregistered placeholder expansion.");
        }
    }

    private void registerPlaceholders() {

        if (!DependencyUtil.isEnabled("PlaceholderAPI"))
            return;

        unregisterPlaceholders();

        this.expansion = new MessageExpansion(this);
        expansion.register();
        log.info("Found PlaceholderAPI! &aRegistered expansion.");
    }

    @Override
    public void onPluginDisable() {
        HandlerList.unregisterAll(this);
        unregisterPlaceholders();

        userManager.save();
    }

    private void loadOptions() {
        this.numberFormat = new DecimalFormat(getConfiguration().getString("number-format", "#.##"));
    }

    @Override
    public void onReload() {
        listenerRegistry.unregisterAll();

        commandParser.emptyCache();

        loadOptions();

        messageManager.load();
        messageManager.loadOptions();

        // Reload the storage when a type change occurs.
        if (userManager.loadStorageType() != userManager.getStorageType()) {
            userManager.save().thenRun(() ->
                    userManager.initializeStorage().thenRun(() -> {
                        userManager.load();
                        listenerRegistry.registerListeners();
                    }));
        } else {
            listenerRegistry.registerListeners();
        }

        registerPlaceholders();
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.MENUS, UsageFlag.CUSTOMISATION, UsageFlag.LANGUAGE};
    }
}
