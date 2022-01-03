package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.game.Game;
import me.wolf.wquakecraft.game.GameState;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.railgun.RailGun;
import me.wolf.wquakecraft.utils.ItemUtils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GameListeners implements Listener {

    final Map<QuakePlayer, Long> cooldownMap = new HashMap<>();
    private final QuakeCraftPlugin plugin;

    public GameListeners(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShoot(PlayerInteractEvent event) {
        final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId());
        if (event.getItem() == null) return;
        if (player == null) return;
        if (player.getPlayerState() != PlayerState.IN_GAME) return;
        final RailGun gun = plugin.getRailGunManager().getRailGunFromPlayer(player);

        if (event.getItem().equals(ItemUtils.createItem(gun.getMaterial(), gun.getName()))) {
            if (canShoot(player)) {
                shootBullet(player);
            }
        }
    }


    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !((event.getDamager()) instanceof Snowball)) return;
        final QuakePlayer killed = plugin.getPlayerManager().getQuakePlayer(event.getEntity().getUniqueId());
        final Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
        if (shooter == null) return;

        final QuakePlayer killer = plugin.getPlayerManager().getQuakePlayer(shooter.getUniqueId());
        if (killer == null || killed == null) return;
        final Game game = plugin.getGameManager().getGameByPlayer(killer); // get the game the players are in

        if (game.getGameState() != GameState.INGAME)
            return; // if the game isn't actually live, don't allow the hit to do anything

        if (killed.getSpawnProtection() == 5) {
            plugin.getGameManager().handleGameKill(game, killer, killed);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPowerUpPickUp(PlayerPickupItemEvent event) {
        final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId());
        if (player == null)
            return; // allowing to pickup items when in PlayerState.IN_GAME is handled in the InventoryInteractions class

        plugin.getPowerUpManager().getPowerUps().forEach(powerUp -> {
            if (event.getItem().getItemStack().equals(powerUp.getIcon())) {
                powerUp.startPowerUp(player);
                player.sendMessage("&bSuccessfully activated the &2" + powerUp.getName() + " &bPower Up&b for &2" + powerUp.getFinalDuration() + " &bseconds!");
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.getInventory().removeItem(powerUp.getIcon()), 2L);
            }
        });

    }

    private boolean canShoot(final QuakePlayer player) {
        final RailGun railGun = player.getRailGun();
        // player shot a bullet
        if (player.hasShootingCooldown()) { // check if they can bypass the cooldown or not
            if (!cooldownMap.containsKey(player)) { // check if the player is not on cooldown (bullet shot)
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (railGun.getFireRate() > 0) {  // start the cooldown timer, player can not shoot during this
                            cooldownMap.put(player, (long) railGun.getFireRate()); // put them in the cooldown map
                            railGun.decrementCooldown();
                        } else { // cooldown is over, player can shoot again
                            this.cancel(); // reset the fire-rate and remove from map
                            railGun.setFireRate(plugin.getFileManager().getRailGunsConfig().getConfig().getDouble("railguns." + railGun.getIdentifier() + ".fire-rate"));
                            cooldownMap.remove(player);
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);

            }
            return !cooldownMap.containsKey(player); // return whether the player is on cooldown or not
        }
        return true; // if the player has the bypass permission (hasshootingcooldown = true) they can shoot all the time
    }


    private void shootBullet(final QuakePlayer shooter) {
        final RailGun gun = plugin.getRailGunManager().getRailGunFromPlayer(shooter);

        final Snowball bullet = shooter.getBukkitPlayer().launchProjectile(Snowball.class); // act as bullet
        bullet.setVelocity(bullet.getVelocity().multiply(3));
        bullet.setShooter(shooter.getBukkitPlayer());

        new BukkitRunnable() {
            private int i = 0;

            @Override
            public void run() {
                i++;
                bullet.getWorld().spawnParticle(Particle.CRIT, bullet.getLocation(), 1); // some particle effects
                if (i > 5) this.cancel();
            }
        }.runTaskTimer(plugin, 0L, 1L);

        final PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(bullet.getEntityId()); // destroying the snowball entity so it looks invisible
        ((CraftPlayer) shooter.getBukkitPlayer()).getHandle().b.sendPacket(destroyPacket);
        shooter.getBukkitPlayer().playSound(shooter.getLocation(), Sound.ENTITY_SNOW_GOLEM_SHOOT, 0.8F, 0.8F);
        gun.setFireRate(plugin.getFileManager().getRailGunsConfig().getConfig().getDouble("railguns." + gun.getIdentifier() + ".fire-rate"));
    }

}
