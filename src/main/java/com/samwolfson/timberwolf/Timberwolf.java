package com.samwolfson.timberwolf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Timberwolf extends JavaPlugin {
    Map<Player, Stack<ChopCache>> caches = new HashMap<>();

    private List<Material> stringToMaterial(List<String> strList) {
        List<Material> materials = new ArrayList<>(); 

        for (String ms : strList) {
            Material mat = Material.getMaterial(ms);
            if (mat == null) {
                this.getLogger().warning("Could not match " + ms + " to a material, ignoring.");
            } else {
                materials.add(mat);
            }
        }

        return materials;
    } 


    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        // save config to plugin folder if it doesn't exist yet
        this.saveDefaultConfig();

        List<Material> allowedTools = stringToMaterial(this.getConfig().getStringList("allowed-tools"));
        List<Material> allowedAdjacent = stringToMaterial(this.getConfig().getStringList("allowed-adjacent"));
        String toolName = this.getConfig().getString("tool-name");

        this.getServer().getPluginManager().registerEvents(
                new ChopListener(this, caches, 
                    allowedAdjacent, 
                    allowedTools,
                    toolName
                ), 
                this);

        this.getCommand("undochop").setExecutor(new UndoCommand(caches));
        this.getCommand("listchops").setExecutor(new ListCommand(caches));
        this.getCommand("blesschop").setExecutor(new BlessCommand(allowedTools, toolName));
    }
}
