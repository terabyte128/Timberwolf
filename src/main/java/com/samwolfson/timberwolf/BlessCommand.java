package com.samwolfson.timberwolf;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlessCommand implements CommandExecutor {
    List<Material> allowedTools;
    String toolName;

    public BlessCommand(List<Material> allowedTools, String toolName) {
        super();
        this.allowedTools = allowedTools;
        this.toolName = toolName;
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You need to be a player to do this.");
            return true;
        }

        Player player = (Player) sender;

        ItemStack toolInHand = player.getInventory().getItemInMainHand();
        if (!allowedTools.contains(toolInHand.getType())) {
            player.sendMessage("That tool can't be used for chopping.");
            player.sendMessage("Try: " + allowedTools);
            return true;
        }

        ItemMeta meta = toolInHand.getItemMeta();
        meta.setDisplayName(toolName);
        toolInHand.setItemMeta(meta);

        player.sendMessage("Congrats! Your tool can now be used for choppin.'");

        return true;
	}
}
