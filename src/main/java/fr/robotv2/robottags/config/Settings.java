package fr.robotv2.robottags.config;

import fr.robotv2.robottags.RobotTags;
import org.bukkit.configuration.file.FileConfiguration;

public final class Settings {

    // Options
    public static boolean DEBUG;
    public static boolean SUPPORT_ESSENTIALSX_CHAT;
    public static boolean GLOWING_ITEM;

    public static boolean DEFAULT_TAG_ENABLED;
    public static String DEFAULT_TAG_ID;

    public static String UI_TITLE;
    public static int UI_TOTAL_SLOTS;
    public static int UI_TOTAL_PAGES;

    public static boolean WANT_NEXT_PAGE;
    public static int NEXT_PAGE_SLOT;
    public static boolean WANT_PREVIOUS_PAGE;
    public static int PREVIOUS_PAGE_SLOT;
    public static boolean WANT_CHANGE_ITEM;
    public static boolean WANT_EMPTY_SLOTS_ITEM;

    public static void initialize() {

        final Config configuration = RobotTags.get().getConfiguration();
        final FileConfiguration fileConfiguration = configuration.get();

        DEBUG = fileConfiguration.getBoolean("debug", false);
        SUPPORT_ESSENTIALSX_CHAT = fileConfiguration.getBoolean("options.support-essentialsx-chat", false);
        GLOWING_ITEM = fileConfiguration.getBoolean("options.glowing-item-if-player-access", true);
        DEFAULT_TAG_ENABLED = fileConfiguration.getBoolean("options.default-tag.enabled", false);
        DEFAULT_TAG_ID = fileConfiguration.getString("options.default-tag.tag", "default");

        UI_TITLE = fileConfiguration.getString("GUI.title", "Main menu");
        UI_TOTAL_SLOTS = fileConfiguration.getInt("GUI.total-slots", 3 * 9);
        UI_TOTAL_PAGES = fileConfiguration.getInt("GUI.total-pages", 1);

        WANT_CHANGE_ITEM = fileConfiguration.getBoolean("GUI.items.change-item.enabled");
        WANT_EMPTY_SLOTS_ITEM = fileConfiguration.getBoolean("GUI.items.empty-slots.enabled");

        WANT_NEXT_PAGE = fileConfiguration.getBoolean("GUI.items.next-page.enabled");
        NEXT_PAGE_SLOT = fileConfiguration.getInt("GUI.items.next-page.slot");

        WANT_PREVIOUS_PAGE = fileConfiguration.getBoolean("GUI.items.previous-page.enabled");
        PREVIOUS_PAGE_SLOT = fileConfiguration.getInt("GUI.items.previous-page.slot");
    }
}
