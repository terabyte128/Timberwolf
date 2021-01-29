package com.samwolfson.timberwolf;

import org.bukkit.Location;
import org.bukkit.Material;

/*
 * Replants must be done asyncronously. I don't know why.
 */
public class ReplantTask implements Runnable {
    Location replantLocation;
    Material saplingType;

    public ReplantTask(Location replantLocation, Material saplingType) {
        this.replantLocation = replantLocation;
        this.saplingType = saplingType;
    }

    @Override
    public void run() {
        replantLocation.getBlock().setType(saplingType);
    }
}
