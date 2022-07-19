package fr.robotv2.robottags;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import fr.robotv2.robottags.command.TagCommand;
import fr.robotv2.robottags.config.Config;
import fr.robotv2.robottags.config.ConfigAPI;
import fr.robotv2.robottags.config.Settings;
import fr.robotv2.robottags.dependencies.Metrics;
import fr.robotv2.robottags.dependencies.PlaceholderapiClip;
import fr.robotv2.robottags.dependencies.UpdateChecker;
import fr.robotv2.robottags.listeners.EssentialChatListener;
import fr.robotv2.robottags.listeners.JoinAndQuitListener;
import fr.robotv2.robottags.listeners.TagInventoryListener;
import fr.robotv2.robottags.player.TagPlayer;
import fr.robotv2.robottags.tag.Tag;
import fr.robotv2.robottags.tag.TagManager;
import fr.robotv2.robottags.ui.CustomItems;
import fr.robotv2.robottags.ui.ItemStock;
import fr.robotv2.robottags.ui.TagInventoryManager;
import fr.robotv2.robottags.util.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.sql.SQLException;
import java.util.stream.Collectors;

public final class RobotTags extends JavaPlugin {

    private static RobotTags instance;
    private final DataManager dataManager = new DataManager();
    private final TagManager tagManager = new TagManager();
    private final PlaceholderapiClip placeholderapi = new PlaceholderapiClip();

    private TagInventoryManager tagInventoryManager;

    public static RobotTags get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        final long current = System.currentTimeMillis();

        getLogger().info("");
        getLogger().info("Thanks for using RobotTags !");
        getLogger().info("Author: Robotv2");
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info("");

        this.loadFiles();

        try {
            this.loadDataManager();
        } catch (SQLException e) {
            e.printStackTrace();
            getLogger().warning("Couldn't connect to the database. Shutting down the plugin.");
            getLogger().warning(ChatColor.RED + e.getClass().getSimpleName() + ": " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        this.loadTags();
        this.loadListeners();
        this.loadCommands();

        tagInventoryManager = new TagInventoryManager(tagManager);

        updateChecker();
        new Metrics(this, 11791);
        placeholderapi.register();

        ItemStock.initialize();
        CustomItems.initialize();

        getLogger().info("The plugin has been loaded in " + (System.currentTimeMillis() - current) + "MS");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        placeholderapi.unregister();
        TagPlayer.getTagPlayers().forEach(getDataManager()::saveTagPlayer);
        instance = null;
    }

    public void onReload() {
        getTagConfig().reload();
        getConfiguration().reload();

        getTagManager().unregisterAll();
        this.loadTags();

        Settings.initialize();
        ItemStock.initialize();
        CustomItems.initialize();
    }

    // <<- UPDATER ->>

    private void updateChecker() {
        final String response = new UpdateChecker(91885).getVersion();

        if (response != null) {
            try {
                double pluginVersion = Double.parseDouble(getDescription().getVersion());
                double pluginVersionLatest = Double.parseDouble(response);

                if (pluginVersion < pluginVersionLatest) {
                    getLogger().info("");
                    getLogger().info("Update: Outdated version detected " + pluginVersion + ", latest version is "
                            + pluginVersionLatest + ", https://www.spigotmc.org/resources/robottags-hex-color-support-mysql-cross-server-and-gui-system.91885/");
                    getLogger().info("");
                }
            } catch (NumberFormatException exception) {
                if (!getDescription().getVersion().equalsIgnoreCase(response)) {
                    getLogger().info("");
                    getLogger().info("Update: Outdated version detected " + getDescription().getVersion() + ", latest version is " + response
                            + ", https://www.spigotmc.org/resources/robottags-hex-color-support-mysql-cross-server-and-gui-system.91885/");
                    getLogger().info("");
                }
            }
        }
    }

    // <<- GETTERS ->>

    public TagManager getTagManager() {
        return tagManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public TagInventoryManager getTagInventoryManager() {
        return tagInventoryManager;
    }

    public Config getTagConfig() {
        return ConfigAPI.getConfig("tags");
    }

    public Config getConfiguration() {
        return ConfigAPI.getConfig("configuration");
    }

    // <<- LOADERS ->>

    private void loadFiles() {
        ConfigAPI.init(this);
        getTagConfig().setup();
        getConfiguration().setup();
        Settings.initialize();
    }

    private void loadDataManager() throws SQLException {

        ConnectionSource source = null;
        DataManager.StorageMode mode;

        final FileConfiguration config = getConfiguration().get();

        try {
            mode = DataManager.StorageMode.valueOf(config.getString("storage.mode", "SQLLITE").toUpperCase());
        } catch (IllegalArgumentException exception) {
            mode = DataManager.StorageMode.SQLLITE;
        }

        switch (mode) {
            case SQLLITE -> {
                final File file = FileUtil.createFile(getDataFolder().getPath(), "database.db");
                source = new JdbcConnectionSource("jdbc:sqlite:".concat(file.getPath()));
            }
            case MYSQL -> {
                final String host = config.getString("storage.mysql-credentials.host");
                final String port = config.getString("storage.mysql-credentials.port");
                final String database = config.getString("storage.mysql-credentials.database");
                final String username = config.getString("storage.mysql-credentials.username");
                final String password = config.getString("storage.mysql-credentials.password");
                final boolean ssl = config.getBoolean("storage.mysql-credentials.useSSL", false);
                source = new JdbcConnectionSource("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl, username, password);
            }
        }

        if(source == null) {
            throw new SQLException("source is null");
        }

        this.dataManager.initialize(source);
    }

    private void loadTags() {
        final ConfigurationSection section = getTagConfig().get()
                .getConfigurationSection("tags");

        if(section == null) {
            return;
        }

        for(String key : section.getKeys(false)) {
            final ConfigurationSection keySection = section.getConfigurationSection(key);
            if(keySection == null) continue;
            getTagManager().registerTag(new Tag(keySection));
        }
    }

    private void loadListeners() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinAndQuitListener(this), this);
        pm.registerEvents(new EssentialChatListener(), this);
        pm.registerEvents(new TagInventoryListener(this), this);
    }

    private void loadCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);
        handler.registerValueResolver(Tag.class, context -> getTagManager().fromId(context.pop()));
        handler.getAutoCompleter().registerSuggestion("tags", getTagManager().getRegisteredTags().stream().map(Tag::getId).collect(Collectors.toList()));
        handler.register(new TagCommand(this));
    }
}
