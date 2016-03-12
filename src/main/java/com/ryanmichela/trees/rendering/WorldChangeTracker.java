package com.ryanmichela.trees.rendering;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.ryanmichela.trees.history.WorldEditHistoryTracker;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {

  private class Changer implements Runnable {

    private final WorldChange[]           changes;
    private final int                     count;
    private final WorldEditHistoryTracker historyTracker;
    private final int                     offset;
    private final Location                refPoint;

    private Changer(final WorldChange[] changes, final Location refPoint,
                    final WorldEditHistoryTracker historyTracker,
                    final int offset, final int count) {
      this.changes = changes;
      this.refPoint = refPoint;
      this.historyTracker = historyTracker;
      this.offset = offset;
      this.count = count;
    }

    @Override
    public void run() {
      for (int i = this.offset; i < (this.offset + this.count); i++) {
        final WorldChange change = this.changes[i];
        final Location changeLoc = this.refPoint.clone().add(change.location);
        final int blockY = changeLoc.getBlockY();
        this.ensureChunkLoaded(changeLoc.getChunk());
        if ((blockY <= 255) && (blockY >= 0)) {
          if (this.historyTracker != null) {
            this.historyTracker.recordHistoricChange(changeLoc,
                                                     change.material.getId(),
                                                     change.materialData);
          }
          WorldChangeTracker.this.massBlockUpdate.setBlock(changeLoc.getBlockX(),
                                                           blockY,
                                                           changeLoc.getBlockZ(),
                                                           change.material.getId(),
                                                           change.materialData);
        }
      }
    }

    private void ensureChunkLoaded(final Chunk chunk) {
      if (!chunk.isLoaded()) {
        if (!chunk.load()) {
          WorldChangeTracker.this.plugin.getLogger()
                                        .severe("Could not load chunk "
                                                    + chunk.toString());
        }
      }
    }
  }

  private int                                      BLOCKS_PER_TICK;
  private final Map<WorldChangeKey, WorldChange>   changes = new HashMap<WorldChangeKey, WorldChange>(
                                                                                                      10000);
  private final CraftMassBlockUpdate               massBlockUpdate;
  private final Plugin                             plugin;

  private final boolean                            recordHistory;
  private final MassBlockUpdate.RelightingStrategy relightingStrategy;

  private int                                      TICK_DELAY;

  public WorldChangeTracker(final Plugin plugin,
                            final CraftMassBlockUpdate massBlockUpdate,
                            final MassBlockUpdate.RelightingStrategy relightingStrategy,
                            final boolean recordHistory) {
    this.plugin = plugin;
    this.massBlockUpdate = massBlockUpdate;
    this.relightingStrategy = relightingStrategy;
    this.recordHistory = recordHistory;

    this.BLOCKS_PER_TICK = plugin.getConfig().getInt("BLOCKS_PER_TICK", 2500);
    this.TICK_DELAY = plugin.getConfig().getInt("TICK_DELAY", 1);

    if (this.BLOCKS_PER_TICK < 1) {
      this.BLOCKS_PER_TICK = 1;
    }
    if (this.TICK_DELAY < 1) {
      this.TICK_DELAY = 1;
    }
  }

  public void addChange(final Vector location, final Material material,
                        final byte materialData, final boolean overwrite) {
    this.addChange(new WorldChange(location, material, materialData), overwrite);
  }

  public void addChange(final WorldChange worldChange, final boolean overwrite) {
    final WorldChangeKey key = new WorldChangeKey(
                                                  worldChange.location.getBlockX(),
                                                  worldChange.location.getBlockY(),
                                                  worldChange.location.getBlockZ());
    if (this.changes.containsKey(key)) {
      if (overwrite) {
        this.changes.put(key, worldChange);
      }
    } else {
      this.changes.put(key, worldChange);
    }
  }

  public void applyChanges(final Location refPoint, final Player byPlayer) {
    if ((this.relightingStrategy == MassBlockUpdate.RelightingStrategy.HYBRID)
        || (this.relightingStrategy == MassBlockUpdate.RelightingStrategy.DEFERRED)) {
      this.massBlockUpdate.setDeferredBufferSize(this.changes.size());
    }

    final WorldEditHistoryTracker historyTracker = (this.recordHistory && Bukkit.getServer()
                                                                                .getPluginManager()
                                                                                .isPluginEnabled("WorldEdit")) ? new WorldEditHistoryTracker(
                                                                                                                                             refPoint,
                                                                                                                                             byPlayer)
                                                                                                              : null;

    final WorldChange[] changesArray = this.changes.values()
                                                   .toArray(new WorldChange[this.changes.values()
                                                                                        .size()]);
    int i;
    for (i = 0; ((i + 1) * this.BLOCKS_PER_TICK) < changesArray.length; i++) {
      this.plugin.getServer()
                 .getScheduler()
                 .scheduleSyncDelayedTask(this.plugin,
                                          new Changer(changesArray, refPoint,
                                                      historyTracker,
                                                      i * this.BLOCKS_PER_TICK,
                                                      this.BLOCKS_PER_TICK),
                                          i * this.TICK_DELAY);
    }

    this.plugin.getServer()
               .getScheduler()
               .scheduleSyncDelayedTask(this.plugin,
                                        new Changer(
                                                    changesArray,
                                                    refPoint,
                                                    historyTracker,
                                                    i * this.BLOCKS_PER_TICK,
                                                    changesArray.length
                                                        - (i * this.BLOCKS_PER_TICK)),
                                        (i + 1) * this.TICK_DELAY);

    this.plugin.getServer().getScheduler()
               .scheduleSyncDelayedTask(this.plugin, new Runnable() {

                 @Override
                 public void run() {
                   if (historyTracker != null) {
                     historyTracker.finalizeHistoricChanges();
                   }
                   WorldChangeTracker.this.massBlockUpdate.notifyClients();
                   WorldChangeTracker.this.logVerbose("Affected blocks: "
                                                      + WorldChangeTracker.this.changes.size());
                 }
               }, (i + 2) * this.TICK_DELAY);

  }

  public WorldChange getChange(final WorldChangeKey key) {
    return this.changes.get(key);
  }

  public Collection<WorldChange> getChanges() {
    return this.changes.values();
  }

  private void logVerbose(final String message) {
    if (this.plugin.getConfig().getBoolean("verbose-logging", false)) {
      this.plugin.getLogger().info(message);
    }
  }
}
