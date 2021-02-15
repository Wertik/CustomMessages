package space.devport.wertik.custommessages.system.message;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import space.devport.dock.lib.xseries.messages.ActionBar;
import space.devport.dock.lib.xseries.messages.Titles;
import space.devport.dock.util.StringUtil;
import space.devport.wertik.custommessages.MessagePlugin;

public enum MessagePosition {

    TEXT(text -> {
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(text));
    }),

    TITLE(text -> {
        String[] arr = text.split("\n");
        String title = arr[0];
        String subtitle = arr.length > 1 ? arr[1] : "";

        MessagePlugin plugin = MessagePlugin.getInstance();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Titles.sendTitle(player,
                    plugin.getConfig().getInt("title.in", 20),
                    plugin.getConfig().getInt("title.stay", 20),
                    plugin.getConfig().getInt("title.out", 20),
                    title,
                    subtitle);
        }
    }),

    ACTION_BAR(text -> {
        String colored = StringUtil.color(text);
        MessagePlugin plugin = MessagePlugin.getInstance();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActionBar.sendActionBar(plugin, player, colored, plugin.getConfig().getInt("action-bar.duration", 20));
        }
    });

    private final MessageDisplay handler;

    MessagePosition(MessageDisplay handler) {
        this.handler = handler;
    }

    MessagePosition() {
        this.handler = text -> {
        };
    }

    private interface MessageDisplay {
        void display(String text);
    }

    public static MessagePosition fromString(@Nullable String str, MessagePosition def) {
        if (Strings.isNullOrEmpty(str))
            return def;

        try {
            return valueOf(str.trim());
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public void display(String text) {
        handler.display(text);
    }
}