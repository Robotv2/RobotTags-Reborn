package fr.robotv2.robottags.util;

import org.bukkit.ChatColor;

public final class ColorUtil {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
