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

import net.sourceforge.arbaro.tree.Leaf;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.TraversalException;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeTraversal;

public class MinecraftStemExporter implements TreeTraversal {

  private final Draw3d d3d;

  private final int    level;
  private Tree         tree;

  public MinecraftStemExporter(final Draw3d d3d, final int level) {
    this.d3d = d3d;
    this.level = level;
  }

  @Override
  public boolean enterStem(final Stem stem) throws TraversalException {
    if ((stem.stemlevel < this.level) || (this.level == -1)) {
      final MinecraftSegmentExporter exporter = new MinecraftSegmentExporter(
                                                                             this.d3d);
      stem.traverseStem(exporter);
    }
    return true;
  }

  @Override
  public boolean enterTree(final Tree tree) throws TraversalException {
    this.tree = tree;
    return true;
  }

  @Override
  public boolean leaveStem(final Stem stem) throws TraversalException {
    return true;
  }

  @Override
  public boolean leaveTree(final Tree tree) throws TraversalException {
    return true;
  }

  @Override
  public boolean visitLeaf(final Leaf leaf) throws TraversalException {
    // don't render leaves here
    return false;
  }
}
