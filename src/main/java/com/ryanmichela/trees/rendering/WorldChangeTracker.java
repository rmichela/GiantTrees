package com.ryanmichela.trees.rendering;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {
    private Map<WorldChangeKey, WorldChange> changes = new HashMap<WorldChangeKey, WorldChange>(1000);


    public void addChange(int x, int y, int z, Material material, byte materialData, boolean overwrite) {
        if (y > 255 || y < 0) {
            return;
        }

        WorldChangeKey key = new WorldChangeKey(x, y, z);
        if (changes.containsKey(key)) {
            if (overwrite) {
                changes.put(key, new WorldChange(x, y, z, material, materialData));
            }
        } else {
            changes.put(key, new WorldChange(x, y, z, material, materialData));
        }
    }

    public void applyChanges(World world) {
        Set<WorldChangeKey> touchedChunks = new HashSet<WorldChangeKey>();

        for (WorldChange change : changes.values()) {
            Block block = world.getBlockAt(change.x, change.y, change.z);
            block.setType(change.material);
            block.setData(change.materialData, false);
            touchedChunks.add(new WorldChangeKey(block.getChunk().getX(), -1, block.getChunk().getZ()));
        }

        for (WorldChangeKey chunkKey : touchedChunks) {
            world.refreshChunk(chunkKey.x, chunkKey.z);
        }
    }
}
