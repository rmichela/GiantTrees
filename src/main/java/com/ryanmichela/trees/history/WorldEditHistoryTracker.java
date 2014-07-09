package com.ryanmichela.trees.history;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldEditHistoryTracker {
    private WorldEditPlugin wePlugin;
    private Player forPlayer;
    private EditSession activeEditSession;
    private NoChangeBukkitWorld localWorld;

    public WorldEditHistoryTracker(Location refPoint, Player forPlayer) {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null) {
            throw new IllegalStateException("WorldEdit not loaded. Cannot create WorldEditHistoryTracker");
        }
        wePlugin = (WorldEditPlugin) plugin;

        localWorld = new NoChangeBukkitWorld(refPoint.getWorld());
        activeEditSession = new EditSession(localWorld, Integer.MAX_VALUE);
        activeEditSession.enableQueue();
        this.forPlayer = forPlayer;
    }

    public void recordHistoricChange(Location changeLoc, int materialId, byte materialData) {
        try {
            com.sk89q.worldedit.Vector weVector = BukkitUtil.toVector(changeLoc);
            activeEditSession.setBlock(weVector, new BaseBlock(materialId, materialData));
        } catch (MaxChangedBlocksException e) {
            Bukkit.getLogger().severe("MaxChangedBlocksException!");
        }
    }

    public void finalizeHistoricChanges()
    {
        BukkitPlayer localPlayer = new BukkitPlayer(wePlugin, null, forPlayer);
        LocalSession localSession = wePlugin.getWorldEdit().getSession(localPlayer);
        activeEditSession.flushQueue();
        localSession.remember(activeEditSession);
        localWorld.enableUndo();
    }
}
