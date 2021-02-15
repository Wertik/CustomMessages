package space.devport.wertik.custommessages.gui;

import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import space.devport.dock.CustomisationManager;
import space.devport.dock.menu.Menu;
import space.devport.dock.menu.MenuBuilder;
import space.devport.dock.menu.item.MatrixItem;
import space.devport.dock.menu.item.MenuItem;
import space.devport.dock.text.language.LanguageManager;
import space.devport.dock.text.placeholders.Placeholders;
import space.devport.wertik.custommessages.MessagePlugin;
import space.devport.wertik.custommessages.sounds.SoundType;
import space.devport.wertik.custommessages.system.message.type.MessageType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log
public class MessageMenu extends Menu {

    private final MessagePlugin plugin;

    private final Player player;

    private final MessageType type;

    private final int slotsPerPage;

    private int page;

    public MessageMenu(MessagePlugin plugin, Player player, MessageType type, int page) {
        super("custommessages_preview", plugin);
        this.player = player;
        this.type = type;
        this.plugin = plugin;

        this.page = page;
        this.slotsPerPage = countMatrixSlots(plugin.getManager(CustomisationManager.class).getMenu("message-overview").construct());

        open();
    }

    public MessageMenu(MessagePlugin plugin, Player player, MessageType type) {
        this(plugin, player, type, 1);
    }

    private int countMatrixSlots(MenuBuilder menuBuilder) {
        int count = 0;
        for (String line : menuBuilder.getBuildMatrix()) {
            for (char c : line.toCharArray())
                if (c == 'm')
                    count++;
        }
        return count;
    }

    public CompletableFuture<Void> build() {
        return plugin.getUserManager().getOrCreateUser(player).thenAcceptAsync(user -> {
            MenuBuilder template = plugin.getManager(CustomisationManager.class).getMenu("message-overview");

            if (template == null) {
                log.warning(() -> "Menu `message-overview` is missing in customisation.yml. Delete the file and let it regenerate.");
                return;
            }

            MenuBuilder menuBuilder = template.construct();

            String usedMessage = user.getMessage(type);

            MatrixItem messageMatrix = menuBuilder.getMatrixItem('m');
            messageMatrix.clear();

            MenuItem messageItem = new MenuItem(menuBuilder.getItem("message-item"));
            MenuItem messageItemTaken = new MenuItem(menuBuilder.getItem("message-item-taken"));

            Placeholders placeholders = plugin.obtainPlaceholders()
                    .add("type", type.toString().toLowerCase())
                    .add("type_display", plugin.getManager(LanguageManager.class).get(String.format("Types.%s", type.toString())))
                    .add("player", player.getName())
                    .add("message_used", usedMessage);

            menuBuilder.placeholders(placeholders);

            List<String> messages = plugin.getMessageManager().getMessages(type);

            for (int i = (this.page - 1) * slotsPerPage; i < messages.size() && i < slotsPerPage * this.page; i++) {

                String key = messages.get(i);
                boolean used = usedMessage.equals(key);

                MenuItem item;
                if (used) {
                    item = new MenuItem(messageItemTaken);
                    messageItemTaken.setClickAction(click -> plugin.getSoundRegistry().play(player, SoundType.MENU_USED));
                } else {
                    item = new MenuItem(messageItem);
                    item.setClickAction((itemClick) -> {
                        user.setMessage(type, key);
                        plugin.getSoundRegistry().play(player, SoundType.MENU_PICK);
                        build().thenRun(this::reload);
                    });
                }

                item.getPrefab().getPlaceholders()
                        .add("%message_name%", key)
                        .add("%message_formatted%", plugin.getMessageManager().obtainPreview(type, player, key));

                messageMatrix.addItem(item);
            }

            for (MenuItem i : messageMatrix.getMenuItems()) {
                log.fine(() -> i.getPrefab().getPlaceholders().getPlaceholderCache().toString());
            }

            // Page control and close

            if (menuBuilder.getItem("close") != null)
                menuBuilder.getItem("close").setClickAction((itemClick -> close()));

            // Next
            if (this.page < this.maxPage() && menuBuilder.getItem("page-next") != null)
                menuBuilder.getMatrixItem('n').getItem("page-next").setClickAction(itemClick -> {
                    incPage();
                    build().thenRun(this::reload);
                });
            else
                menuBuilder.removeMatrixItem('n');

            // Previous
            if (this.page > 1 && menuBuilder.getItem("page-previous") != null)
                menuBuilder.getMatrixItem('p').getItem("page-previous").setClickAction(itemClick -> {
                    decPage();
                    build().thenRun(this::reload);
                });
            else
                menuBuilder.removeMatrixItem('p');

            setMenuBuilder(menuBuilder.construct());
        }).exceptionally(e -> {
            log.severe(() -> String.format("Failed to open message menu for %s: %s", player.getName(), e.getMessage()));
            e.printStackTrace();
            return null;
        });
    }

    public void open() {
        build().thenRun(() -> {
            // Synchronize
            Bukkit.getScheduler().runTask(plugin, () -> {
                open(player);
                plugin.getSoundRegistry().play(player, SoundType.MENU_OPEN);
            });
        });
    }

    @Override
    public void onClose() {
        plugin.getSoundRegistry().play(player, SoundType.MENU_CLOSE);
    }

    private int maxPage() {
        return plugin.getMessageManager().getMessages(type).size() / slotsPerPage;
    }

    private void incPage() {
        this.page++;
    }

    private void decPage() {
        this.page--;
    }
}