package me.wolf.wquakecraft.arena;

import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.Location;

import java.util.*;

public class Arena {

    private final String name;
    private ArenaState arenaState;
    private int gameTimer, lobbyCountdown, powerupSpawn, maxPlayers, minPlayers, maxKills;
    private final Set<QuakePlayer> arenaMembers;
    private List<Location> spawnLocations, powerupLocations;
    private Location lobbyLocation;

    public Arena(final String name) { // creating new arenas
        this.name = name;
        this.arenaState = ArenaState.READY;
        this.maxPlayers = 10;
        this.minPlayers = 2;
        this.gameTimer = 300;
        this.lobbyCountdown = 10;
        this.maxKills = 50;
        this.arenaMembers = new HashSet<>();
        this.spawnLocations = new ArrayList<>();
        this.powerupLocations = new ArrayList<>();
        this.powerupSpawn = 30; // every 30 sec a powerup will spawn
    }


    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public String getName() {
        return name;
    }

    public ArenaState getArenaState() {
        return arenaState;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setArenaState(ArenaState arenaState) {
        this.arenaState = arenaState;
    }

    public Set<QuakePlayer> getArenaMembers() {
        return arenaMembers;
    }
    public void addArenaMember(final QuakePlayer quakePlayer) {
        this.arenaMembers.add(quakePlayer);
    }
    public void removeArenaMember(final QuakePlayer quakePlayer) {
        this.arenaMembers.remove(quakePlayer);
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public Arena setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
        return this;
    }

    public Arena setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
        return this;
    }

    public Arena setLobbyCountdown(int lobbyCountdown) {
        this.lobbyCountdown = lobbyCountdown;
        return this;
    }

    public Arena setPowerupLocations(List<Location> powerupLocations) {
        this.powerupLocations = powerupLocations;
        return this;
    }


    public Arena setSpawnLocations(List<Location> spawnLocations) {
        this.spawnLocations = spawnLocations;
        return this;
    }

    public Arena setMaxKills(int maxKills) {
        this.maxKills = maxKills;
        return this;
    }

    public Arena setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        return this;
    }

    public Arena setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public Arena setPowerupSpawn(int powerupSpawn) {
        this.powerupSpawn = powerupSpawn;
        return this;
    }

    public void addSpawnLocation(final Location location) {
        this.spawnLocations.add(location);
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public void decrementLobbyCountdown() {
        this.lobbyCountdown--;
    }

    public void decrementGameTimer() {
        this.gameTimer--;
    }

    public List<Location> getPowerupLocations() {
        return powerupLocations;
    }

    public void addPowerupLocation(final Location location) {
        this.powerupLocations.add(location);
    }

    public int getPowerupSpawn() {
        return powerupSpawn;
    }

    public void decrementPowerUpTimer() {
        this.powerupSpawn--;
    }

    public int getMaxKills() {
        return maxKills;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return name.equals(arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
