package com.ryanmichela.trees.rendering;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {
    private Map<WorldChangeKey, WorldChange> changes = new HashMap<WorldChangeKey, WorldChange>(10000);
    private CraftMassBlockUpdate massBlockUpdate;
    private MassBlockUpdate.RelightingStrategy relightingStrategy;

    public WorldChangeTracker(CraftMassBlockUpdate massBlockUpdate, MassBlockUpdate.RelightingStrategy relightingStrategy) {
        this.massBlockUpdate = massBlockUpdate;
        this.relightingStrategy = relightingStrategy;
    }

    public void addChange(Vector location, Material material, byte materialData, boolean overwrite) {
        addChange(new WorldChange(location, material, materialData), overwrite);
    }

    public void addChange(WorldChange worldChange, boolean overwrite) {
        WorldChangeKey key = new WorldChangeKey(worldChange.location.getBlockX(), worldChange.location.getBlockY(), worldChange.location.getBlockZ());
        if (changes.containsKey(key)) {
            if (overwrite) {
                changes.put(key, worldChange);
            }
        } else {
            changes.put(key, worldChange);
        }
    }

    public WorldChange getChange(WorldChangeKey key) {
        return changes.get(key);
    }

    public Collection<WorldChange> getChanges() {
        return changes.values();
    }

    public void applyChanges(Location refPoint) {
        System.out.println("Affected blocks: " + changes.size());
        if (relightingStrategy == MassBlockUpdate.RelightingStrategy.HYBRID || relightingStrategy == MassBlockUpdate.RelightingStrategy.DEFERRED) {
            massBlockUpdate.setDeferredBufferSize(changes.size());
        }
        for (WorldChange change : changes.values()) {
            Location changeLoc = refPoint.clone().add(change.location);
            int blockY = changeLoc.getBlockY();
            if (blockY <= 255 || blockY >= 0) {
                massBlockUpdate.setBlock(changeLoc.getBlockX(), changeLoc.getBlockY(), changeLoc.getBlockZ(), change.material.getId(), change.materialData);
            }
        }

        massBlockUpdate.notifyClients();
    }
}
