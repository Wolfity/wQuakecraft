package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreak implements Listener {

    private final QuakeCraftPlugin plugin;

    public BlockBreak(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId()) != null);
    }


}
