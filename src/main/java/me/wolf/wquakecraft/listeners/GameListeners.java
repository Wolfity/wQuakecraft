package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.game.Game;
import me.wolf.wquakecraft.player.PlayerState;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.railgun.RailGun;
import me.wolf.wquakecraft.utils.ItemUtils;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListeners implements Listener {

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
            shootBullet(player);
        }

    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player) || !((event.getDamager()) instanceof Snowball)) return;
        final QuakePlayer killed = plugin.getPlayerManager().getQuakePlayer(event.getEntity().getUniqueId());
        final Player shooter = (Player) ((Snowball) event.getDamager()).getShooter();
        if(shooter == null) return;

        QuakePlayer killer = plugin.getPlayerManager().getQuakePlayer(shooter.getUniqueId());
        if(killer == null || killed == null) return;
        final Game game = plugin.getGameManager().getGameByPlayer(killer);

        plugin.getGameManager().handleGameKill(game, killer, killed);


        event.setCancelled(true);
    }


    private void shootBullet(final QuakePlayer shooter) {
        final RailGun gun = plugin.getRailGunManager().getRailGunFromPlayer(shooter);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (gun.getFireRate() > 0) { // the gun can not shoot yet
                    gun.decrementCooldown();
                } else {
                    this.cancel();
                    final Snowball bullet = shooter.getBukkitPlayer().launchProjectile(Snowball.class); // act as bullet
                    bullet.setVelocity(bullet.getVelocity().multiply(3.5));
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

                    gun.setFireRate(plugin.getFileManager().getRailGunsConfig().getConfig().getDouble("railguns." + gun.getIdentifier() + ".fire-rate"));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

    }

}
