package com.ryanmichela.trees.history;

import org.bukkit.World;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.foundation.Block;

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
	public boolean setBlock(Vector pt, Block block, boolean notifyAdjacent) throws WorldEditException {
    	if (doneUpdating) return super.setBlock(pt, block, notifyAdjacent);
    	else return true;
	}
    
    @Override
    public boolean setBlock(Vector pt, BaseBlock block, boolean notifyAdjacent) throws WorldEditException {
        if (doneUpdating) return super.setBlock(pt, block, notifyAdjacent);
        else return true;
    }

}
