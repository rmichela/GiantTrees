package com.ryanmichela.trees.history;

import org.bukkit.World;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;

/**
 * Copyright 2014 Ryan Michela
 */
public class NoChangeBukkitWorld extends BukkitWorld {

  private boolean doneUpdating = false;

  public NoChangeBukkitWorld(final World world) {
    super(world);
  }

  public void enableUndo() {
    this.doneUpdating = true;
  }

  @Override
  public boolean
      setBlock(final Vector pt, final BaseBlock block,
               final boolean notifyAdjacent) throws WorldEditException {
    if (this.doneUpdating) {
      return super.setBlock(pt, block, notifyAdjacent);
    } else {
      return true;
    }
  }

}
