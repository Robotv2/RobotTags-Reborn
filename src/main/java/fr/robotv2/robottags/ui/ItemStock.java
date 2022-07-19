package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.util.FillAPI;
import fr.robotv2.robottags.util.ItemAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStock {

    private static final Map<ItemStockType, ItemStack> BUILD_ITEMS = new HashMap<>();
    private static final RobotTags plugin = RobotTags.get();

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

    public static void initialize() {
        BUILD_ITEMS.clear();
        EnumSet.allOf(ItemStockType.class).forEach(ItemStock::loadItem);
        FillAPI.setEmpty(BUILD_ITEMS.get(ItemStockType.EMPTY_SLOTS));
    }

    private static void loadItem(ItemStockType type) {

        final String id = type.getId();
        final String name = plugin.getConfiguration().get().getString("GUI.items." + id + ".display");
        final List<String> lore = plugin.getConfiguration().get().getStringList("GUI.items." + id + ".lore");
        final String material = plugin.getConfiguration().get().getString("GUI.items." + id + ".material", "STONE");

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

        BUILD_ITEMS.put(type, builder.build());
    }


    public static ItemStack getChangeItem(Tag tag) {
        ItemStack item = BUILD_ITEMS.get(ItemStockType.CHANGE_ITEM);
        ItemAPI.ItemBuilder builder = ItemAPI.toBuilder(item);

        assert item.getItemMeta() != null;

        String title = item.getItemMeta().getDisplayName();
        List<String> lore = item.getItemMeta().getLore();

        title = title.replace("%tag%", tag.getDisplay()).replace("%tag-id%", tag.getId());
        builder.setName(title);

        if(lore != null && !lore.isEmpty()) {
            lore = lore.stream()
                    .map(line -> line
                            .replace("%tag%", tag.getDisplay())
                            .replace("%tag-id%", tag.getId()))
                    .collect(Collectors.toList());
            builder.setLore(lore);
        }

        return builder.build();
    }

    public static ItemStack getNextPageItem() {
        return BUILD_ITEMS.get(ItemStockType.NEXT_PAGE);
    }

    public static ItemStack getPreviousPageItem() {
        return BUILD_ITEMS.get(ItemStockType.PREVIOUS_PAGE);
    }
}
