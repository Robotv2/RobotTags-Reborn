package fr.robotv2.robottags.ui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TagInventoryHolder implements InventoryHolder {

    private final int page;
    public TagInventoryHolder(int page) {
        this.page = page;
    }

    public TagInventoryHolder() {
        this(1);
    }

    public int getPage() {
        return page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }
}
