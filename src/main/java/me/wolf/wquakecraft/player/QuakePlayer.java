package me.wolf.wquakecraft.player;

import me.wolf.wquakecraft.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class QuakePlayer {

    private final UUID uuid;
    private int kills;

    public QuakePlayer(final UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKiller() {
        this.kills++;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getName() {
        return getBukkitPlayer().getName();
    }

    public void sendCenteredMessage(final String msg) {
        Utils.sendCenteredMessage(getBukkitPlayer(), Utils.colorize(msg));
    }

    public void sendMessage(final String msg) {
        getBukkitPlayer().sendMessage(Utils.colorize(msg));
    }

    public void teleport(final Location location) {
        getBukkitPlayer().teleport(location);
    }

    public void resetHunger() {
        getBukkitPlayer().setFoodLevel(20);
    }

    public World getWorld() {
        return getLocation().getWorld();
    }

    public double getX() {
        return getLocation().getX();
    }

    public double getY() {
        return getLocation().getY();
    }

    public double getZ() {
        return getLocation().getZ();
    }

    public float getYaw() {
        return getLocation().getYaw();
    }

    public float getPitch() {
        return getLocation().getPitch();
    }

    public Location getLocation() {
        return getBukkitPlayer().getLocation();
    }

    public UUID getUuid() {
        return uuid;
    }
}
