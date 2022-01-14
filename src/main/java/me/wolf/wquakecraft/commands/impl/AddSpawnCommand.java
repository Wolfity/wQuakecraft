package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.utils.Utils;

public class AddSpawnCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "addspawn";
    }

    @Override
    protected String getUsage() {
        return "&b/quake addspawn <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Add a player spawn point to an arena";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {
        if (!isAdmin(player)) {
            player.sendMessage("&cNo Permission!");
            return;
        }
        if (args.length != 2) {
            player.sendMessage(getUsage());
            return;
        }

        final String arenaName = args[1];
        addSpawn(player, arenaName, plugin);

    }

    private void addSpawn(final QuakePlayer quakePlayer, final String arenaName, final QuakeCraftPlugin plugin) {
        final YamlConfig cfg = plugin.getFileManager().getArenasConfigFile();
        final Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        if (arena != null) {
            if (arena.getSpawnLocations().size() < arena.getMaxPlayers()) { // less spawns then players
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".spawn", Utils.locationToString(quakePlayer.getLocation()));
                quakePlayer.sendMessage("&aSuccessfully added a spawn location to the arena &2" + arenaName);
            } else { // replace the latest created spawn
                arena.getSpawnLocations().remove(arena.getSpawnLocations().size() - 1);
                cfg.getConfig().set("arenas." + arenaName + ".spawns." + arena.getSpawnLocations().size() + ".spawn", Utils.locationToString(quakePlayer.getLocation()));

                quakePlayer.sendMessage("&cYou are creating too many spawn points for the size of this arena!\n" +
                        "Your last set spawn point has been replaced by this one.");
            }
            arena.addSpawnLocation(quakePlayer.getLocation());
            cfg.saveConfig();

        } else quakePlayer.sendMessage("&cThis arena does not exist!");
    }
}
