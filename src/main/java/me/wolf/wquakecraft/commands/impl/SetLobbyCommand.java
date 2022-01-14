package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.files.YamlConfig;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.utils.Utils;

public class SetLobbyCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "setlobby";
    }

    @Override
    protected String getUsage() {
        return "&b/quake setlobby <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Set the waiting lobby spawn location";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {
        if (!isAdmin(player)) {
            player.sendMessage("&cNo Permission");
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

        setLobby(player, arenaName, plugin);


    }

    private void setLobby(final QuakePlayer quakePlayer, final String arenaName, final QuakeCraftPlugin plugin) {
        final Arena arena = plugin.getArenaManager().getArenaByName(arenaName);
        final YamlConfig cfg = plugin.getFileManager().getArenasConfigFile();
        cfg.getConfig().set("arenas." + arenaName + ".lobby-location", Utils.locationToString(quakePlayer.getLocation()));
        cfg.saveConfig();
        arena.setLobbyLocation(quakePlayer.getLocation());
        quakePlayer.sendMessage("&aSuccessfully set the lobby spawn for &2" + arenaName);

    }
}
