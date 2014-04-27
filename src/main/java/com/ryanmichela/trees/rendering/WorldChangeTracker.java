package com.ryanmichela.trees.rendering;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {
    private Map<WorldChangeKey, WorldChange> changes = new HashMap<WorldChangeKey, WorldChange>(1000);


    public void addChange(Vector location, Material material, byte materialData, boolean overwrite) {
        int blockY = location.getBlockY();
        if (blockY > 255 || blockY < 0) {
            return;
        }

        WorldChangeKey key = new WorldChangeKey(location.getBlockX(), blockY, location.getBlockZ());
        if (changes.containsKey(key)) {
            if (overwrite) {
                changes.put(key, new WorldChange(location, material, materialData));
            }
        } else {
            changes.put(key, new WorldChange(location, material, materialData));
        }
    }

    public void applyChanges(Location refPoint) {
        Set<WorldChangeKey> touchedChunks = new HashSet<WorldChangeKey>();

        for (WorldChange change : changes.values()) {
            Location changeLoc = refPoint.clone().add(change.location);
            Block block = changeLoc.getBlock();
            block.setType(change.material);
            block.setData(change.materialData, false);
            touchedChunks.add(new WorldChangeKey(block.getChunk().getX(), -1, block.getChunk().getZ()));
        }

        for (WorldChangeKey chunkKey : touchedChunks) {
            refPoint.getWorld().refreshChunk(chunkKey.x, chunkKey.z);
        }
    }
}
