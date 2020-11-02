package space.devport.wertik.custommessages;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.event.HandlerList;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.custommessages.commands.MessageCommand;
import space.devport.wertik.custommessages.listeners.Listeners;
import space.devport.wertik.custommessages.system.MessageManager;
import space.devport.wertik.custommessages.system.UserManager;

public class MessagePlugin extends DevportPlugin {

    private MessagePlaceholders placeholders;

    @Getter
    private MessageManager messageManager;

    @Getter
    private UserManager userManager;

    @Getter
    private Listeners listeners;

    public static MessagePlugin getInstance() {
        return getPlugin(MessagePlugin.class);
    }

    @Override
    public void onPluginEnable() {
        this.messageManager = new MessageManager(this);
        this.userManager = new UserManager(this);

        messageManager.load();
        userManager.load();

        setupPlaceholders();

        this.listeners = new Listeners(this);
        listeners.registerListeners();

        new MessageLanguage(this);

        addMainCommand(new MessageCommand(this));
    }

    private void setupPlaceholders() {
        if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return;
        }

        if (placeholders == null)
            this.placeholders = new MessagePlaceholders(this);

        // Attempt to unregister expansion
        if (VersionUtil.compareVersions("2.10.9", PlaceholderAPIPlugin.getInstance().getDescription().getVersion()) > -1 &&
                this.placeholders.isRegistered()) {

            this.placeholders.unregister();
            consoleOutput.debug("Unregistered expansion");
        }

        this.placeholders.register();
        consoleOutput.info("Found PlaceholderAPI! Registered expansion.");
    }

    @Override
    public void onPluginDisable() {
        HandlerList.unregisterAll(this);
        this.userManager.save();
    }

    @Override
    public void onReload() {
        this.messageManager.load();
        setupPlaceholders();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.MENUS, UsageFlag.CUSTOMISATION, UsageFlag.LANGUAGE};
    }
}
