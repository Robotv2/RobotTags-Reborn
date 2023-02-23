package fr.robotv2.robottags.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.robotv2.robottags.RobotTags;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ItemAPI {

    public static HashMap<String, ItemStack> heads = new HashMap<>();

    public static HashMap<String, ItemStack> getCachedHeads() {
        return heads;
    }

    public static ItemStack getHead(UUID playerUUID) {
        if(heads.containsKey(playerUUID.toString())) {
            return heads.get(playerUUID.toString());
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        head.setItemMeta(meta);

        heads.put(playerUUID.toString(), head);
        return head;
    }

    public static ItemStack createSkull(String url) {

        if(heads.containsKey(url)) {
            return heads.get(url);
        }

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty()) return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }

        head.setItemMeta(headMeta);
        heads.put(url, head);
        return head;
    }

    public static ItemBuilder toBuilder(ItemStack item) {
        ItemBuilder builder = new ItemBuilder();
        builder.setMeta(item.getItemMeta());
        builder.setType(item.getType());
        builder.setAmount(item.getAmount());
        return builder;
    }

    public static ItemBuilder fromSection(ConfigurationSection section, @NotNull Player target) {

        final ItemStack temp;

        final String name = section.getString("name", "Unknown");
        final List<String> lore = section.getStringList("lore");
        final String material = section.getString("material", "STONE");

        if (material.startsWith("head-")) {
            temp = ItemAPI.createSkull(material.substring("head-".length()));
        } else if (material.equalsIgnoreCase("player-head")) {
            temp = ItemAPI.getHead(target.getUniqueId());
        } else {
            Material mat = Material.matchMaterial(material);
            temp = new ItemStack(mat != null ? mat : Material.STONE);
        }

        final ItemAPI.ItemBuilder builder = ItemAPI.toBuilder(temp);
        builder.setName(ItemAPI.sanitize(name, target));

        if(!lore.isEmpty()) {
            builder.setLore(lore.stream().map(line -> ItemAPI.sanitize(line, target)).toList());
        }

        return builder;
    }

    private static String sanitize(String text, Player target) {
        return ColorUtil.color(PlaceholderAPI.setPlaceholders(target, text));
    }

    public static boolean hasKey(ItemStack item, String keyStr, PersistentDataType<?, ?> type) {
        NamespacedKey key = new NamespacedKey(RobotTags.get(), keyStr);
        return item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(key, type);
    }

    public static <V> V getKeyValue(ItemStack item, String keyStr, PersistentDataType<?, V> type) {
        NamespacedKey key = new NamespacedKey(RobotTags.get(), keyStr);
        return item.getItemMeta().getPersistentDataContainer().get(key, type);
    }

    public static class ItemBuilder {
        private Material type;
        private int amount;
        private ItemMeta meta = new ItemStack(Material.GRASS).getItemMeta();

        public ItemBuilder setType(Material type) {
            this.type = type;
            return this;
        }

        public ItemBuilder setAmount(int amount) {
            this.amount = amount;
            return this;
        }

        public ItemBuilder setName(String name) {
            this.meta.setDisplayName(ColorUtil.color(name));
            return this;
        }

        public ItemBuilder setLore(String... lore) {
            this.meta.setLore(Arrays.stream(lore).map(ColorUtil::color).collect(Collectors.toList()));
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            this.meta.setLore(lore.stream().map(ColorUtil::color).collect(Collectors.toList()));
            return this;
        }

        public ItemBuilder setKey(String keyStr, String value) {
            NamespacedKey key = new NamespacedKey(RobotTags.get(), keyStr);
            this.meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
            return this;
        }

        public ItemBuilder setKey(String keyStr, double value) {
            NamespacedKey key = new NamespacedKey(RobotTags.get(), keyStr);
            this.meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
            return this;
        }

        public ItemBuilder setKey(String keyStr, int value) {
            NamespacedKey key = new NamespacedKey(RobotTags.get(), keyStr);
            this.meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
            return this;
        }

        public ItemBuilder setKey(String keyStr, float value) {
            NamespacedKey key = new NamespacedKey(RobotTags.get(), keyStr);
            this.meta.getPersistentDataContainer().set(key, PersistentDataType.FLOAT, value);
            return this;
        }

        public ItemBuilder addEnchant(Enchantment enchant, int level, boolean ignoreLevelRestriction) {
            this.meta.addEnchant(enchant, level, ignoreLevelRestriction);
            return this;
        }

        public ItemBuilder setUnbreakable(boolean unbreakable) {
            this.meta.setUnbreakable(unbreakable);
            return this;
        }

        public ItemBuilder addFlags(ItemFlag... flags) {
            this.meta.addItemFlags(flags);
            return this;
        }

        public ItemBuilder setMeta(ItemMeta meta) {
            this.meta = meta;
            return this;
        }

        public ItemStack build() {
            if(this.type == null)
                type = Material.AIR;
            if(this.amount <= 0)
                amount = 1;
            ItemStack item = new ItemStack(type, amount);
            item.setItemMeta(meta);
            return item;
        }
    }
}
