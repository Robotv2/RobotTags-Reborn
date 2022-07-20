package fr.robotv2.robottags.ui;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.util.ColorUtil;
import fr.robotv2.robottags.util.ItemAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class CustomItems {

    private final static RobotTags plugin = RobotTags.get();
    private static final Map<String, ItemStack> CUSTOM_ITEMS = new HashMap<>();

    public enum ClickTypeTag {
        RIGHT,
        LEFT
    }

    public static void initialize() {
        CUSTOM_ITEMS.clear();
        ConfigurationSection section = plugin.getConfiguration().get().getConfigurationSection("GUI.custom-items");
        if(section != null) {
            section.getKeys(false).forEach(CustomItems::buildAndSave);
        }
    }

    public static Set<String> getIds() {
        return CUSTOM_ITEMS.keySet();
    }

    public static int getSlot(String id) {
        return plugin.getConfiguration().get().getInt("GUI.custom-items." + id + ".slot");
    }

    public static boolean isEnabled(String id) {
        return plugin.getConfiguration().get().getBoolean("GUI.custom-items." + id + ".enabled");
    }

    private static void buildAndSave(String id) {
        try {
            String name = plugin.getConfiguration().get().getString("GUI.custom-items." + id + ".name");
            List<String> lore = plugin.getConfiguration().get().getStringList("GUI.custom-items." + id + ".lore");
            String MATERIAL_OR_HEAD = plugin.getConfiguration().get().getString("GUI.custom-items." + id + ".material", "STONE");

            ItemAPI.ItemBuilder builder;

            if(MATERIAL_OR_HEAD.startsWith("head-")) {
                ItemStack head = ItemAPI.createSkull(MATERIAL_OR_HEAD.replace("head-", ""));
                builder = ItemAPI.toBuilder(head);
            } else {
                builder = new ItemAPI.ItemBuilder().setType(Material.valueOf(MATERIAL_OR_HEAD.toUpperCase()));
            }

            builder.setName(name).setLore(lore).setKey("custom-item", id).addFlags(ItemFlag.HIDE_ATTRIBUTES).build();
            CUSTOM_ITEMS.put(id, builder.build());
        } catch (Exception e) {
            RobotTags.get().getLogger().warning(ColorUtil.color("&cAn error occurred while trying to create the custom-item: " + id));
            RobotTags.get().getLogger().warning(ColorUtil.color("&cError message: &f" + e.getMessage()));
        }
    }

    public static ItemStack getItemFor(String id, Player player) {
        if(!CUSTOM_ITEMS.containsKey(id)) {
            return new ItemStack(Material.AIR);
        }

        ItemStack item = CUSTOM_ITEMS.get(id);
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return item;

        String name = meta.getDisplayName();
        List<String> lore = meta.getLore();

        name = CustomItems.sanitizeString(name, player);
        if(lore != null && !lore.isEmpty()) {
            lore = lore.stream()
                    .map(line -> sanitizeString(line, player))
                    .collect(Collectors.toList());
        }

        ItemAPI.ItemBuilder builder = ItemAPI.toBuilder(item).setName(name);
        if(lore != null) builder.setLore(lore);

        return builder.build();
    }

    public static void click(ClickTypeTag type, String id, Player player) {
        if(!CUSTOM_ITEMS.containsKey(id)) return;

        String section_id = type == ClickTypeTag.RIGHT ? "right-click" : "left-click";
        List<String> section = plugin.getConfiguration().get().getStringList("GUI.custom-items." + id + "." + section_id);
        if(section.isEmpty()) return;

        for(String cmd : section) {

            if(cmd.startsWith("[CONSOLE]")) {
                cmd = cmd
                        .replace("[CONSOLE] ", "")
                        .replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                continue;
            }

            if(cmd.startsWith("[PLAYER]")) {
                cmd = cmd
                        .replace("[PLAYER] ", "")
                        .replace("%player%", player.getName());
                Bukkit.dispatchCommand(player, cmd);
                continue;
            }

            if(cmd.startsWith("[MESSAGE]")) {
                cmd = cmd
                        .replace("[MESSAGE] ", "")
                        .replace("%player%", player.getName());
                cmd = CustomItems.sanitizeString(cmd, player);
                player.sendMessage(ColorUtil.color(cmd));
                continue;
            }

            if(cmd.startsWith("[CLOSE]")) {
                player.closeInventory();
            }
        }
    }

    private static String sanitizeString(String text, Player player) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
