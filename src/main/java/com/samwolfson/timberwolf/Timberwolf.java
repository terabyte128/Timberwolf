package com.samwolfson.timberwolf;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Timberwolf extends JavaPlugin {
    ChopCache chopCache;

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

        List<String> allowedToolsStr = (List<String>) this.getConfig().getList("allowed-tools");
        List<String> allowedAdjacentStr = (List<String>) this.getConfig().getList("allowed-adjacent");

        chopCache = new ChopCache();
        this.getServer().getPluginManager().registerEvents(
                new ChopListener(this, chopCache, 
                    stringToMaterial(allowedAdjacentStr), 
                    stringToMaterial(allowedToolsStr)
                ), 
                this);

        this.getCommand("undochop").setExecutor(new UndoCommand(chopCache));
    }
}
