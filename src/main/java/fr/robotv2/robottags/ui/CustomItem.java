package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.Messages;
import fr.robotv2.robottags.util.ColorUtil;
import fr.robotv2.robottags.util.ItemAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItem extends ItemStack {

    private static final Map<String, CustomItem> items = new HashMap<>();

    public static void clearItems() {
        items.clear();
    }

    @UnmodifiableView
    public static Collection<CustomItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public static void addCustomItem(CustomItem item) {
        items.put(item.getId().toLowerCase(), item);
    }

    public static CustomItem getCustomItem(String id) {
        return items.get(id.toLowerCase());
    }

    private final ConfigurationSection section;

    public CustomItem(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    public ItemStack getStackFor(Player player) {
        return ItemAPI.fromSection(this.section, player).build();
    }

    public String getId() {
        return this.section.getName();
    }

    public int getSlot() {
        return this.section.getInt("slot", 0);
    }

    public int getPage() {
        return this.section.getInt("page", -1);
    }

    public boolean isEnabled() {
        return this.section.getBoolean("enabled");
    }

    public void handleAction(ClickType type, Player player) {

        final String sectionId = type == ClickType.RIGHT ? "right-click" : "left-click";
        final List<String> commands = this.section.getStringList(sectionId);

        if(commands.isEmpty()) {
            return;
        }

        for(String command : commands) {

            final String prefix = command.split(" ")[0];

            command = command.substring(prefix.length());
            command = command.replace("%player%", player.getName());
            command = command.replace("%prefix%", Messages.PREFIX.getColored());

            switch (prefix) {
                case "[CONSOLE]" -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                case "[PLAYER]" -> Bukkit.dispatchCommand(player, command);
                case "[CLOSE]" -> player.closeInventory();
                case "[MESSAGE]" -> player.sendMessage(ColorUtil.color(command));
            }
        }
    }
}
