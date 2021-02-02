package com.samwolfson.timberwolf;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;

public class ChopCache {
    public Set<Location> woodLocations;
    public Material woodMaterial;

    public ChopCache(Set<Location> woodLocations, Material woodMaterial) {
        this.woodLocations = woodLocations;
        this.woodMaterial = woodMaterial;
    }
}
