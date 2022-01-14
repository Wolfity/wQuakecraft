package me.wolf.wquakecraft.scoreboards;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.arena.Arena;
import me.wolf.wquakecraft.game.Game;
import me.wolf.wquakecraft.player.QuakePlayer;
import me.wolf.wquakecraft.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class QuakeScoreboard {

    private final QuakeCraftPlugin plugin;

    public QuakeScoreboard(final QuakeCraftPlugin plugin) {
        this.plugin = plugin;
    }

    public void lobbyScoreboard(final Player player) {
        final QuakePlayer quakePlayer = plugin.getPlayerManager().getQuakePlayer(player.getUniqueId());
        if (quakePlayer == null) return;

        final String activeGun = quakePlayer.getRailGun() == null ? Utils.colorize("&cNone") : quakePlayer.getRailGun().getName();


        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("quake", "quake");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&b&lQuake Lobby"));

        final Team help = scoreboard.registerNewTeam("help");
        help.addEntry(Utils.colorize("&7For Info: "));
        help.setPrefix("");
        help.setSuffix(Utils.colorize("&a" + "/quake"));
        objective.getScore(Utils.colorize("&7For Info: ")).setScore(1);

        final Team gun = scoreboard.registerNewTeam("gun");
        gun.addEntry(Utils.colorize("&bActive Railgun: "));
        gun.setPrefix("");
        gun.setSuffix(Utils.colorize(activeGun));
        objective.getScore(Utils.colorize("&bActive Railgun: ")).setScore(2);

        player.setScoreboard(scoreboard);
    }

    public void gameScoreboard(final QuakePlayer player) {
        if (player == null) return;
        if (!player.isInGame()) return;

        final Game game = plugin.getGameManager().getGameByPlayer(player);
        if (game == null) return;
        final Arena arena = game.getArena();
        if (arena == null) return;

        final ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        final Objective objective = scoreboard.registerNewObjective("quake", "quake");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Utils.colorize("&c&lQuake Game"));

        final Team time = scoreboard.registerNewTeam("time");
        time.addEntry(Utils.colorize("&bTime: "));
        time.setPrefix("");
        time.setSuffix(Utils.colorize("&7" + game.getArena().getGameTimer()));
        objective.getScore(Utils.colorize("&bTime: ")).setScore(1);

        final Team empty1 = scoreboard.registerNewTeam("empty1");
        empty1.addEntry(" ");
        empty1.setPrefix("");
        empty1.setSuffix("");
        objective.getScore(" ").setScore(2);

        final Team map = scoreboard.registerNewTeam("map");
        map.addEntry(Utils.colorize("&bMap: &2"));
        map.setPrefix("");
        map.setSuffix(Utils.colorize(arena.getName()));
        objective.getScore(Utils.colorize("&bMap: &2")).setScore(3);

        final Team empty2 = scoreboard.registerNewTeam("empty2");
        empty2.addEntry("  ");
        empty2.setPrefix("");
        empty2.setSuffix("");
        objective.getScore("  ").setScore(4);

        final Team kills = scoreboard.registerNewTeam("kills");
        kills.addEntry(Utils.colorize("&3Kills: "));
        kills.setPrefix("");
        kills.setSuffix(Utils.colorize("&2" + player.getKills()));
        objective.getScore(Utils.colorize("&3Kills: ")).setScore(5);

        player.getBukkitPlayer().setScoreboard(scoreboard);
    }

}
