package space.devport.wertik.custommessages.system.struct;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import space.devport.utils.text.StringUtil;
import space.devport.utils.xseries.messages.ActionBar;
import space.devport.utils.xseries.messages.Titles;
import space.devport.wertik.custommessages.MessagePlugin;

public enum MessagePosition {

    TEXT(text -> text),

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
        return null;
    }),

    ACTION_BAR(text -> {
        String colored = StringUtil.color(text);
        MessagePlugin plugin = MessagePlugin.getInstance();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActionBar.sendActionBar(plugin, player, colored, plugin.getConfig().getInt("action-bar.duration", 20));
        }
        return null;
    });

    private final MessageDisplay handler;

    MessagePosition(MessageDisplay handler) {
        this.handler = handler;
    }

    private interface MessageDisplay {
        String display(String text);
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

    @Nullable
    public String display(String text) {
        return handler.display(text);
    }
}