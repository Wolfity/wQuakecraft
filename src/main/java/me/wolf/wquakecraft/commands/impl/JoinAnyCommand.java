package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;

public class JoinAnyCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "joinany";
    }

    @Override
    protected String getUsage() {
        return "&b/quake joinany";
    }

    @Override
    protected String getDescription() {
        return "&7Join any available arena";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {

        if (args.length != 1) {
            player.sendMessage(getUsage());
            return;
        }
        if (player.getPlayerState() != PlayerState.IN_QUAKE) {
            player.sendMessage("&cYou need to leave your current game first!");
            return;
        }
        if (plugin.getArenaManager().getFreeArena() == null) {
            player.sendMessage("&cCurrently no free arenas available!");
            return;
        }
        plugin.getGameManager().joinGame(player);


    }
}
