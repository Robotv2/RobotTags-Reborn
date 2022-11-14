package fr.robotv2.robottags.ui;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final ItemStack stack;

    public CustomItem(@NotNull ConfigurationSection section) {
        this.section = section;

        final String name = section.getString("name");
        final List<String> lore = section.getStringList("lore");
        final String material = section.getString("material", "STONE");

        ItemAPI.ItemBuilder builder;

        if (material.startsWith("head-")) {
            final ItemStack head = ItemAPI.createSkull(material.replace("head-", ""));
            builder = ItemAPI.toBuilder(head);
        } else {
            builder = new ItemAPI.ItemBuilder().setType(Material.getMaterial(material.toUpperCase()));
        }

        this.stack = builder
                .setName(name)
                .setLore(lore)
                .setKey("custom-item", getId())
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
    }

    public ItemStack getStackFor(Player player) {

        final ItemMeta meta = this.stack.getItemMeta();
        if(meta == null) {
            return this.stack;
        }

        final ItemAPI.ItemBuilder builder = ItemAPI.toBuilder(this.stack);
        builder.setName(this.sanitizeString(meta.getDisplayName(), player));

        List<String> lore = meta.getLore();

        if(lore != null && !lore.isEmpty()) {
            lore = lore.stream().map(line -> sanitizeString(line, player)).collect(Collectors.toList());
            builder.setLore(lore);
        }

        return builder.build();
    }

    public String getId() {
        return this.section.getName();
    }

    public int getSlot() {
        return this.section.getInt("slot", 0);
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
            command = command.replace("%player%", player.getName());

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
