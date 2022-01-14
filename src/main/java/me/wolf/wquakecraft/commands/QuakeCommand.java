package me.wolf.wquakecraft.commands;

import me.wolf.wquakecraft.QuakeCraftPlugin;
import me.wolf.wquakecraft.commands.impl.*;
import me.wolf.wquakecraft.player.QuakePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuakeCommand extends Command {

    private final QuakeCraftPlugin plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();
    private final List<SubCommand> adminCommands = new ArrayList<>();

    public QuakeCommand(QuakeCraftPlugin plugin) {
        super("quake");
        this.plugin = plugin;
        addSubCommand(false, new JoinArenaCommand(), new LeaveGameCommand(), new ArenasCommand(), new JoinAnyCommand());
        addSubCommand(true, new AddPowerupSpawnCommand(),
                new AddSpawnCommand(), new CreateArenaCommand(), new RemoveArenaCommand(), new SetHubCommand(), new SetLobbyCommand());
    }

    private void addSubCommand(final boolean admin, final SubCommand... subs) {
        if (!admin) {
            subCommands.addAll(Arrays.asList(subs));
        } else adminCommands.addAll(Arrays.asList(subs));
    }


    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, String[] args) {

        if (commandSender instanceof Player) {
            final QuakePlayer player = plugin.getPlayerManager().getQuakePlayer(((Player) commandSender).getUniqueId());

            if (args.length > 0) {
                for (SubCommand subCommand : Stream.concat(subCommands.stream(), adminCommands.stream()).collect(Collectors.toList())) {
                    if (args[0].equalsIgnoreCase(subCommand.getCommandName())) {
                        subCommand.executeCommand(player, args, plugin);
                    }
                }

            } else {
                final StringBuilder msg = new StringBuilder();
                if (player.getBukkitPlayer().hasPermission("quake.admin")) {
                    msg.append("&7[----------&bQuake Help&7----------] \n");
                    adminCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));
                    msg.append("&7[-------------------------------]");
                }
                subCommands.forEach(subCommand -> msg.append(subCommand.getUsage()).append(" - ").append(subCommand.getDescription()).append("\n"));


                player.sendMessage(msg.toString());
            }
        }
        return false;
    }
}