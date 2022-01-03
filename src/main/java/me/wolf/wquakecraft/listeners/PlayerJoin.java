package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final QuakeCraftPlugin plugin;

    public PlayerJoin(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().createQuakePlayer(event.getPlayer().getUniqueId());
        event.getPlayer().getInventory().setItem(0, ItemUtils.createItem(Material.WOODEN_HOE, "&cChoose a Rail Gun"));
        plugin.getQuakeScoreboard().lobbyScoreboard(event.getPlayer());
    }

}
