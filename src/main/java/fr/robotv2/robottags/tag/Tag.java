package fr.robotv2.robottags.tag;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import fr.robotv2.robottags.util.ColorUtil;
import fr.robotv2.robottags.util.ItemAPI;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.stream.Collectors;

public final class Tag {

    private final ConfigurationSection section;
    private final String id;

    private final String display;
    private final boolean useHexColor;

    private final boolean needPermission;
    private final String permission;

    private ItemStack guiItem;
    private final int page;
    private final int slot;

    public Tag(final ConfigurationSection section) {
        this.section = section;
        this.id = section.getName();
        this.useHexColor = section.getBoolean("use-hex-color", true);

        final String display = Objects.requireNonNull(section.getString("display"));
        this.display = useHexColor ? IridiumColorAPI.process(display) : ColorUtil.color(display);

        this.needPermission = section.getBoolean("need-permission", false);
        this.permission = section.getString("permission", "");

        this.page = section.getInt("page", 1);
        this.slot = section.getInt("slot", 0);
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return display;
    }

    public boolean useHexColor() {
        return useHexColor;
    }

    public boolean needPermission() {
        return needPermission;
    }

    public String getPermission() {
        return permission;
    }

    public ItemStack getGuiItem() {

        if(guiItem != null) {
            return guiItem;
        }

        final ItemStack result;
        final String material = section.getString("material", "STONE");

        if(material.startsWith("head-")) {
            result = ItemAPI.createSkull(material.substring("head-".length()));
        } else {
            final Material mat = Material.matchMaterial(material);
            result = new ItemStack(mat != null ? mat : Material.STONE);
        }

        final ItemMeta meta = Objects.requireNonNull(result.getItemMeta());
        meta.setDisplayName(getDisplay());
        meta.setLore(section.getStringList("lore").stream().map(ColorUtil::color).collect(Collectors.toList()));
        result.setItemMeta(meta);
        this.guiItem = result;

        return result;
    }

    public int getPage() {
        return page;
    }

    public int getSlot() {
        return slot;
    }
}
