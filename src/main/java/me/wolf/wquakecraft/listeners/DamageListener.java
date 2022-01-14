package me.wolf.wquakecraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onNaturalDamage(EntityDamageEvent event) {
        event.setCancelled(true); // cancel it no matter what state the players are in
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        event.setCancelled(true); // cancel no matter what
    }


}
