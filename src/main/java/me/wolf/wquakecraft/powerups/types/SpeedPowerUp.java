package me.wolf.wquakecraft.powerups.types;

import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.powerups.PowerUp;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedPowerUp extends PowerUp {

    public SpeedPowerUp(final ItemStack icon, final int duration) {
        super("speed", icon, duration);

    }

    @Override
    public void startPowerUp(QuakePlayer quakePlayer) {
        quakePlayer.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getDuration() * 20, 1));
    }

}
