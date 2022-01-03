package me.wolf.wquakecraft.railgun;

import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.Material;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RailGunManager {

    private final Set<RailGun> railGuns = new HashSet<>();

    public void loadRailGuns(final YamlConfig cfg) {
        for (final String gun : cfg.getConfig().getConfigurationSection("railguns").getKeys(false)) {
            final Material material = Material.valueOf(cfg.getConfig().getString("railguns." + gun + ".material"));
            final double fireRate = cfg.getConfig().getDouble("railguns." + gun + ".fire-rate");
            final String name = cfg.getConfig().getString("railguns." + gun + ".name");
            railGuns.add(new RailGun(gun, name, material, fireRate)); // "gun" is the identifier here
        }

    }

    // get a railgun object by passing in an identifier, throws NPE if not found
    public RailGun getRailGunByIdentifier(final String identifier) {
        return railGuns.stream().filter(railGun -> railGun.getIdentifier().equalsIgnoreCase(identifier)).findFirst().orElse(null);
    }

    public List<RailGun> getSortedGuns() {
        return railGuns.stream().sorted(Comparator.comparing(RailGun::getIdentifier)).collect(Collectors.toList());
    }

    public RailGun getRailGunFromPlayer(final QuakePlayer player) {
        if (player.getRailGun() == null) {
            player.setRailGun(getRailGunByIdentifier("default"));
        } // give them the default railgun if none were selected
        return player.getRailGun() == null ? getRailGunByIdentifier("default") : player.getRailGun();
    }

    public Set<RailGun> getRailGuns() {
        return railGuns;
    }
}
