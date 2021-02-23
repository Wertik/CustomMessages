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
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.sounds.SoundPlayType;
import space.devport.wertik.custommessages.sounds.SoundType;
import space.devport.wertik.custommessages.system.message.MessageManager;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.HashMap;
import java.util.Iterator;
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
        boolean vanishSupport = plugin.getConfig().getBoolean("vanish-support", true);

        if (messageManager.isEnabled(MessageType.JOIN))
            registerListener(MessageType.JOIN, new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    event.setJoinMessage(null);
                    if (vanishSupport && !isVanished(event.getPlayer())) {
                        handle(event.getPlayer(), MessageType.JOIN, SoundType.MESSAGE_JOIN);
                    }
                }
            });

        if (messageManager.isEnabled(MessageType.LEAVE))
            registerListener(MessageType.LEAVE, new Listener() {
                @EventHandler
                public void onLeave(PlayerQuitEvent event) {
                    event.setQuitMessage(null);
                    if (vanishSupport && !isVanished(event.getPlayer())) {
                        handle(event.getPlayer(), MessageType.LEAVE, SoundType.MESSAGE_LEAVE, event.getPlayer().getWorld());
                    }
                }

                @EventHandler
                public void onKick(PlayerKickEvent event) {
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

                    event.setDeathMessage(null);
                    handle(killer, MessageType.KILL, SoundType.MESSAGE_KILL, player);

                    plugin.getSoundRegistry().get(SoundType.MESSAGE_KILL).ifPresent(s -> {
                        // Play the sound for both
                        if (s.getPlayType() == SoundPlayType.PLAYER) {
                            s.playForPlayer(killer);
                            s.playForPlayer(player);
                        } else
                            s.play(player);
                    });
                }
            });

        log.info(() -> "Registered " + registeredListeners.size() + " listener(s)...");
    }

    public void registerListener(MessageType type, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        this.registeredListeners.put(type, listener);
    }

    public void unregisterAll() {
        int count = registeredListeners.size();
        for (Iterator<Listener> iterator = registeredListeners.values().iterator(); iterator.hasNext(); ) {
            Listener listener = iterator.next();
            iterator.remove();

            HandlerList.unregisterAll(listener);
        }
        log.info(String.format("Unregistered %d listener(s)...", count));
    }

    private void handle(@NotNull Player player, @NotNull MessageType type, SoundType soundType, Object... extra) {
        messageManager.getFormattedMessage(player, type, extra).thenAcceptAsync(message -> {
            messageManager.getPosition().display(message);
            plugin.getSoundRegistry().play(player, soundType);
        });
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}