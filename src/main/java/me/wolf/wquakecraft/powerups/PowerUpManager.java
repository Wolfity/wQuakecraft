package me.wolf.wquakecraft.powerups;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.powerups.types.MachineGunPowerUp;
import me.wolf.wquakecraft.powerups.types.SpeedPowerUp;
import me.wolf.wquakecraft.utils.ItemUtils;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class PowerUpManager {

    private final Set<PowerUp> powerUps = new HashSet<>();


    public void loadPowerUps(final QuakeCraftPlugin plugin) {
        final YamlConfig cfg = plugin.getFileManager().getPowerupsConfig();

        for (final String powerup : cfg.getConfig().getConfigurationSection("powerups").getKeys(false)) {
            if (cfg.getConfig().getBoolean("powerups." + powerup + ".enabled")) { // check if it's enabled
                final int duration = cfg.getConfig().getInt("powerups." + powerup + ".duration");
                final Material icon = Material.valueOf(cfg.getConfig().getString("powerups." + powerup + ".icon"));
                switch (powerup) {
                    case "speed":
                        powerUps.add(new SpeedPowerUp(ItemUtils.createItem(icon, powerup), duration));
                        break;
                    case "machinegun":
                        powerUps.add(new MachineGunPowerUp(plugin, ItemUtils.createItem(icon, powerup), duration));
                        break;
                }
            }
        }

    }

    public Set<PowerUp> getPowerUps() {
        return powerUps;
    }

}
