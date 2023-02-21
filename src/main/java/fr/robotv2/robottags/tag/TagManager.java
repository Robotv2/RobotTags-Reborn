package fr.robotv2.robottags.tag;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TagManager {

    private final Map<String, Tag> tags = new HashMap<>();

    @UnmodifiableView
    public Collection<Tag> getRegisteredTags() {
        return Collections.unmodifiableCollection(tags.values());
    }

    public void clearRegisteredTags() {
        this.tags.clear();
    }

    @Nullable
    public Tag fromId(String id) {
        return id != null ? tags.get(id.toLowerCase()) : null;
    }

    public void registerTag(Tag tag) {
        tags.put(tag.getId().toLowerCase(), tag);
    }

    public boolean exist(String id) {
        return tags.containsKey(id.toLowerCase());
    }

    public boolean hasAccess(Player player, @Nullable Tag tag) {

        if(tag == null) {
            return false;
        }

        if(!this.exist(tag.getId())) {
            return false;
        }

        if(tag.needPermission() && !player.hasPermission(tag.getPermission())) {
            return false;
        }

        return tag.getConditions().isEmpty()
                || tag.getConditions().stream().allMatch(condition -> condition.meetCondition(player, tag));
    }
}
