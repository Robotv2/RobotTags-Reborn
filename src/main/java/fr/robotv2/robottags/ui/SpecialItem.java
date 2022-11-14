package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.util.ItemAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecialItem {

    private final static Map<ItemStockType, SpecialItem> items = new HashMap<>();

    public static SpecialItem getSpecialItem(ItemStockType type) {
        return items.get(type);
    }

    public static void addSpecialItem(ItemStockType type, SpecialItem item) {
        items.put(type, item);
    }

    private static String sanitizeString(String message, Tag tag) {
        return message
                .replace("%tag%", tag.getDisplay())
                .replace("%tag-id%", tag.getId());
    }

    public static ItemStack getChangeItem(Tag tag) {

        final SpecialItem specialItem = SpecialItem.getSpecialItem(ItemStockType.CHANGE_ITEM);
        final ItemStack item = specialItem.getItemStack();
        final ItemAPI.ItemBuilder builder = ItemAPI.toBuilder(item);

        if(item.getItemMeta() == null) {
            return item;
        }

        final String title = SpecialItem.sanitizeString(item.getItemMeta().getDisplayName(), tag);
        builder.setName(title);

        List<String> lore = item.getItemMeta().getLore();
        if(lore != null && !lore.isEmpty()) {
            lore = lore.stream()
                    .map(line -> SpecialItem.sanitizeString(line, tag))
                    .collect(Collectors.toList());
            builder.setLore(lore);
        }

        return builder.build();
    }

    public enum ItemStockType {

        CHANGE_ITEM("change-item"),
        NEXT_PAGE("next-page"),
        PREVIOUS_PAGE("previous-page"),
        EMPTY_SLOTS("empty-slots");

        private final String id;
        ItemStockType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    private final String id;
    private final ItemStack stack;

    public SpecialItem(ConfigurationSection section) {

        this.id = section.getName();
        final String name = section.getString("display");
        final List<String> lore = section.getStringList("lore");
        final String material = section.getString("material", "STONE");

        ItemAPI.ItemBuilder builder;

        if(material.startsWith("head-")) {
            ItemStack head = ItemAPI.createSkull(material.substring("head-".length()));
            builder = ItemAPI.toBuilder(head);
        } else {
            builder = new ItemAPI.ItemBuilder().setType(Material.getMaterial(material.toUpperCase()));
        }

        if(!material.equalsIgnoreCase("air")) {
            builder.setName(name).setLore(lore).setKey(id, 1).addFlags(ItemFlag.HIDE_ATTRIBUTES).build();
        }

        this.stack = builder.build();
    }

    public String getId() {
        return this.id;
    }

    public ItemStack getItemStack() {
        return stack;
    }
}
