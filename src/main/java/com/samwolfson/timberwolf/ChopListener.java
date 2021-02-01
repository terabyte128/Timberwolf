package com.samwolfson.timberwolf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ChopListener implements Listener {
    final JavaPlugin plugin;
    final ChopCache chopCache;
    final List<Material> allowedAdjacent;
    final List<Material> allowedTools;

    public ChopListener(JavaPlugin plugin, ChopCache chopCache, List<Material> allowedAdjacent, List<Material> allowedTools) {
        this.plugin = plugin;
        this.chopCache = chopCache;
        this.allowedAdjacent = allowedAdjacent;
        this.allowedTools = allowedTools;
    }

    private class TreeData {
        public Material saplingMaterial;
        public Material leafMaterial;

        public TreeData(Material saplingMaterial, Material leafMaterial) {
            this.saplingMaterial = saplingMaterial;
            this.leafMaterial = leafMaterial;
        }
    }

    // map log types to all of the other types that are associated with this tree type
    Map<Material, TreeData> LOG_TO_TREE_DATA = new HashMap<>() {
        {
            put(Material.ACACIA_LOG, new TreeData(Material.ACACIA_SAPLING, Material.ACACIA_LEAVES));
            put(Material.BIRCH_LOG, new TreeData(Material.BIRCH_SAPLING, Material.BIRCH_LEAVES));
            put(Material.DARK_OAK_LOG, new TreeData(Material.DARK_OAK_SAPLING, Material.DARK_OAK_LEAVES));
            put(Material.JUNGLE_LOG, new TreeData(Material.JUNGLE_SAPLING, Material.JUNGLE_LEAVES));
            put(Material.OAK_LOG, new TreeData(Material.OAK_SAPLING, Material.OAK_LEAVES));
            put(Material.SPRUCE_LOG, new TreeData(Material.SPRUCE_SAPLING, Material.SPRUCE_LEAVES));
        }
    };

    @EventHandler
    public void onDestroyWood(BlockBreakEvent e) {
        // guard with permission
        if (!e.getPlayer().hasPermission("timerwolf.chop")) {
            return;
        }

        ItemStack itemInHand = e.getPlayer().getInventory().getItemInMainHand();

        // restrict to specific tools
        if (!allowedTools.contains(itemInHand.getType())) {
            return;
        }

        Block block = e.getBlock();
        Material logType = block.getType();
        World world = plugin.getServer().getWorld(block.getWorld().getUID());

        if (world == null) {
            plugin.getLogger().log(Level.SEVERE, "failed to drop tree: world was null!");
            return;
        }

        // must be a valid type of log
        if (!LOG_TO_TREE_DATA.containsKey(block.getType())) {
            return;
        }

        Block underneath = e.getBlock().getRelative(BlockFace.DOWN);

        // must be placed atop dirt
        if (!(underneath.getType().equals(Material.DIRT) || underneath.getType().equals(Material.COARSE_DIRT))) {
            return;
        }

        Set<Block> alreadyChecked = new HashSet<>();

        if (isTree(logType, block, alreadyChecked)) {
            // don't drop items from this event
            e.setDropItems(false);
            
            // only destroy the logs
            List<Block> toDestroy = alreadyChecked.stream().filter(b -> b.getType().equals(logType)).collect(Collectors.toList());

            // cache the last thing that a player chopped
            Set<Location> destroyedLocations = toDestroy.stream().map(Block::getLocation).collect(Collectors.toSet());
            chopCache.setPlayerCache(e.getPlayer(), new ChopCache.PlayerCache(destroyedLocations, logType));

            // replace logs with air
            for (Block b : toDestroy) {
                b.setType(Material.AIR);
            }

            // schedule a new sapling to be planted
            plugin.getServer().getScheduler().runTask(plugin, new ReplantTask(block.getLocation(), LOG_TO_TREE_DATA.get(logType).saplingMaterial));

            // give em the goods
            ItemStack woodDrop = new ItemStack(logType, toDestroy.size());
            world.dropItem(block.getLocation(), woodDrop);

            // damage their tool appropriately
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta instanceof Damageable) {
                Damageable dm = (Damageable) meta;
                dm.setDamage(dm.getDamage() + toDestroy.size());
                itemInHand.setItemMeta((ItemMeta) dm);
            }
        }


    }

    /* 
     * Get the 26 blocks that surround a block, i.e., each block in the 3x3x3
     * cube of blocks around it, not including the block itself.
     */
    private Block[] getSurroundingBlocks(Block b) {
        Location l = b.getLocation();
        Block[] surrounding = new Block[26];

        int i = 0;

        // technically, this is O(1)
        for (int x = l.getBlockX() - 1; x <= l.getBlockX() + 1; x++) {
            for (int y = l.getBlockY() - 1; y <= l.getBlockY() + 1; y++) {
                for (int z = l.getBlockZ() - 1; z <= l.getBlockZ() + 1; z++) {
                    Block candidate = l.getWorld().getBlockAt(x, y, z);

                    if (!candidate.equals(b))
                        surrounding[i++] = candidate;
                }
            }
        }

        return surrounding;
    }

    /*
     * Determine if the blocks surrounding current form a tree made of baseWood.
     */
    public boolean isTree(Material baseWood, Block current, Set<Block> alreadyChecked) {
        // if we hit leaves, then we reached the edge
        if (current.getBlockData() instanceof Leaves) {
            Leaves leaves = (Leaves) current.getBlockData();

            // leaves on naturally generated trees are not persistent;
            // they disappear when wood is not nearby
            return !leaves.isPersistent();
        }

        // if we've hit something that isn't a log, then we're at an edge. If
        // it's something that's allowed to be adjacent, then return true. If
        // not, return false.
        if (!current.getType().equals(baseWood)) {
            System.out.println(current.getType());
            return allowedAdjacent.contains(current.getType());
        }

        alreadyChecked.add(current);
        Block[] surroundingBlocks = getSurroundingBlocks(current);

        for (Block b : surroundingBlocks) {
            if (!alreadyChecked.contains(b)) {
                if (!isTree(baseWood, b, alreadyChecked)) {
                    return false;
                }
            }
        }

        return true;
    }
}
