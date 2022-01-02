package me.wolf.wquakecraft;

import me.wolf.wquakecraft.arena.ArenaManager;
import me.wolf.wquakecraft.commands.impl.QuakeCommand;
import me.wolf.wquakecraft.files.FileManager;
import me.wolf.wquakecraft.game.GameManager;
import me.wolf.wquakecraft.listeners.BlockBreak;
import me.wolf.wquakecraft.listeners.BlockPlace;
import me.wolf.wquakecraft.listeners.GameListeners;
import me.wolf.wquakecraft.listeners.InventoryInteractions;
import me.wolf.wquakecraft.player.PlayerManager;
import me.wolf.wquakecraft.railgun.RailGunManager;
import me.wolf.wquakecraft.scoreboards.QuakeScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;

public class QuakeCraftPlugin extends JavaPlugin {

    private FileManager fileManager;
    private PlayerManager playerManager;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private QuakeScoreboard quakeScoreboard;
    private RailGunManager railGunManager;

    @Override
    public void onEnable() {


        this.getConfig().options().copyDefaults();
        saveDefaultConfig();

        registerManagers();
        registerCommands();
        registerListeners();

    }

    private void registerCommands() {
        Collections.singletonList(
                new QuakeCommand(this)
        ).forEach(this::registerCommand);

    }

    private void registerListeners() {
        Arrays.asList(
                new BlockPlace(this),
                new BlockBreak(this),
                new InventoryInteractions(this),
                new GameListeners(this)
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
    }

    private void registerCommand(final Command command) {
        try {
            final Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            final CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(command.getLabel(), command);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void registerManagers() {
        this.fileManager = new FileManager(this);
        this.playerManager = new PlayerManager();
        this.arenaManager = new ArenaManager(fileManager.getArenasConfigFile());
        this.gameManager = new GameManager(this);
        this.quakeScoreboard = new QuakeScoreboard(this);
        this.railGunManager = new RailGunManager();

        railGunManager.loadRailGuns(fileManager.getRailGunsConfig());
        arenaManager.loadArenas();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public QuakeScoreboard getQuakeScoreboard() {
        return quakeScoreboard;
    }

    public RailGunManager getRailGunManager() {
        return railGunManager;
    }
}
