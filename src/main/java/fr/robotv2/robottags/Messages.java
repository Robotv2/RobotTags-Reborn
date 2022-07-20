package fr.robotv2.robottags;

import fr.robotv2.robottags.config.Config;
import fr.robotv2.robottags.config.ConfigAPI;
import fr.robotv2.robottags.util.ColorUtil;
import org.bukkit.command.CommandSender;

public enum Messages {

    PREFIX("prefix"),
    PLUGIN_RELOADED("plugin-reloaded"),

    ADMIN_SET_TAG("admin-set-tag"),
    ADMIN_CLEAR_TAG("admin-clear-tag"),

    PLAYER_TAG_CHANGED("player-tag-changed"),
    PLAYER_CANT_ACCESS("player-can't-access");

    private final String path;
    Messages(String path) {
        this.path = path;
    }

    public static Config getMessageConfig() {
        return ConfigAPI.getConfig("messages");
    }

    public String getPath() {
        return path;
    }

    public String getColored() {
        final String message = getMessageConfig().get().getString(getPath());
        return message != null ? ColorUtil.color(message) : "";
    }

    public void send(CommandSender sender) {
        sender.sendMessage(Messages.PREFIX + getColored());
    }
}
