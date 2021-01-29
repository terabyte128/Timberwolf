package com.samwolfson.timberwolf;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class ChopCache {
    public static class PlayerCache {
        public Set<Location> woodLocations;
        public Material woodMaterial;

        public PlayerCache(Set<Location> woodLocations, Material woodMaterial) {
            this.woodLocations = woodLocations;
            this.woodMaterial = woodMaterial;
        }
    }

    private final HashMap<Player, PlayerCache> playerCaches;

    public ChopCache() {
        playerCaches = new HashMap<>();
    }

    public PlayerCache getPlayerCache(Player p) {
        return playerCaches.get(p);
    }

    public void setPlayerCache(Player p, PlayerCache pc) {
        playerCaches.put(p, pc);
    }
    
    public void removePlayerCache(Player p) {
        playerCaches.remove(p);
    }
}
