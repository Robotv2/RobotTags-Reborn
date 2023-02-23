package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.config.Settings;
import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.tag.TagManager;
import fr.robotv2.robottags.util.ColorUtil;
import fr.robotv2.robottags.util.FillAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;

public record TagInventoryManager(TagManager tagManager) {

    public void openForPlayer(Player player, int page) {
        CompletableFuture
                .supplyAsync(() -> this.getTagMenuInventory(player, page))
                .thenAccept(inventory -> Bukkit.getScheduler().runTask(RobotTags.get(), () -> player.openInventory(inventory)))
                .join();
    }

    public Inventory getTagMenuInventory(Player player, int page) {
        final Inventory inventory = Bukkit.createInventory(new TagInventoryHolder(page), Settings.UI_TOTAL_SLOTS, ColorUtil.color(Settings.UI_TITLE));

        if (Settings.WANT_EMPTY_SLOTS_ITEM) {
            FillAPI.setupEmptySlots(inventory);
        }

        if (page < 0) {
            page = 1;
        }

        if (Settings.WANT_NEXT_PAGE && page != Settings.UI_TOTAL_PAGES) {
            inventory.setItem(Settings.NEXT_PAGE_SLOT, SpecialItem.getSpecialItem(SpecialItem.ItemStockType.NEXT_PAGE).getItemStack());
        }

        if (Settings.WANT_PREVIOUS_PAGE && page != 1) {
            inventory.setItem(Settings.PREVIOUS_PAGE_SLOT, SpecialItem.getSpecialItem(SpecialItem.ItemStockType.PREVIOUS_PAGE).getItemStack());
        }

        for (Tag tag : tagManager.getRegisteredTags()) {

            if (page != tag.getPage()) {
                continue;
            }

            if (Settings.WANT_CHANGE_ITEM && !tagManager.hasAccess(player, tag)) {
                inventory.setItem(tag.getSlot(), SpecialItem.getChangeItem(tag));
            } else {
                inventory.setItem(tag.getSlot(), tag.getGuiItem(player));
            }
        }

        for(CustomItem item : CustomItem.getItems()) {
            if(item.isEnabled() && (item.getPage() == -1 || item.getPage() == page)) {
                inventory.setItem(item.getSlot(), item.getStackFor(player));
            }
        }

        return inventory;
    }
}
