package me.wolf.wquakecraft.arena;

import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Arena {

    private final String name;
    private ArenaState arenaState;
    private int maxPlayers, minPlayers, gameTimer, gameCountdown, lobbyCountdown;
    private final Set<QuakePlayer> arenaMembers;
    private final List<Location> spawnLocations;
    private Location lobbyLocation;

    public Arena(final String name) { // creating new arenas
        this.name = name;
        this.arenaState = ArenaState.READY;
        this.maxPlayers = 10;
        this.minPlayers = 2;
        this.gameTimer = 300;
        this.gameCountdown = 10;
        this.lobbyCountdown = 10;
        this.arenaMembers = new HashSet<>();
        this.spawnLocations = new ArrayList<>();
    }

    public Arena(final String name, final int maxPlayers, final int minPlayers, final int gameTimer,
                 final int gameCountdown, final int lobbyCountdown, final List<Location> spawnLocations, final Location lobbyLocation) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.gameTimer = gameTimer;
        this.gameCountdown = gameCountdown;
        this.lobbyCountdown = lobbyCountdown;
        this.arenaState = ArenaState.READY;
        this.arenaMembers = new HashSet<>();
        this.spawnLocations = spawnLocations;
        this.lobbyLocation = lobbyLocation;
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

    public int getGameCountdown() {
        return gameCountdown;
    }

    public int getGameTimer() {
        return gameTimer;
    }

    public int getLobbyCountdown() {
        return lobbyCountdown;
    }

    public void setGameCountdown(int gameCountdown) {
        this.gameCountdown = gameCountdown;
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
}
