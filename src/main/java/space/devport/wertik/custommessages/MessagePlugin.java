package space.devport.wertik.custommessages;

import lombok.Getter;
import lombok.extern.java.Log;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import space.devport.utils.DevportPlugin;
import space.devport.utils.UsageFlag;
import space.devport.utils.logging.DebugLevel;
import space.devport.utils.utility.VersionUtil;
import space.devport.wertik.custommessages.commands.MessageCommand;
import space.devport.wertik.custommessages.listeners.PlayerListener;
import space.devport.wertik.custommessages.system.MessageManager;
import space.devport.wertik.custommessages.system.UserManager;

@Log
public class MessagePlugin extends DevportPlugin {

    private MessagePlaceholders placeholders;

    @Getter
    private MessageManager messageManager = new MessageManager(this);

    @Getter
    private UserManager userManager = new UserManager(this);

    @Getter
    private PlayerListener playerListener;

    public static MessagePlugin getInstance() {
        return getPlugin(MessagePlugin.class);
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public void onPluginEnable() {
        messageManager.load();
        messageManager.loadOptions();

        userManager.load();

        setupPlaceholders();

        this.playerListener = new PlayerListener(this);
        playerListener.registerListeners();

        new MessageLanguage(this).register();

        registerMainCommand(new MessageCommand(this));
    }

    private void unregisterPlaceholders() {

        if (this.placeholders == null)
            return;

        // Attempt to unregister expansion
        if (VersionUtil.compareVersions("2.10.9", PlaceholderAPIPlugin.getInstance().getDescription().getVersion()) > -1 &&
                placeholders.isRegistered()) {

            placeholders.unregister();
            this.placeholders = null;
            log.log(DebugLevel.DEBUG, "Unregistered placeholder expansion.");
        }
    }

    private void setupPlaceholders() {
        if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return;
        }

        unregisterPlaceholders();

        this.placeholders = new MessagePlaceholders(this);
        placeholders.register();
        log.info("Found PlaceholderAPI! Registered expansion.");
    }

    @Override
    public void onPluginDisable() {
        HandlerList.unregisterAll(this);
        unregisterPlaceholders();

        userManager.save();
    }

    @Override
    public void onReload() {
        messageManager.load();
        messageManager.loadOptions();

        setupPlaceholders();
    }

    @Override
    public UsageFlag[] usageFlags() {
        return new UsageFlag[]{UsageFlag.COMMANDS, UsageFlag.CONFIGURATION, UsageFlag.MENUS, UsageFlag.CUSTOMISATION, UsageFlag.LANGUAGE};
    }
}
