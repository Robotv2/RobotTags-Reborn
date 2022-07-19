package fr.robotv2.robottags.listeners;

import fr.robotv2.robottags.config.Settings;
import fr.robotv2.robottags.player.TagPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EssentialChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {

        if(!Settings.SUPPORT_ESSENTIALSX_CHAT) return;

        String format = e.getFormat();
        format = format.replace("{TAG}", TagPlayer.getTagPlayer(e.getPlayer()).getTagDisplaySafe());
        e.setFormat(format);
    }
}
