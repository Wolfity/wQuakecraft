package me.wolf.wquakecraft.commands.impl;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.SubCommand;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.utils.Utils;

public class SetHubCommand extends SubCommand {
    @Override
    protected String getCommandName() {
        return "sethub";
    }

    @Override
    protected String getUsage() {
        return "&b/quake sethub";
    }

    @Override
    protected String getDescription() {
        return "&7Set the main hub";
    }

    @Override
    protected void executeCommand(QuakePlayer player, String[] args, QuakeCraftPlugin plugin) {
        if (!isAdmin(player)) {
            player.sendMessage("&cNo Permission");
            return;
        }

        if (args.length != 1) {
            player.sendMessage(getUsage());
            return;
        }

        plugin.getConfig().set("hub", Utils.locationToString(player.getLocation()));
        plugin.saveConfig();
        player.sendMessage("&aSuccessfully updated the hub location");

    }
}
