package fr.robotv2.robottags.tag;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class TagManager {

    private final Map<String, Tag> tags = new ConcurrentHashMap<>();

    public Tag fromId(String id) {
        return tags.get(id.toLowerCase());
    }

    public boolean exist(String id) {
        return tags.containsKey(id.toLowerCase());
    }

    public Collection<Tag> getRegisteredTags() {
        return Collections.unmodifiableCollection(tags.values());
    }

    public void registerTag(Tag tag) {
        tags.put(tag.getId().toLowerCase(), tag);
    }

    public void unregisterTag(Tag tag) {
        tags.remove(tag.getId().toLowerCase());
    }

    public void unregisterAll() {
        tags.clear();
    }

    public boolean hasAccess(Player player, Tag tag) {
        if(!exist(tag.getId())) return false;
        if(!tag.needPermission()) return true;
        return player.hasPermission(tag.getPermission());
    }
}
