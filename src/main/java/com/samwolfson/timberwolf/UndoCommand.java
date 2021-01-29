package com.samwolfson.timberwolf;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class UndoCommand implements CommandExecutor {

    private final ChopCache chopCache;

    public UndoCommand(ChopCache chopCache) {
        this.chopCache = chopCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        final Player player = (Player) sender;

        ChopCache.PlayerCache playerCache = chopCache.getPlayerCache(player);

        if (playerCache == null) {
            sender.sendMessage("You don't have any chops cached.");
            return false;
        }

        PlayerInventory playerInventory = player.getInventory();
        ItemStack requiredItems = new ItemStack(playerCache.woodMaterial, playerCache.woodLocations.size());

        // check that they have enough in their inventory
        if (!playerInventory.containsAtLeast(requiredItems, requiredItems.getAmount())) {
            player.sendMessage("You must have at least " + playerCache.woodLocations.size() + " of "
                    + playerCache.woodMaterial + " before you can do that.");
            return false;
        }

        for (Location l : playerCache.woodLocations) {
            l.getBlock().setType(playerCache.woodMaterial);
        }

        playerInventory.removeItem(requiredItems);
        chopCache.removePlayerCache(player);

        return true;
    }
}
