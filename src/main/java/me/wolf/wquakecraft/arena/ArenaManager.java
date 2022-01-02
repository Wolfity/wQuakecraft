package me.wolf.wquakecraft.arena;

import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager {

    private final YamlConfig cfg;

    public ArenaManager(final YamlConfig cfg) {
        this.cfg = cfg;
    }

    private final Set<Arena> arenas = new HashSet<>();

    public void createArena(final String name) {
        final Arena arena = new Arena(name);
        cfg.getConfig().createSection("arenas." + name);
        cfg.getConfig().set("arenas." + name + ".max-players", arena.getMaxPlayers());
        cfg.getConfig().set("arenas." + name + ".min-players", arena.getMinPlayers());
        cfg.getConfig().set("arenas." + name + ".lobby-countdown", arena.getLobbyCountdown());
        cfg.getConfig().set("arenas." + name + ".game-timer", arena.getGameTimer());
        cfg.getConfig().set("arenas." + name + ".max-kills", arena.getMaxKills());
        cfg.getConfig().set("arenas." + name + ".powerup-spawn-time", arena.getPowerupSpawn());
        cfg.saveConfig();

        arenas.add(arena);
    }

    public void loadArenas() {
        try { // arenas must be fully setup in order to load them correctly after a restart
            for (final String arenaName : cfg.getConfig().getConfigurationSection("arenas").getKeys(false)) {
                final List<Location> spawnLocations = new ArrayList<>();
                final List<Location> powerupLocations = new ArrayList<>();

                final int maxPlayers = cfg.getConfig().getInt("arenas." + arenaName + ".max-players");
                final int minPlayers = cfg.getConfig().getInt("arenas." + arenaName + ".min-players");
                final int lobbyCountdown = cfg.getConfig().getInt("arenas." + arenaName + ".lobby-countdown");
                final int gameTimer = cfg.getConfig().getInt("arenas." + arenaName + ".game-timer");
                final int maxKills = cfg.getConfig().getInt("arenas." + arenaName + ".max-kills");
                final int powerupSpawn = cfg.getConfig().getInt("arenas." + arenaName + ".powerup-spawn-time");

                final Location lobbyLoc = new Location(Bukkit.getWorld(Objects.requireNonNull(
                        cfg.getConfig().getString("arenas." + arenaName + ".lobby-world"))),
                        cfg.getConfig().getDouble("arenas." + arenaName + ".lobby-x"),
                        cfg.getConfig().getDouble("arenas." + arenaName + ".lobby-y"),
                        cfg.getConfig().getDouble("arenas." + arenaName + ".lobby-z"),
                        (float) cfg.getConfig().getDouble("arenas." + arenaName + ".lobby-yaw"),
                        (float) cfg.getConfig().getDouble("arenas." + arenaName + ".lobby-pitch"));

                // we are not allowing more spawns then players.
                for (int i = 1; i < maxPlayers + 1; i++) {
                    spawnLocations.add(new Location(
                            Bukkit.getWorld(Objects.requireNonNull(cfg.getConfig().getString("arenas." + arenaName + ".spawns." + i + ".world"))),
                            cfg.getConfig().getDouble("arenas." + arenaName + ".spawns." + i + ".x"),
                            cfg.getConfig().getDouble("arenas." + arenaName + ".spawns." + i + ".y"),
                            cfg.getConfig().getDouble("arenas." + arenaName + ".spawns." + i + ".z"),
                            (float) cfg.getConfig().getDouble("arenas." + arenaName + ".spawns." + i + ".yaw"),
                            (float) cfg.getConfig().getDouble("arenas." + arenaName + ".spawns." + i + ".pitch")));
                }

                for (final String loc : cfg.getConfig().getConfigurationSection("arenas." + arenaName + ".powerups").getKeys(false)) {
                    powerupLocations.add(
                            new Location(Bukkit.getWorld(Objects.requireNonNull(cfg.getConfig().getString("arenas." + arenaName + ".powerups." + loc + ".powerup-world"))),
                                    cfg.getConfig().getDouble("arenas." + arenaName + ".powerups." + loc + ".powerup-x"),
                                    cfg.getConfig().getDouble("arenas." + arenaName + ".powerups." + loc + ".powerup-y"),
                                    cfg.getConfig().getDouble("arenas." + arenaName + ".powerups." + loc + ".powerup-z")));
                }

                arenas.add(new Arena(arenaName)
                        .setMaxPlayers(maxPlayers)
                        .setMinPlayers(minPlayers)
                        .setGameTimer(gameTimer)
                        .setLobbyCountdown(lobbyCountdown)
                        .setSpawnLocations(spawnLocations)
                        .setLobbyLocation(lobbyLoc)
                        .setMaxKills(maxKills)
                        .setPowerupLocations(powerupLocations)
                        .setPowerupSpawn(powerupSpawn));

                Bukkit.getLogger().info("[QUAKECRAFT] SUCCESSFULLY LOADED THE ARENA:  " + arenaName);
            }
        } catch (final NullPointerException e) {
            Bukkit.getLogger().info("No Arenas were loaded!");
        }
    }

    // deleting an arena
    public void deleteArena(final String name) {
        arenas.remove(getArenaByName(name));
        cfg.getConfig().set("arenas." + name, null);
        cfg.saveConfig();
    }

    public Arena getFreeArena() {
        return arenas.stream().filter(arena -> arena.getArenaState() == ArenaState.READY).findFirst().orElse(null);
    }

    public Set<Arena> getArenas() {
        return arenas;
    }

    public boolean doesArenaExist(final String name) { // check if an arena with the passed in name exists
        return arenas.stream().anyMatch(arena -> arena.getName().equalsIgnoreCase(name));
    }

    public Arena getArenaByName(final String name) { // get an arena by passing in the string name
        return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Set<Arena> getAllFreeArenas() {
        return arenas.stream().filter(arena -> arena.getArenaState() == ArenaState.READY).collect(Collectors.toSet());
    }

    public Arena getArenaByPlayer(final QuakePlayer quakePlayer) {
        return arenas.stream().filter(arena -> arena.getArenaMembers().contains(quakePlayer)).findFirst().orElse(null);
    }

    public boolean isArenaAvailable(final String name) { // checking if the game is available (in the right state)
        return getArenaByName(name) != null && getArenaByName(name).getArenaState() == ArenaState.READY;
    }
}
