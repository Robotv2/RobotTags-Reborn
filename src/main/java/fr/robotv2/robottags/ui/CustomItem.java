package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.Messages;
import fr.robotv2.robottags.util.ColorUtil;
import fr.robotv2.robottags.util.ItemAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;

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

        final String name = section.getString("name");
        final List<String> lore = section.getStringList("lore");
        final String material = section.getString("material", "STONE");

        ItemStack result;

        if (material.startsWith("head-")) {
            result = ItemAPI.createSkull(material.substring("head-".length()));
        } else if (material.equalsIgnoreCase("player-head")) {
            result = ItemAPI.getHead(player.getUniqueId());
        } else {
            Material mat = Material.matchMaterial(material);
            result = new ItemStack(mat != null ? mat : Material.STONE);
        }

        final ItemAPI.ItemBuilder builder = ItemAPI.toBuilder(result);
        builder.setName(this.sanitizeString(name, player));

        if(!lore.isEmpty()) {
            builder.setLore(lore.stream()
                            .map(line -> sanitizeString(line, player))
                            .collect(Collectors.toList()));
        }

        return builder.build();
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

    private String sanitizeString(String text, Player player) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
