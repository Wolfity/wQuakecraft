package me.wolf.wquakecraft.listeners;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.game.Game;
import me.wolf.wquakecraft.game.GameState;
import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private final QuakeCraftPlugin plugin;

    public PlayerQuit(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(event.getPlayer().getUniqueId());

        if (player.isInGame()) {
            final Game game = plugin.getGameManager().getGameByPlayer(player);
            plugin.getPlayerManager().removeQuakePlayer(event.getPlayer().getUniqueId());

            if(game.getArena().getArenaMembers().size() <= 1) { // if a user leaves, and there are either 0 players or 1, the game ends
                plugin.getGameManager().setGameState(game, GameState.END);
            }
            plugin.getGameManager().leaveGame(player, false);
        }

    }


}
