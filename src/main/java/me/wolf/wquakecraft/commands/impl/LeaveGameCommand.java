package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.QuakePlayer;

public class LeaveGameCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "leavegame";
    }

    @Override
    protected String getUsage() {
        return "&b/quake leavegame";
    }

    @Override
    protected String getDescription() {
        return "&7Leave your current game";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {
        if (args.length != 1) {
            player.sendMessage(getUsage());
            return;
        }

        if (!player.isInGame()) {
            player.sendMessage("&cYou are not in game!");
            return;
        }

        plugin.getGameManager().leaveGame(player, false);

    }
}
