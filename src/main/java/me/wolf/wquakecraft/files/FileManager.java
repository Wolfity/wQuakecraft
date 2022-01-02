package me.wolf.wquakecraft.files;


import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.utils.Utils;
import org.bukkit.Bukkit;

public class FileManager {

    private YamlConfig arenas, railGuns, powerups;


    public FileManager(final QuakeCraftPlugin plugin) {
        try {
            arenas = new YamlConfig("arenas.yml", plugin);
            railGuns = new YamlConfig("railguns.yml", plugin);
            powerups = new YamlConfig("powerups.yml", plugin);
        } catch (final Exception e) {
            Bukkit.getLogger().info(Utils.colorize("&4Something went wrong while loading the yml files"));
            e.printStackTrace();
        }
    }

    public YamlConfig getArenasConfigFile() {
        return arenas;
    }

    public YamlConfig getRailGunsConfig() {
        return railGuns;
    }

    public YamlConfig getPowerupsConfig() {
        return powerups;
    }
}
