package space.devport.wertik.custommessages.listeners;

import lombok.extern.java.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.system.message.MessageManager;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.HashMap;
import java.util.Map;

@Log
public class ListenerRegistry {

    private final MessagePlugin plugin;
    private final MessageManager messageManager;

    public ListenerRegistry(MessagePlugin plugin) {
        this.plugin = plugin;
        this.messageManager = plugin.getMessageManager();
    }

    private final Map<MessageType, Listener> registeredListeners = new HashMap<>();

    public void registerListeners() {

        if (messageManager.isEnabled(MessageType.JOIN))
            registerListener(MessageType.JOIN, new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    event.setJoinMessage(handle(event.getPlayer(), MessageType.JOIN));
                }
            });

        if (messageManager.isEnabled(MessageType.LEAVE))
            registerListener(MessageType.LEAVE, new Listener() {
                @EventHandler
                public void onLeave(PlayerQuitEvent event) {
                    event.setQuitMessage(handle(event.getPlayer(), MessageType.LEAVE, event.getPlayer().getWorld()));
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

        if (messageManager.isEnabled(MessageType.KILL))
            registerListener(MessageType.KILL, new Listener() {
                @EventHandler(priority = EventPriority.HIGH)
                public void onKill(PlayerDeathEvent event) {
                    Player player = event.getEntity();
                    Player killer = event.getEntity().getKiller();

                    if (killer == null)
                        return;

                    event.setDeathMessage(handle(killer, MessageType.KILL, player));
                }
            });

        log.info("Registered " + this.registeredListeners.size() + " listener(s)...");
    }

    public void registerListener(MessageType type, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        this.registeredListeners.put(type, listener);
    }

    public void unregisterAll() {
        for (Listener listener : registeredListeners.values()) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Nullable
    private String handle(@NotNull Player player, @NotNull MessageType type, Object... extra) {
        String message = messageManager.getFormattedMessage(player, type, extra);
        return messageManager.getPosition().display(message);
    }
}