package com.samwolfson.timberwolf;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand implements CommandExecutor {
    private final Map<Player, Stack<ChopCache>> caches;

    public ListCommand(Map<Player, Stack<ChopCache>> caches) {
        super();
        this.caches = caches;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do that.");
            return true;
        }

        Player player = (Player) sender;
        
        Stack<ChopCache> actions = caches.get(player);

        if (actions == null || actions.isEmpty()) {
            player.sendMessage("You don't have any chops cached.");
            return true;
        }

        player.sendMessage("You have " + actions.size() + " chops:");

        for (int i = actions.size() - 1; i >= 0; i--) {
            ChopCache action = actions.get(i);
            StringBuilder actionMessage = new StringBuilder();
            Optional<Location> firstLocation = action.woodLocations.stream().findFirst();

            if (!firstLocation.isPresent()) {
                continue;
            }
            
            actionMessage.append(" - ")
                .append(action.woodLocations.size())
                .append(" of ")
                .append(action.woodMaterial)
                .append(" at x: ")
                .append(firstLocation.get().getBlockX())
                .append(" y: ")
                .append(firstLocation.get().getBlockY())
                .append(" z: ")
                .append(firstLocation.get().getBlockZ());

            player.sendMessage(actionMessage.toString());
        }

        return true;
	}
}
