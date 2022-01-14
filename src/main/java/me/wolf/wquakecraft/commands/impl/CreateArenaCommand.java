package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.QuakePlayer;

public class CreateArenaCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "createarena";
    }

    @Override
    protected String getUsage() {
        return "&b/quake createarena <arena>";
    }

    @Override
    protected String getDescription() {
        return "&7Creates a new arena";
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

        if (plugin.getArenaManager().doesArenaExist(arenaName)) {
            player.sendMessage("&cThis arena already exists");
            return;
        }

        plugin.getArenaManager().createArena(arenaName);
        player.sendMessage("&aSuccessfully created the arena &2" + arenaName);


    }
}
