package me.wolf.wquakecraft.powerups;

import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.inventory.ItemStack;

public abstract class PowerUp {

    private final String name;
    private final ItemStack icon;
    private final int finalDuration;
    private int duration;

    public PowerUp(final String name, final ItemStack icon, final int duration) {
        this.name = name;
        this.icon = icon;
        this.duration = duration;
        this.finalDuration = duration;
    }


    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void decrementDuration() {
        this.duration--;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public int getFinalDuration() {
        return finalDuration;
    }

    public abstract void startPowerUp(final QuakePlayer quakePlayer);
}
