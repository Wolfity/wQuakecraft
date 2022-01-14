package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.player.QuakePlayer;

public class AddPowerupSpawnCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "addpowerupspawn";
    }

    @Override
    protected String getUsage() {
        return "&b/quake addpowerupspawn <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Add a power-up spawn location";
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
        if (!plugin.getArenaManager().doesArenaExist(arenaName)) {
            player.sendMessage("&cThis arena does not exist!");
            return;
        }

        addPowerupSpawn(player, arenaName, plugin);

    }

    private void addPowerupSpawn(final QuakePlayer player, final String name, final QuakeCraftPlugin plugin) {
        final Arena arena = plugin.getArenaManager().getArenaByName(name);
        final YamlConfig cfg = plugin.getFileManager().getArenasConfigFile();
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
