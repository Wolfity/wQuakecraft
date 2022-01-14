package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;

public class JoinArenaCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "joinarena";
    }

    @Override
    protected String getUsage() {
        return "&b/quake joinarena <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Join a specific arena";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {
        if (args.length != 2) {
            player.sendMessage(getUsage());
            return;
        }
        final String arenaName = args[1];
        if (!plugin.getArenaManager().isArenaAvailable(arenaName)) {
            player.sendMessage("&cThis arena is not available!");
            return;
        }
        if (player.getPlayerState() != PlayerState.IN_QUAKE) {
            player.sendMessage("&cYou are already in-game! Leave you current game first");
            return;
        }

        plugin.getGameManager().joinGame(player, plugin.getArenaManager().getArenaByName(arenaName));
        player.sendMessage("&aSuccessfully joined the arena!");
    }
}
