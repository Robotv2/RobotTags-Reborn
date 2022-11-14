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

    public static class SendableMessage {

        private String message;
        private boolean colored;
        private boolean prefix;

        public SendableMessage(String message) {
            this.message = message;
        }

        public SendableMessage colored(boolean colored) {
            this.colored = colored;
            return this;
        }

        public SendableMessage prefix(boolean prefix) {
            this.prefix = prefix;
            return this;
        }

        public SendableMessage replace(CharSequence target, CharSequence replacement) {
            this.message = message.replace(target, replacement);
            return this;
        }

        public void send(CommandSender sender) {

            if(colored) {
                this.message = ColorUtil.color(message);
            }

            if(this.prefix) {
                sender.sendMessage(Messages.PREFIX.getColored() + this.message);
            } else {
                sender.sendMessage(this.message);
            }
        }
    }

    private final String path;
    Messages(String path) {
        this.path = path;
    }

    public static Config getMessageConfig() {
        return ConfigAPI.getConfig("messages");
    }

    public SendableMessage toSendableMessage() {
        return new SendableMessage(getMessageConfig().get().getString(getPath()));
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
