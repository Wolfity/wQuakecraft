package me.wolf.wquakecraft.game;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.arena.ArenaState;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.powerups.PowerUp;
import me.wolf.wquakecraft.railgun.RailGun;
import me.wolf.wquakecraft.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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

        arena.getArenaMembers().forEach(player -> {
            leaveGame(player); // leave the game + give the rail gun selector back
            player.getInventory().setItem(0, ItemUtils.createItem(Material.WOODEN_HOE, "&cChoose a Rail Gun"));
        });

        arena.getArenaMembers().clear();

        // clearing all possible leftover powerups
        arena.getPowerupLocations().forEach(location -> Arrays.stream(location.getChunk().getEntities())
                .filter(entity -> entity instanceof Item).forEach(Entity::remove));

        Bukkit.getLogger().info("[QUAKECRAFT] The arena " + game.getArena().getName() + " is now available again!");

        games.remove(game);
    }

    // giving each user their gun
    private void giveGuns(final Arena arena) {
        arena.getArenaMembers().forEach(quakePlayer -> {
            final RailGun railGun = plugin.getRailGunManager().getRailGunFromPlayer(quakePlayer);
            quakePlayer.getInventory().addItem(ItemUtils.createItem(railGun.getMaterial(), railGun.getName()));
            quakePlayer.sendMessage("&aReceived your Rail Gun: " + railGun.getName());
        });
    }

    public void handleGameKill(final Game game, final QuakePlayer killer, final QuakePlayer killed) {
        game.getArena().getArenaMembers().forEach(player -> player.sendMessage("&b" + killed.getName() + " &3was killed by &3" + killer.getName()));
        killer.incrementKills();
        killed.teleport(game.getArena().getSpawnLocations().get(new Random().nextInt(game.getArena().getSpawnLocations().size()))); // teleport to a randon location

        new BukkitRunnable() {
            @Override
            public void run() {
                if (killed.getSpawnProtection() > 0) {
                    killed.decrementSpawnProtection();
                } else {
                    this.cancel();
                    killed.setSpawnProtection(5); // 5 seconds of spawn prot
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        if (killer.getKills() == game.getArena().getMaxKills()) { // if the max kills has been reached, end the game
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
                    arena.getArenaMembers().forEach(quakPlayer -> quakPlayer.getInventory().clear()); // clear inv before giving railgun
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
                if (game.getGameState() == GameState.INGAME) { // only run when the game is live
                    if (arena.getGameTimer() > 0) {
                        arena.decrementGameTimer();

                        if (arena.getPowerupSpawn() > 0) { // run the powerup spawn timer until the game ends.
                            arena.decrementPowerUpTimer();
                        } else {
                            spawnPowerUp(arena); // spawning the powerup
                            // restarting the timer
                            arena.setPowerupSpawn(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".powerup-spawn-time"));
                        }

                    } else { // gametimer runs out, end the game
                        this.cancel();
                        arena.setGameTimer(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".game-timer"));
                        // resetting the arena's power up spawn timer for the next game
                        arena.setPowerupSpawn(plugin.getFileManager().getArenasConfigFile().getConfig().getInt("arenas." + arena.getName() + ".powerup-spawn-time"));
                        setGameState(game, GameState.END);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void spawnPowerUp(final Arena arena) {
        final int randomSpawn = new Random().nextInt(arena.getPowerupLocations().size() - 1);
        final int randomPowerUp = new Random().nextInt(plugin.getPowerUpManager().getPowerUps().size() - 1);

        final PowerUp powerUp = new ArrayList<>(plugin.getPowerUpManager().getPowerUps()).get(randomPowerUp);

        arena.getSpawnLocations().get(0).getWorld().dropItem(arena.getPowerupLocations().get(randomSpawn), powerUp.getIcon());
        arena.getArenaMembers().forEach(quakePlayer -> quakePlayer.sendMessage("&3[!] &bA Power Up has been spawned! Walk over it to use it"));
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
        arena.getArenaMembers().forEach(queueMember -> queueMember.sendMessage("&b" + quakePlayer.getName() + "&3 joined the game!"));

        quakePlayer.teleport(arena.getLobbyLocation()); // teleport to lobby

        if (arena.getArenaMembers().size() == arena.getMinPlayers()) { // more or equals the required amount of players are in
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

    public Set<Game> getGames() {
        return games;
    }
}
