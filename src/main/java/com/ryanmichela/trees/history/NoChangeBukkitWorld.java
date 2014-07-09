package com.ryanmichela.trees.history;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.foundation.Block;
import org.bukkit.World;

/**
 * Copyright 2014 Ryan Michela
 */
public class NoChangeBukkitWorld extends BukkitWorld {
    private boolean doneUpdating = false;

    public NoChangeBukkitWorld(World world) {
        super(world);
    }

    public void enableUndo() {
        doneUpdating = true;
    }

    @Override
    public boolean setBlock(Vector pt, Block block, boolean notifyAdjacent) {
        if (doneUpdating) return super.setBlock(pt, block, notifyAdjacent);
        else return true;
    }

    @Override
    public void setBlockData(Vector pt, int data) {
        if (doneUpdating) super.setBlockData(pt, data);
    }

    @Override
    public void setBlockDataFast(Vector pt, int data) {
        if (doneUpdating) super.setBlockDataFast(pt, data);
    }

    @Override
    public boolean setBlockType(Vector pt, int type) {
        if (doneUpdating) return super.setBlockType(pt, type);
        else return true;
    }

    @Override
    public boolean setBlockTypeFast(Vector pt, int type) {
        if (doneUpdating) return super.setBlockTypeFast(pt, type);
        else return true;
    }

    @Override
    public boolean setTypeIdAndData(Vector pt, int type, int data) {
        if (doneUpdating) return super.setTypeIdAndData(pt, type, data);
        else return true;
    }

    @Override
    public boolean setTypeIdAndDataFast(Vector pt, int type, int data) {
        if (doneUpdating) return super.setTypeIdAndDataFast(pt, type, data);
        else return true;
    }
}
