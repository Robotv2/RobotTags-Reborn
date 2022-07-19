package fr.robotv2.robottags.listeners;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.player.TagPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinAndQuitListener implements Listener {

    private final RobotTags plugin;
    public JoinAndQuitListener(RobotTags instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        final UUID playerUUID = event.getPlayer().getUniqueId();
        TagPlayer tagPlayer = plugin.getDataManager().getTagPlayer(playerUUID);

        if(tagPlayer == null) {
            tagPlayer = new TagPlayer(playerUUID);
        }

        TagPlayer.registerTagPlayer(tagPlayer);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        final UUID playerUUID = event.getPlayer().getUniqueId();
        final TagPlayer tagPlayer = TagPlayer.getTagPlayer(playerUUID);

        if(tagPlayer != null) {
            plugin.getDataManager().saveTagPlayer(tagPlayer);
            TagPlayer.unregisterTagPlayer(tagPlayer);
        }
    }
}
