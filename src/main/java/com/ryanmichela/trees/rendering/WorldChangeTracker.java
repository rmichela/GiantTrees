package com.ryanmichela.trees.rendering;

import com.ryanmichela.trees.history.WorldEditHistoryTracker;
import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {
    private Plugin plugin;
    private Map<WorldChangeKey, WorldChange> changes = new HashMap<WorldChangeKey, WorldChange>(10000);
    private CraftMassBlockUpdate massBlockUpdate;
    private MassBlockUpdate.RelightingStrategy relightingStrategy;
    private boolean recordHistory;

    private int BLOCKS_PER_TICK;
    private int TICK_DELAY;

    public WorldChangeTracker(Plugin plugin, CraftMassBlockUpdate massBlockUpdate, MassBlockUpdate.RelightingStrategy relightingStrategy, boolean recordHistory) {
        this.plugin = plugin;
        this.massBlockUpdate = massBlockUpdate;
        this.relightingStrategy = relightingStrategy;
        this.recordHistory = recordHistory;

        BLOCKS_PER_TICK = plugin.getConfig().getInt("BLOCKS_PER_TICK", 2500);
        TICK_DELAY = plugin.getConfig().getInt("TICK_DELAY", 1);

        if (BLOCKS_PER_TICK < 1) BLOCKS_PER_TICK = 1;
        if (TICK_DELAY < 1) TICK_DELAY = 1;
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

    public void applyChanges(Location refPoint, Player byPlayer) {
        if (relightingStrategy == MassBlockUpdate.RelightingStrategy.HYBRID || relightingStrategy == MassBlockUpdate.RelightingStrategy.DEFERRED) {
            massBlockUpdate.setDeferredBufferSize(changes.size());
        }

        final WorldEditHistoryTracker historyTracker =
                (recordHistory && Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")) ?
                        new WorldEditHistoryTracker(refPoint, byPlayer) : null;

        WorldChange[] changesArray = changes.values().toArray(new WorldChange[changes.values().size()]);
        int i;
        for (i = 0; (i + 1) * BLOCKS_PER_TICK < changesArray.length; i++) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Changer(changesArray, refPoint, historyTracker, i*BLOCKS_PER_TICK, BLOCKS_PER_TICK), i* TICK_DELAY);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Changer(changesArray, refPoint, historyTracker, i*BLOCKS_PER_TICK, changesArray.length - (i*BLOCKS_PER_TICK)), (i+1)* TICK_DELAY);


        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (historyTracker != null) {
                    historyTracker.finalizeHistoricChanges();
                }
                massBlockUpdate.notifyClients();
                logVerbose("Affected blocks: " + changes.size());
            }
        }, (i+2)* TICK_DELAY);

    }

    private class Changer implements Runnable {
        private WorldChange[] changes;
        private Location refPoint;
        private WorldEditHistoryTracker historyTracker;
        private int offset;
        private int count;

        private Changer(WorldChange[] changes, Location refPoint, WorldEditHistoryTracker historyTracker, int offset, int count) {
            this.changes = changes;
            this.refPoint = refPoint;
            this.historyTracker = historyTracker;
            this.offset = offset;
            this.count = count;
        }

        @Override
        public void run() {
            for(int i = offset; i < offset + count; i++) {
                WorldChange change = changes[i];
                Location changeLoc = refPoint.clone().add(change.location);
                int blockY = changeLoc.getBlockY();
                ensureChunkLoaded(changeLoc.getChunk());
                if (blockY <= 255 && blockY >= 0) {
                    if (historyTracker != null) {
                        historyTracker.recordHistoricChange(changeLoc, change.material.getId(), change.materialData);
                    }
                    massBlockUpdate.setBlock(changeLoc.getBlockX(), blockY, changeLoc.getBlockZ(), change.material.getId(), change.materialData);
                }
            }
        }

        private void ensureChunkLoaded(Chunk chunk) {
            if (!chunk.isLoaded()) {
                if (!chunk.load()) {
                    plugin.getLogger().severe("Could not load chunk " + chunk.toString());
                }
            }
        }
    }

    private void logVerbose(String message) {
        if (plugin.getConfig().getBoolean("verbose-logging", false)) {
            plugin.getLogger().info(message);
        }
    }
}
