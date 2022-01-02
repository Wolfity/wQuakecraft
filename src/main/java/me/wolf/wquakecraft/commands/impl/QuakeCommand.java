package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.arena.ArenaManager;
import me.wolf.wquakecraft.commands.BaseCommand;
import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.utils.ItemUtils;
import me.wolf.wquakecraft.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuakeCommand extends BaseCommand {

    private final QuakeCraftPlugin plugin;

    public QuakeCommand(QuakeCraftPlugin plugin) {
        super("quake");
        this.plugin = plugin;
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        final Player player = ((Player) sender);
        final QuakePlayer quakePlayer = plugin.getPlayerManager().getQuakePlayer(player.getUniqueId());
        final ArenaManager arenaManager = plugin.getArenaManager();
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            player.sendMessage(Utils.colorize("&b[----------&7Quake Help &b----------]\n" +
                    "&b/quake join &7- Join Quakecraft \n" +
                    "&b/quake leave &7- Leave Quakecraft\n" +
                    "&b/quake joinarena <arena> &7- Join the arena if possible\n" +
                    "&b/quake leavearena &7- Leave your current arena\n" +
                    "&b/quake joinany &7- Join any free available arena\n" +
                    "&b/quake arenas &7- List all free arenas\n" +
                    "[-------------------------------]"));
        }
        if (args.length == 1) {
            if (isAdmin()) {
                if (args[0].equalsIgnoreCase("admin")) {
                    tell("&7[---------- &bQuake Admin Help &7----------]\n" +
                            "&b/quake createarena <name> &7- Create an arena \n" +
                            "&b/quake removearena <name> &7- Remove an arena \n" +
                            "&b/quake addspawn <name> &7- Add an arena spawn to the arena\n" +
                            "&b/quake addpowerupspawn <name> &7- Add a spot where a powerup can spawn" +
                            "[--------------------------------------]");
                } else if (args[0].equalsIgnoreCase("sethub")) {
                    tell("&aSuccessfully set the Quake Hub!");
                    setHub(player.getLocation());
                }
            }
            if (args[0].equalsIgnoreCase("join")) {
                if (quakePlayer == null) {
                    plugin.getPlayerManager().createQuakePlayer(player.getUniqueId());
                    plugin.getQuakeScoreboard().lobbyScoreboard(player);
                    tell("&aSuccessfully joined quake!");
                    player.getInventory().setItem(0, ItemUtils.createItem(Material.WOODEN_HOE, "&cChoose a Rail Gun"));

                } else tell("&aYou are already in quakecraft!");

            } else if (args[0].equalsIgnoreCase("leave")) {
                if (quakePlayer != null) {
                    tell("&aSuccessfully left quakecraft, cya!");
                    quakePlayer.getInventory().removeItem(ItemUtils.createItem(Material.WOODEN_HOE, "&cChoose a Rail Gun"));
                    plugin.getPlayerManager().removeQuakePlayer(player.getUniqueId());
                    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());


                } else tell("&cYou are not in quakecraft!");

            } else if (args[0].equalsIgnoreCase("arenas")) {
                final StringBuilder stringBuilder = new StringBuilder("&bAvailable Arenas:\n");
                for (final Arena arena : arenaManager.getAllFreeArenas()) {
                    stringBuilder.append("&3- &a").append(arena.getName()).append("\n");
                }
                player.sendMessage(Utils.colorize(stringBuilder.toString()));
            } else if (args[0].equalsIgnoreCase("leavearena")) {
                if (quakePlayer.isInGame()) {
                    plugin.getGameManager().leaveGame(quakePlayer);
                    plugin.getArenaManager().getArenaByPlayer(quakePlayer).removeArenaMember(quakePlayer);
                } else tell("&cYou are not in a game!");
            }
        }
        if (args.length == 2) {
            if (quakePlayer != null) {
                final String arenaName = args[1];
                if (args[0].equalsIgnoreCase("joinarena")) {
                    if (arenaManager.isArenaAvailable(arenaName)) { // also checks whether it exists
                        if (quakePlayer.getPlayerState() == PlayerState.IN_QUAKE) {
                            plugin.getGameManager().joinGame(quakePlayer, arenaManager.getArenaByName(arenaName));
                        } else tell("&cYou need to leave your current game first!");
                    } else tell("&cThis arena is not available!");
                }
                // creating an arena

                if (args[0].equalsIgnoreCase("createarena")) {
                    if (isAdmin()) {
                        if (!arenaManager.doesArenaExist(arenaName)) {
                            arenaManager.createArena(arenaName);
                            quakePlayer.sendMessage("&aSuccessfully created the arena &2" + arenaName);
                        } else quakePlayer.sendMessage("&cThis arena already exists!");
                    }
                    // removing an arena
                } else if (args[0].equalsIgnoreCase("removearena")) {
                    if (isAdmin()) {
                        if (arenaManager.doesArenaExist(arenaName)) {
                            arenaManager.deleteArena(arenaName);
                            quakePlayer.sendMessage("&aSuccessfully deleted the arena &2" + arenaName);
                        }
                    }
                } else if (args[0].equalsIgnoreCase("addspawn")) {
                    if (isAdmin()) {
                        addSpawn(quakePlayer, arenaName, plugin.getFileManager().getArenasConfigFile());
                    }
                } else if (args[0].equalsIgnoreCase("setlobby")) {
                    if (isAdmin()) {
                        setLobby(quakePlayer, arenaName, plugin.getFileManager().getArenasConfigFile());
                    }
                } else if (args[0].equalsIgnoreCase("addpowerupspawn")) {
                    if(isAdmin()) {
                        addPowerupSpawn(quakePlayer, arenaName, plugin.getFileManager().getArenasConfigFile());
                    }
                }
            } else tell("&cYou need to be in Quakecraft in order to execute this command!");
        }

    }

    private void addSpawn(final QuakePlayer quakePlayer, final String arenaName, final YamlConfig cfg) {
        final Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        if (arena != null) {
            if (arena.getSpawnLocations().size() < arena.getMaxPlayers()) { // less spawns then players
                arena.addSpawnLocation(quakePlayer.getLocation());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".world", quakePlayer.getWorld().getName());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".x", quakePlayer.getX());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".y", quakePlayer.getY());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".z", quakePlayer.getZ());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".pitch", quakePlayer.getPitch());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".yaw", quakePlayer.getYaw());
                tell("&aSuccessfully added a spawn location to the arena &2" + arenaName);
            } else { // replace the latest created spawn
                arena.getSpawnLocations().remove(arena.getSpawnLocations().size() - 1);
                arena.addSpawnLocation(quakePlayer.getLocation()); //
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".world", quakePlayer.getWorld().getName());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".x", quakePlayer.getX());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".y", quakePlayer.getY());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".z", quakePlayer.getZ());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".pitch", quakePlayer.getPitch());
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".yaw", quakePlayer.getYaw());

                quakePlayer.sendMessage("&cYou are creating too many spawn points for the size of this arena!\n" +
                        "Your last set spawn point has been replaced by this one.");
            }
            cfg.saveConfig();

        } else quakePlayer.sendMessage("&cThis arena does not exist!");
    }

    private void setLobby(final QuakePlayer quakePlayer, final String arenaName, final YamlConfig cfg) {
        final Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        if (arena != null) {
            cfg.getConfig().set("arenas." + arenaName + ".lobby-world", quakePlayer.getWorld().getName());
            cfg.getConfig().set("arenas." + arenaName + ".lobby-x", quakePlayer.getX());
            cfg.getConfig().set("arenas." + arenaName + ".lobby-y", quakePlayer.getY());
            cfg.getConfig().set("arenas." + arenaName + ".lobby-z", quakePlayer.getZ());
            cfg.getConfig().set("arenas." + arenaName + ".lobby-pitch", quakePlayer.getPitch());
            cfg.getConfig().set("arenas." + arenaName + ".lobby-yaw", quakePlayer.getYaw());
            cfg.saveConfig();
            quakePlayer.sendMessage("&aSuccessfully set the lobby spawn for &2" + arenaName);
        } else quakePlayer.sendMessage("&cThis arena does not exist!");
    }

    private void setHub(final Location location) {
        plugin.getConfig().set("spawn.world", location.getWorld().getName());
        plugin.getConfig().set("spawn.x", location.getX());
        plugin.getConfig().set("spawn.y", location.getY());
        plugin.getConfig().set("spawn.z", location.getZ());
        plugin.getConfig().set("spawn.pitch", location.getPitch());
        plugin.getConfig().set("spawn.yaw", location.getY());
        plugin.saveConfig();
    }

    private void addPowerupSpawn(final QuakePlayer player, final String name, final YamlConfig cfg) {
        final Arena arena = plugin.getArenaManager().getArenaByName(name);
        if (arena != null) {
            cfg.getConfig().set("arenas." + name + ".powerups." + arena.getPowerupLocations().size() + ".powerup-world", player.getWorld().getName());
            cfg.getConfig().set("arenas." + name + ".powerups." + arena.getPowerupLocations().size() + ".powerup-x", player.getX());
            cfg.getConfig().set("arenas." + name + ".powerups." + arena.getPowerupLocations().size() + ".powerup-y", player.getY());
            cfg.getConfig().set("arenas." + name + ".powerups." + arena.getPowerupLocations().size() + ".powerup-z", player.getZ());
            cfg.saveConfig();
            arena.addPowerupLocation(player.getLocation());
            player.sendMessage("&aSuccessfully added a powerup spawn for &2" + name);

        } else player.sendMessage("&cThis arena does not exist!");
    }

}