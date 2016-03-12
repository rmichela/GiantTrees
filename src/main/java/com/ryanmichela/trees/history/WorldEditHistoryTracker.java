package com.ryanmichela.trees.history;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldEditHistoryTracker {

  private final EditSession         activeEditSession;
  private final Player              forPlayer;
  private final NoChangeBukkitWorld localWorld;
  private final WorldEditPlugin     wePlugin;

  public WorldEditHistoryTracker(final Location refPoint, final Player forPlayer) {
    final Plugin plugin = Bukkit.getServer().getPluginManager()
                                .getPlugin("WorldEdit");
    if (plugin == null) { throw new IllegalStateException(
                                                          "WorldEdit not loaded. Cannot create WorldEditHistoryTracker"); }
    this.wePlugin = (WorldEditPlugin) plugin;

    this.localWorld = new NoChangeBukkitWorld(refPoint.getWorld());
    this.activeEditSession = new EditSession(this.localWorld, Integer.MAX_VALUE);
    this.activeEditSession.enableQueue();
    this.activeEditSession.setMask((com.sk89q.worldedit.function.mask.Mask) null);
    this.activeEditSession.setFastMode(true);
    this.forPlayer = forPlayer;
  }

  public void finalizeHistoricChanges() {
    final BukkitPlayer localPlayer = new BukkitPlayer(this.wePlugin, null,
                                                      this.forPlayer);
    final LocalSession localSession = this.wePlugin.getWorldEdit()
                                                   .getSession(localPlayer);
    this.activeEditSession.flushQueue();
    localSession.remember(this.activeEditSession);
    this.localWorld.enableUndo();
  }

  public int getBlockChangeCount() {
    return this.activeEditSession.getBlockChangeCount();
  }

  public int getSize() {
    return this.activeEditSession.size();
  }

  public void
      recordHistoricChange(final Location changeLoc, final int materialId,
                           final byte materialData) {
    try {
      final com.sk89q.worldedit.Vector weVector = new Vector(
                                                             changeLoc.getBlockX(),
                                                             changeLoc.getBlockY(),
                                                             changeLoc.getBlockZ());
      this.activeEditSession.setBlock(weVector, new BaseBlock(materialId,
                                                              materialData));
    } catch (final MaxChangedBlocksException e) {
      Bukkit.getLogger().severe("MaxChangedBlocksException!");
    }
  }
}
