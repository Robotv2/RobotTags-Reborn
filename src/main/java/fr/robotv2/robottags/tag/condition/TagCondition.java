package fr.robotv2.robottags.tag.condition;

import fr.robotv2.robottags.tag.Tag;
import org.bukkit.entity.Player;

public interface TagCondition {
    boolean meetCondition(Player player, Tag tag);
}
