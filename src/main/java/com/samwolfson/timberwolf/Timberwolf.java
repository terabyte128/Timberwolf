package com.samwolfson.timberwolf;

import org.bukkit.plugin.java.JavaPlugin;

public class Timberwolf extends JavaPlugin {
    ChopCache chopCache;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        chopCache = new ChopCache();
        this.getServer().getPluginManager().registerEvents(new ChopListener(this, chopCache), this);
        this.getCommand("undochop").setExecutor(new UndoCommand(chopCache));
    }
}
