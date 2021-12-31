package me.wolf.wquakecraft.game;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.arena.ArenaState;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private final QuakeCraftPlugin plugin;

    public GameManager(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<Game> games = new HashSet<>();

    public void setGameState(final Game game, final GameState gameState) {
        final Arena arena = game.getArena();
        switch (gameState) {
            case PREGAME: // Set to pregame once the lobbyCountdown starts
                arena.setArenaState(ArenaState.PREGAME);
                game.setGameState(gameState);
                arena.getArenaMembers().forEach(quakePlayer -> quakePlayer.setPlayerState(PlayerState.IN_PREGAME));
                break;
            case INGAME:
                arena.setArenaState(ArenaState.INGAME);
                game.setGameState(gameState);
                arena.getArenaMembers().forEach(quakePlayer -> quakePlayer.setPlayerState(PlayerState.IN_GAME));
                teleportToSpawn(arena);
                startGameTimer(game);
                break;
            case END:
                arena.setArenaState(ArenaState.END);
                game.setGameState(gameState);
                arena.getArenaMembers().forEach(quakePlayer -> quakePlayer.setPlayerState(PlayerState.IN_GAME));

                Bukkit.getScheduler().runTaskLater(plugin, () -> cleanupGame(game), 200L); // give 200 sec before cleaning up
                break;
        }
    }

    private void cleanupGame(final Game game) {
        final Arena arena = game.getArena();
        arena.setArenaState(ArenaState.READY);
        arena.setGameTimer(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".game-timer"));

        // teleporting them to the lobby
        arena.getArenaMembers().forEach(quakePlayer -> {
            quakePlayer.clearFullInv();
            quakePlayer.resetHunger();
            quakePlayer.teleport(
                    new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("world"))),
                            plugin.getConfig().getDouble("x"),
                            plugin.getConfig().getDouble("y"),
                            plugin.getConfig().getDouble("z"),
                            (float) plugin.getConfig().getDouble("pitch"),
                            (float) plugin.getConfig().getDouble("yaw")));
        });

        games.remove(game);
    }

    private void startLobbyCountdown(final Game game) {
        final Arena arena = game.getArena();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arena.getLobbyCountdown() > 0) {
                    arena.decrementLobbyCountdown();
                    arena.getArenaMembers().forEach(quakePlayer -> quakePlayer.sendMessage("&bThe game will start in &3" + arena.getLobbyCountdown() + "&b second(s)!"));
                } else {
                    this.cancel();
                    arena.setLobbyCountdown(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".lobby-countdown"));
                    setGameState(game, GameState.INGAME); // lobby countdown ended
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }


    private void startGameTimer(final Game game) {
        final Arena arena = game.getArena();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arena.getGameTimer() > 0) {
                    arena.decrementGameTimer();
                } else {
                    this.cancel();
                    arena.setGameTimer(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".game-timer"));
                    setGameState(game, GameState.END);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void joinGame(final QuakePlayer quakePlayer, final Arena arena) {
        Game game = getGameByArena(arena);
        if (getGameByArena(arena) == null) {
            game = new Game(arena);
        }
        games.add(game);

        arena.addArenaMember(quakePlayer);
        quakePlayer.sendMessage("&aSuccessfully joined a game!");
        arena.getArenaMembers().forEach(queueMember -> {
            queueMember.sendMessage("&b" + queueMember.getName() + "&3 joined the game!");
        });

        quakePlayer.teleport(arena.getLobbyLocation());

        if (arena.getArenaMembers().size() >= arena.getMinPlayers()) { // more or equals the required amount of players are in
            startLobbyCountdown(game); // start the lobby cd
        }

    }

    public void leaveGame(final QuakePlayer quakePlayer) {
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(quakePlayer);
        if (arena == null) return;

        arena.removeArenaMember(quakePlayer);
        quakePlayer.sendMessage("&aSuccessfully left the arena!");

    }

    private Game getGameByArena(final Arena arena) {
        return games.stream().filter(game -> game.getArena().equals(arena)).findFirst().orElse(null);
    }

    private void teleportToSpawn(final Arena arena) {
        final Queue<Location> remainingSpawns = new ArrayDeque<>(arena.getSpawnLocations());
        for (final QuakePlayer quakePlayer : arena.getArenaMembers()) {
            quakePlayer.teleport(remainingSpawns.poll());
        }
    }
}
