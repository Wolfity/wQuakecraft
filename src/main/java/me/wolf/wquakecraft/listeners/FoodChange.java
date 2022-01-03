package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodChange implements Listener {

    private final QuakeCraftPlugin plugin;
    public FoodChange(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        event.setFoodLevel(20);
        event.setCancelled(plugin.getPlayerManager().getQuakePlayer(event.getEntity().getUniqueId()) != null);
    }

}
