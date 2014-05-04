package com.ryanmichela.trees.rendering;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {
    private Map<WorldChangeKey, WorldChange> changes = new HashMap<WorldChangeKey, WorldChange>(1000);
    private CraftMassBlockUpdate massBlockUpdate;

    public WorldChangeTracker(CraftMassBlockUpdate massBlockUpdate) {
        this.massBlockUpdate = massBlockUpdate;
    }

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
        System.out.println("Affected blocks: " + changes.size());
        massBlockUpdate.setDeferredBufferSize(changes.size());
        for (WorldChange change : changes.values()) {
            Location changeLoc = refPoint.clone().add(change.location);
            massBlockUpdate.setBlock(changeLoc.getBlockX(), changeLoc.getBlockY(), changeLoc.getBlockZ(), change.material.getId(), change.materialData);
        }

        massBlockUpdate.notifyClients();
    }
}
