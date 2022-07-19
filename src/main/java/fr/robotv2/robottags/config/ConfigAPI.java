package fr.robotv2.robottags.config;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Objects;

public class ConfigAPI {

    private static Plugin plugin;
    private static final HashMap<String, Config> configs = new HashMap<>();

    public static Config getConfig(String name) {
        Objects.requireNonNull(plugin);
        Config config = configs.get(name);

        if(config == null) {
            config = new Config(ConfigAPI.plugin, name);
            configs.put(name, config);
        }

        return config;
    }

    public static void init(Plugin plugin) {
        ConfigAPI.plugin = plugin;
    }
}
