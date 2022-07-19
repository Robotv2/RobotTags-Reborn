package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.config.Settings;
import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.tag.TagManager;
import fr.robotv2.robottags.util.ColorUtil;
import fr.robotv2.robottags.util.FillAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public record TagInventoryManager(TagManager tagManager) {

    public void openForPlayer(Player player, int page) {
        player.openInventory(getTagMenuInventory(player, page));
    }

    public Inventory getTagMenuInventory(Player player, int page) {
        final Inventory inventory = Bukkit.createInventory(new TagInventoryHolder(page), Settings.UI_TOTAL_SLOTS, ColorUtil.color(Settings.UI_TITLE));

        if (Settings.WANT_EMPLTY_SLOTS_ITEM) {
            FillAPI.setupEmptySlots(inventory);
        }

        if (page < 0) {
            page = 1;
        }

        if (Settings.WANT_NEXT_PAGE && page != Settings.UI_TOTAL_PAGES) {
            inventory.setItem(Settings.NEXT_PAGE_SLOT, ItemStock.getNextPageItem());
        }

        if (Settings.WANT_PREVIOUS_PAGE && page != 1) {
            inventory.setItem(Settings.PREVIOUS_PAGE_SLOT, ItemStock.getPreviousPageItem());
        }

        for (Tag tag : tagManager.getRegisteredTags()) {
            if (page != tag.getPage()) continue;

            if (!tagManager.hasAccess(player, tag) && Settings.WANT_CHANGE_ITEM) {
                inventory.setItem(tag.getSlot(), ItemStock.getChangeItem(tag));
            } else {
                inventory.setItem(tag.getSlot(), tag.getGuiItem());
            }
        }

        for(String id : CustomItems.getIds()) {
            if(CustomItems.isEnabled(id))
                inventory.setItem(CustomItems.getSlot(id), CustomItems.getItemFor(id, player));
        }

        return inventory;
    }
}
