package fr.robotv2.robottags.tag;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TagManager {

    private final Map<String, Tag> tags = new ConcurrentHashMap<>();

    @UnmodifiableView
    public Collection<Tag> getRegisteredTags() {
        return Collections.unmodifiableCollection(tags.values());
    }

    public void registerTag(Tag tag) {
        tags.put(tag.getId().toLowerCase(), tag);
    }

    public Tag fromId(String id) {
        return tags.get(id.toLowerCase());
    }

    public boolean exist(String id) {
        return tags.containsKey(id.toLowerCase());
    }

    public boolean hasAccess(Player player, Tag tag) {

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
