package me.wolf.wquakecraft.commands;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.player.QuakePlayer;


public abstract class SubCommand {

    protected abstract String getCommandName();

    protected abstract String getUsage();

    protected abstract String getDescription();

    protected abstract void executeCommand(final QuakePlayer player, final String[] args, final QuakeCraftPlugin plugin);

    protected boolean isAdmin(final QuakePlayer quakePlayer) {
        return quakePlayer.getBukkitPlayer().hasPermission("quake.admin");
    }

}
