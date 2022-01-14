package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.utils.Utils;

public class ArenasCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "arenas";
    }

    @Override
    protected String getUsage() {
        return "&b/quake arenas";
    }

    @Override
    protected String getDescription() {
        return "&7See a list of all available arenas";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {
        if (args.length != 1) {
            player.sendMessage(getUsage());
            return;
        }

        final StringBuilder stringBuilder = new StringBuilder("&bAvailable Arenas:\n");
        for (final Arena arena : plugin.getArenaManager().getAllFreeArenas()) {
            stringBuilder.append("&3- &a").append(arena.getName()).append("\n");
        }
        player.sendMessage(Utils.colorize(stringBuilder.toString()));

    }
}
