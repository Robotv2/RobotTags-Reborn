package fr.robotv2.robottags.player;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.config.Settings;
import fr.robotv2.robottags.events.TagChangeEvent;
import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.tag.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@DatabaseTable(tableName = "robottags_player_data")
public final class TagPlayer {

    private final TagManager tagManager = RobotTags.get().getTagManager();

    @DatabaseField(columnName = "player_uuid", id = true, unique = true)
    private UUID playerUUID;

    @DatabaseField(columnName = "tag_id")
    private String tagID = null;

    public TagPlayer(@NotNull UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public TagPlayer() {}

    public UUID getPlayerUniqueId() {
        return playerUUID;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    @Nullable
    public String getTagId() {
        return tagID;
    }

    public void setTagId(@Nullable String tagID, boolean callEvent) {

        if(callEvent) {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.playerUUID);
            final Tag from = tagManager.fromId(this.tagID);
            final Tag to = tagID != null ? tagManager.fromId(tagID) : null;
            final TagChangeEvent event = new TagChangeEvent(offlinePlayer, from, to);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) {
                return;
            }
        }

        this.tagID = tagID;
    }

    @Nullable
    public Tag getTag() {

        if(tagID != null && tagManager.exist(tagID)) {
            return tagManager.fromId(tagID);
        }

        if(Settings.DEFAULT_TAG_ENABLED && tagManager.exist(Settings.DEFAULT_TAG_ID)) {
            return tagManager.fromId(Settings.DEFAULT_TAG_ID);
        }

        return null;
    }

    @NotNull
    public String getTagDisplaySafe() {
        return getTag() != null ? getTag().getDisplay() : "";
    }

    @NotNull
    public String getTagIdSafe() {
        return tagID != null ? getTagId() : "";
    }

    //<<- STATIC METHOD ->>

    static Map<UUID, TagPlayer> players = new ConcurrentHashMap<>();

    @UnmodifiableView
    public static Collection<TagPlayer> getTagPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

    public static TagPlayer getTagPlayer(UUID playerUUID) {
        return players.get(playerUUID);
    }

    public static TagPlayer getTagPlayer(Player player) {
        return getTagPlayer(player.getUniqueId());
    }

    public static void registerTagPlayer(TagPlayer player) {
        players.put(player.playerUUID, player);
    }

    public static void unregisterTagPlayer(TagPlayer player) {
        players.remove(player.playerUUID);
    }
}
