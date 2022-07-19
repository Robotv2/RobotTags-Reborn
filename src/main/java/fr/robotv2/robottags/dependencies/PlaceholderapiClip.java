package fr.robotv2.robottags.dependencies;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.player.TagPlayer;
import fr.robotv2.robottags.tag.TagManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderapiClip extends PlaceholderExpansion {

    @Override
    public @NotNull String getAuthor() {
        return "Robotv2";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "robottags";
    }

    @Override
    public @NotNull String getVersion() {
        return RobotTags.get().getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, @NotNull String placeholder) {

        if (player == null || !player.isOnline())
            return "";

        final TagPlayer tagPlayer = TagPlayer.getTagPlayer(player);

        switch(placeholder.toLowerCase()) {
            case "player":
                return tagPlayer.getTagDisplaySafe();
            case "player_id":
                return tagPlayer.getTagIdSafe();
            case "player_uncolored":
                return ChatColor.stripColor(tagPlayer.getTagDisplaySafe());
        }

        if(placeholder.startsWith("tag_")) {
            final TagManager tagManager = RobotTags.get().getTagManager();
            final String tadID = placeholder.substring("tag_".length());
            if(tagManager.exist(tadID)) {
                return tagManager.fromId(tadID).getDisplay();
            }
        }

        return placeholder;
    }
}
