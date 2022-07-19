package fr.robotv2.robottags.listeners;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.player.TagPlayer;
import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.tag.TagManager;
import fr.robotv2.robottags.ui.CustomItems;
import fr.robotv2.robottags.ui.TagInventoryHolder;
import fr.robotv2.robottags.util.ItemAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class TagInventoryListener implements Listener {

    private final RobotTags plugin;
    public TagInventoryListener(RobotTags instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if(!(event.getInventory().getHolder() instanceof TagInventoryHolder holder)) {
            return;
        }

        event.setCancelled(true);

        final ClickType type = event.getClick();
        final Player player = (Player) event.getWhoClicked();
        final ItemStack current = event.getCurrentItem();
        final int page = holder.getPage();

        if(current == null || current.getItemMeta() == null) {
            return;
        }

        if(ItemAPI.hasKey(current, "tag", PersistentDataType.STRING)) {

            String tagID = (String) ItemAPI.getKeyValue(current, "tag", PersistentDataType.STRING);
            Tag tag = plugin.getTagManager().fromId(tagID);

            if(!plugin.getTagManager().hasAccess(player, tag)) {
                MessageManager.Message.PLAYER_CANT_ACCESS.send(player);
                return;
            }

            TagPlayer.getTagPlayer(player).setTagId(tagID);
            player.closeInventory();

            String message = MessageManager.Message.PLAYER_TAG_CHANGED.getMessage().replace("%tag%", tag.getDisplay());
            //TODO Main.sendMessage(player, true, message);

        } else if(ItemAPI.hasKey(current, "next-page", PersistentDataType.INTEGER)) {
            plugin.getTagInventoryManager().openForPlayer(player, page + 1);
        } else if(ItemAPI.hasKey(current, "previous-page", PersistentDataType.INTEGER)) {
            plugin.getTagInventoryManager().openForPlayer(player, page - 1);
        }

        else if(ItemAPI.hasKey(current, "custom-item", PersistentDataType.STRING)) {
            final String itemID = (String) ItemAPI.getKeyValue(current, "custom-item", PersistentDataType.STRING);
            if(type == ClickType.LEFT) {
                CustomItems.click(CustomItems.ClickTypeTag.LEFT, itemID, player);
            } else if(type == ClickType.RIGHT) {
                CustomItems.click(CustomItems.ClickTypeTag.RIGHT, itemID, player);
            }
        }
    }
}