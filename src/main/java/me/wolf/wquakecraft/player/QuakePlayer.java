package me.wolf.wquakecraft.player;

import me.wolf.wquakecraft.railgun.RailGun;
import me.wolf.wquakecraft.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class QuakePlayer {

    private final UUID uuid;
    private int kills, spawnProtection;
    private PlayerState playerState;
    private RailGun railGun;
    private boolean hasShootingCooldown;

    public QuakePlayer(final UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
        this.playerState = PlayerState.IN_QUAKE;
        this.spawnProtection = 5;
        this.hasShootingCooldown = true;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
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

    public RailGun getRailGun() {
        return railGun;
    }

    public void setRailGun(RailGun railGun) {
        this.railGun = railGun;
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

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
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

    public void clearFullInv() {
        getBukkitPlayer().getInventory().clear();
        getBukkitPlayer().getInventory().setHelmet(null);
        getBukkitPlayer().getInventory().setChestplate(null);
        getBukkitPlayer().getInventory().setLeggings(null);
        getBukkitPlayer().getInventory().setBoots(null);
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public Inventory getInventory() {
        return getBukkitPlayer().getInventory();
    }

    public boolean isInGame() {
        return playerState == PlayerState.IN_GAME;
    }

    public int getSpawnProtection() {
        return spawnProtection;
    }

    public void decrementSpawnProtection() {
        this.spawnProtection--;
    }

    public void setHasShootingCooldown(boolean hasShootingCooldown) {
        this.hasShootingCooldown = hasShootingCooldown;
    }

    public boolean hasShootingCooldown() {
        return hasShootingCooldown;
    }

    public void setSpawnProtection(int spawnProtection) {
        this.spawnProtection = spawnProtection;
    }

    public UUID getUuid() {
        return uuid;
    }
}
