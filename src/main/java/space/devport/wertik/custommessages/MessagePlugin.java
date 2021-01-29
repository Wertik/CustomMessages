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
import space.devport.wertik.custommessages.commands.MessageCommand;
import space.devport.wertik.custommessages.listeners.PlayerListener;
import space.devport.wertik.custommessages.system.MessageManager;
import space.devport.wertik.custommessages.system.UserManager;

@Log
public class MessagePlugin extends DevportPlugin {

    @Getter
    private static MessagePlugin instance;

    @Getter
    private final MessageManager messageManager = new MessageManager(this);

    @Getter
    private final UserManager userManager = new UserManager(this);

    @Getter
    private final PlayerListener playerListener = new PlayerListener(this);

    private MessageExpansion expansion;

    @Override
    public void onPluginEnable() {
        MessagePlugin.instance = this;

        messageManager.load();
        messageManager.loadOptions();

        userManager.load();

        playerListener.registerListeners();

        new MessageLanguage(this).register();

        registerMainCommand(new MessageCommand(this));

        registerPlaceholders();
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

    @Override
    public void onReload() {
        playerListener.unregisterAll();

        messageManager.load();
        messageManager.loadOptions();

        playerListener.registerListeners();

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
