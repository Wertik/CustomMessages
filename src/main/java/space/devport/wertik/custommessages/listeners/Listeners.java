package space.devport.wertik.custommessages.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.system.MessageManager;
import space.devport.wertik.custommessages.system.struct.MessageType;

import java.util.HashMap;
import java.util.Map;

public class Listeners {

    private final MessagePlugin plugin;
    private final MessageManager messageManager;

    public Listeners(MessagePlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    private final Map<MessageType, Listener> registeredListeners = new HashMap<>();

    public void registerListeners() {

        registerListener(MessageType.JOIN, new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                event.setJoinMessage(handle(event.getPlayer(), MessageType.JOIN));
            }
        });

        registerListener(MessageType.LEAVE, new Listener() {
            @EventHandler
            public void onLeave(PlayerQuitEvent event) {
                event.setQuitMessage(handle(event.getPlayer(), MessageType.LEAVE));
            }

            @EventHandler
            public void onKick(PlayerKickEvent event) {
                String msg = handle(event.getPlayer(), MessageType.LEAVE);
                // Both kick and leave message are sent when a player is kicked.
                // When we are sending a leave message, avoid the kick one.
                if (msg != null)
                    event.setLeaveMessage("");
            }
        });

        plugin.getConsoleOutput().info("Registered " + this.registeredListeners.size() + " listener(s)...");
    }

    public void registerListener(MessageType type, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        this.registeredListeners.put(type, listener);
    }

    @Nullable
    private String handle(@NotNull Player player, @NotNull MessageType type) {
        return messageManager.getFormattedMessage(player, type);
    }
}