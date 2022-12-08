package fr.robotv2.robottags.events;

import fr.robotv2.robottags.tag.Tag;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagChangeEvent extends Event implements Cancellable {

    private final static HandlerList HANDLERS = new HandlerList();

    private final OfflinePlayer offlinePlayer;
    private final Tag from;
    private final Tag to;

    private boolean cancel;

    public TagChangeEvent(@NotNull OfflinePlayer who, Tag from, Tag to) {
        this.offlinePlayer = who;
        this.from = from;
        this.to = to;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public @Nullable Tag getFrom() {
        return from;
    }

    public @Nullable Tag getTo() {
        return to;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
