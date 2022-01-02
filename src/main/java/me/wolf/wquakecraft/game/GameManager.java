package me.wolf.wquakecraft.game;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.arena.ArenaState;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.railgun.RailGun;
import me.wolf.wquakecraft.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private final QuakeCraftPlugin plugin;

    public GameManager(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    private final Set<Game> games = new HashSet<>();

    // handling every specific game event based on the game state
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
                giveGuns(arena);
                startGameTimer(game);
                sendGameMessage(game, false);
                break;
            case END:
                arena.setArenaState(ArenaState.END);
                game.setGameState(gameState);
                arena.getArenaMembers().forEach(quakePlayer -> quakePlayer.setPlayerState(PlayerState.IN_GAME));
                sendGameMessage(game, true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> cleanupGame(game), 200L); // give 200 sec before cleaning up
                break;
        }
    }

    // cleaning up everything after the game, remove players from the game, remove the arena from the game, etc...
    private void cleanupGame(final Game game) {
        final Arena arena = game.getArena();
        arena.setArenaState(ArenaState.READY);
        arena.setGameTimer(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".game-timer"));

        arena.getArenaMembers().forEach(this::leaveGame);
        arena.getArenaMembers().clear();

        Bukkit.getLogger().info("[QUAKECRAFT] The arena " + game.getArena().getName() + " is now available again!");
        games.remove(game);
    }

    // giving each user their gun
    private void giveGuns(final Arena arena) {
        arena.getArenaMembers().forEach(quakePlayer -> {
            final RailGun railGun = plugin.getRailGunManager().getRailGunFromPlayer(quakePlayer);
            quakePlayer.getInventory().addItem(ItemUtils.createItem(railGun.getMaterial(), railGun.getName()));
            quakePlayer.sendMessage("&aReceived your railgun: " + railGun.getName());
        });
    }

    public void handleGameKill(final Game game, final QuakePlayer killer, final QuakePlayer killed) {
        game.getArena().getArenaMembers().forEach(player -> player.sendMessage("&b" + killed.getName() + " &3was killed by &3" + killer.getName()));
        killer.incrementKills();
        killed.teleport(game.getArena().getSpawnLocations().get(new Random().nextInt(game.getArena().getSpawnLocations().size()))); // teleport to a randon location

        if(killer.getKills() == game.getArena().getMaxKills()) { // if the max kills has been reached, end the game
            setGameState(game, GameState.END);
        }
    }

    // starting the lobby countdown, when it ends, players are teleported to spawns and the game starts
    private void startLobbyCountdown(final Game game) {
        final Arena arena = game.getArena();
        Bukkit.getScheduler().runTaskLater(plugin, () -> new BukkitRunnable() { // give players an additional 5 sec to join before they cant, then start countdown
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
        }.runTaskTimer(plugin, 0L, 20L), 100L);

    }

    // starting the game timer
    private void startGameTimer(final Game game) {
        final Arena arena = game.getArena();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (arena.getGameTimer() > 0) {
                    arena.decrementGameTimer();
                } else { // gametimer runs out, end the game
                    this.cancel();
                    arena.setGameTimer(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".game-timer"));
                    setGameState(game, GameState.END);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // method for handling game joins
    public void joinGame(final QuakePlayer quakePlayer, final Arena arena) {
        quakePlayer.setPlayerState(PlayerState.IN_PREGAME);

        Game game = getGameByArena(arena); // checing if there is a game active from the arena the user wants to play in
        if (getGameByArena(arena) == null) { // if there is none, create a new one
            game = new Game(arena);
        }
        games.add(game);

        arena.addArenaMember(quakePlayer);
        quakePlayer.sendMessage("&aSuccessfully joined a game!"); // send a message to all current players
        arena.getArenaMembers().forEach(queueMember -> queueMember.sendMessage("&b" + queueMember.getName() + "&3 joined the game!"));

        quakePlayer.teleport(arena.getLobbyLocation()); // teleport to lobby

        if (arena.getArenaMembers().size() >= arena.getMinPlayers()) { // more or equals the required amount of players are in
            startLobbyCountdown(game); // start the lobby cd
        }

        final Game finalGame = game;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (quakePlayer.getPlayerState() != PlayerState.IN_QUAKE) {
                    plugin.getQuakeScoreboard().gameScoreboard(quakePlayer.getBukkitPlayer(), finalGame);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    // method for handling a game leave
    public void leaveGame(final QuakePlayer quakePlayer) {
        final Arena arena = plugin.getArenaManager().getArenaByPlayer(quakePlayer);
        if (arena == null) return;

        quakePlayer.sendMessage("&aSuccessfully left the arena!");
        quakePlayer.setPlayerState(PlayerState.IN_QUAKE); // resetting their state back to lobby
        quakePlayer.clearFullInv(); // resetting their inventory, hunger and teleporting them back to the hub
        quakePlayer.resetHunger();
        quakePlayer.teleport( // teleport to the quake spawn
                new Location(Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("spawn.world"))),
                        plugin.getConfig().getDouble("spawn.x"),
                        plugin.getConfig().getDouble("spawn.y"),
                        plugin.getConfig().getDouble("spawn.z"),
                        (float) plugin.getConfig().getDouble("spawn.pitch"),
                        (float) plugin.getConfig().getDouble("spawn.yaw")));

        quakePlayer.setKills(0); // reset their kills
        plugin.getQuakeScoreboard().lobbyScoreboard(quakePlayer.getBukkitPlayer());
         // remove them from the arena
    }

    // get the game based of an arena
    private Game getGameByArena(final Arena arena) {
        return games.stream().filter(game -> game.getArena().equals(arena)).findFirst().orElse(null);
    }

    // get the game a player is in, throws null if not in game
    public Game getGameByPlayer(final QuakePlayer player) {
        return games.stream().filter(game -> game.getArena().getArenaMembers().contains(player)).findFirst().orElse(null);
    }

    // teleport every player to a different spawn loc
    private void teleportToSpawn(final Arena arena) {
        final Queue<Location> remainingSpawns = new ArrayDeque<>(arena.getSpawnLocations());
        for (final QuakePlayer quakePlayer : arena.getArenaMembers()) {
            quakePlayer.teleport(remainingSpawns.poll());
        }
    }

    /**
     * @param game:    the game we are sending the message to
     * @param gameEnd: true -> message for game endings, false -> message for game start
     */

    private void sendGameMessage(final Game game, final boolean gameEnd) {
        final QuakePlayer winner = Collections.max(game.getArena().getArenaMembers(), Comparator.comparingInt(QuakePlayer::getKills));
        game.getArena().getArenaMembers().forEach(arenaMember -> {
            if (gameEnd) {
                arenaMember.sendCenteredMessage("&7-------------------------------------");
                arenaMember.sendCenteredMessage("");
                arenaMember.sendCenteredMessage("&c&lThe Game has ended!");
                arenaMember.sendCenteredMessage("&bWinner");
                arenaMember.sendCenteredMessage("&e#1 " + winner.getName() + " &7- " + winner.getKills());
                arenaMember.sendCenteredMessage("");
                arenaMember.sendCenteredMessage("&aEveryone will be warped out in 10 seconds!");
                arenaMember.sendCenteredMessage("&7-------------------------------------");

            } else {
                arenaMember.sendCenteredMessage("&7-------------------------------------");
                arenaMember.sendCenteredMessage("");
                arenaMember.sendCenteredMessage("&a&lThe game has started!");
                arenaMember.sendCenteredMessage("&a&lGood Luck!");
                arenaMember.sendCenteredMessage("&bThe first player to get &3" + game.getArena().getMaxKills() + " &bkills wins!");
                arenaMember.sendCenteredMessage("");
                arenaMember.sendCenteredMessage("&7-------------------------------------");
            }
            arenaMember.getBukkitPlayer().playSound(arenaMember.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 0.5F);
        });
    }
}
