package space.devport.wertik.custommessages.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import space.devport.utils.DevportListener;
import space.devport.wertik.custommessages.MessagePlugin;

public class PlayerListener extends DevportListener {

    private final MessagePlugin plugin;

    public PlayerListener(MessagePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getUserManager().loadUser(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getUserManager().saveUser(event.getPlayer().getUniqueId());
    }
}
