package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.railgun.RailGun;
import me.wolf.wquakecraft.utils.ItemUtils;
import me.wolf.wquakecraft.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryInteractions implements Listener {

    private final QuakeCraftPlugin plugin;

    public InventoryInteractions(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onRailGunSelector(PlayerInteractEvent event) {
        if (plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId()) == null) return;
        if (event.getMaterial() == Material.WOODEN_HOE) {
            openRailGunSelector(event.getPlayer());

        }
    }

    @EventHandler
    public void selectRailGun(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;

        final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(event.getWhoClicked().getUniqueId());
        if (player == null) return;

        plugin.getRailGunManager().getRailGuns().forEach(railGun -> {
            if (event.getCurrentItem().equals(ItemUtils.createItem(railGun.getMaterial(), railGun.getName()))) {
                player.setRailGun(railGun);
                player.sendMessage("&aSuccessfully selected " + railGun.getName() + "&a!");
                plugin.getQuakeScoreboard().lobbyScoreboard(player.getBukkitPlayer()); // update the scoreboard
            }
        });

    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId()) != null);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId());
        event.setCancelled(player != null && player.getPlayerState() != PlayerState.IN_GAME);
    }

    // players cant move items in their inventory whilst ingame
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(event.getWhoClicked().getUniqueId());

            final List<ItemStack> items = new ArrayList<>();
            items.add(event.getCurrentItem());
            items.add(event.getCursor());
            items.add((event.getClick() == ClickType.NUMBER_KEY) ?
                    event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) : event.getCurrentItem());
            for (ItemStack item : items) {
                if (item != null)
                    event.setCancelled(player != null);
            }
        }
    }

    private void openRailGunSelector(final Player player) {
        final Inventory selector = Bukkit.createInventory(null, 9, Utils.colorize("&cRailgun Selector"));

        for (final RailGun railGun : plugin.getRailGunManager().getSortedGuns()) {
            selector.addItem(ItemUtils.createItem(railGun.getMaterial(), railGun.getName()));
        }
        player.openInventory(selector);
    }

}

