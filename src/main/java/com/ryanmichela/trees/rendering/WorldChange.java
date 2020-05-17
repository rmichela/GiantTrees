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
package com.ryanmichela.trees.rendering;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

class WorldChange {

  public final Material material;
  public final Consumer<BlockData> blockDataMutator;
  public final Vector location;

  WorldChange(final Vector location, final Material material,
              final Consumer<BlockData> blockDataMutator) {
    this.location = location;
    this.material = material;
    this.blockDataMutator = blockDataMutator;
  }
}
