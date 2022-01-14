package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.QuakePlayer;

public class RemoveArenaCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "removearena";
    }

    @Override
    protected String getUsage() {
        return "&b/quake removearena <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7 Delete an arena";
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

        plugin.getArenaManager().deleteArena(arenaName);
        player.sendMessage("&aSuccessfully deleted the arena &2" + arenaName);

    }
}
