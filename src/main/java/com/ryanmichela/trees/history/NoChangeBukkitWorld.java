/*
 * Copyright (C) 2014 Ryan Michela
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ryanmichela.trees.history;

import org.bukkit.World;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;

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
