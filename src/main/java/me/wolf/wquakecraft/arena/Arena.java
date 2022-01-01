package me.wolf.wquakecraft.arena;

import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.Location;

import java.util.*;

public class Arena {

    private final String name;
    private ArenaState arenaState;
    private int maxPlayers, minPlayers, gameTimer, lobbyCountdown, maxKills;
    private final Set<QuakePlayer> arenaMembers;
    private final List<Location> spawnLocations;
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
    }

    public Arena(final String name, final int maxPlayers, final int minPlayers, final int gameTimer,
                 final int lobbyCountdown, final List<Location> spawnLocations, final Location lobbyLocation, final int maxKills) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gameTimer = gameTimer;
        this.lobbyCountdown = lobbyCountdown;
        this.arenaState = ArenaState.READY;
        this.arenaMembers = new HashSet<>();
        this.spawnLocations = spawnLocations;
        this.lobbyLocation = lobbyLocation;
        this.maxKills = maxKills;
    }


    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
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

    public void setGameTimer(int gameTimer) {
        this.gameTimer = gameTimer;
    }

    public void setLobbyCountdown(int lobbyCountdown) {
        this.lobbyCountdown = lobbyCountdown;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
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

    public int getMaxKills() {
        return maxKills;
    }

    public void setMaxKills(int maxKills) {
        this.maxKills = maxKills;
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
