package me.wolf.wquakecraft.powerups.types;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.powerups.PowerUp;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MachineGunPowerUp extends PowerUp {

    private final QuakeCraftPlugin plugin;

    public MachineGunPowerUp(final QuakeCraftPlugin plugin, final ItemStack icon, final int duration) {
        super("machinegun", icon, duration);
        this.plugin = plugin;
    }

    @Override
    public void startPowerUp(QuakePlayer quakePlayer) {
        quakePlayer.setHasShootingCooldown(false);

        new BukkitRunnable() { // allow them to bypass the shooting cooldown for the specified amount of time
            @Override
            public void run() {
                if (getDuration() > 0) {
                    decrementDuration();
                } else {
                    this.cancel();
                    setDuration(getFinalDuration());
                    quakePlayer.setHasShootingCooldown(true);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
