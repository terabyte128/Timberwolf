package com.samwolfson.timberwolf;

import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class UndoCommand implements CommandExecutor {

    private final Map<Player, Stack<ChopCache>> caches;

    public UndoCommand(Map<Player, Stack<ChopCache>> caches) {
        this.caches = caches;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        final Player player = (Player) sender;

        Stack<ChopCache> actions;
        ChopCache lastAction;

        try {
            actions = caches.get(player);
            if (actions == null) {
                player.sendMessage("You don't have any chops cached.");
                return true;
            }
            lastAction = actions.peek();
        } catch (EmptyStackException ignored) {
            player.sendMessage("You don't have any chops cached.");
            return true;
        }

        PlayerInventory playerInventory = player.getInventory();
        ItemStack requiredItems = new ItemStack(lastAction.woodMaterial, lastAction.woodLocations.size());

        // check that they have enough in their inventory
        if (!playerInventory.containsAtLeast(requiredItems, requiredItems.getAmount())) {
            player.sendMessage("You must have at least " + lastAction.woodLocations.size() + " of "
                    + lastAction.woodMaterial + " before you can do that.");
            return true;
        }

        for (Location l : lastAction.woodLocations) {
            l.getBlock().setType(lastAction.woodMaterial);
        }

        playerInventory.removeItem(requiredItems);
        actions.pop();

        return true;
    }
}
