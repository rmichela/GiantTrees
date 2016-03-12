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

import net.sourceforge.arbaro.export.ExportError;
import net.sourceforge.arbaro.export.Exporter;
import net.sourceforge.arbaro.tree.TraversalException;
import net.sourceforge.arbaro.tree.Tree;

public class MinecraftExporter extends Exporter {

  private final Draw3d d3d;

  public MinecraftExporter(final Tree tree, final Draw3d draw3d) {
    super(tree, null, null);
    this.d3d = draw3d;
  }

  @Override
  public void write() throws ExportError {
    try {
      // stems
      final MinecraftStemExporter exporter = new MinecraftStemExporter(
                                                                       this.d3d,
                                                                       this.tree.params.stopLevel);
      this.tree.traverseTree(exporter);

      // leaves
      if (this.tree.params.Leaves > 0) {
        final MinecraftLeafExporter lexporter = new MinecraftLeafExporter(
                                                                          this.d3d);
        this.tree.traverseTree(lexporter);
      }
    } catch (final TraversalException e) {
      e.printStackTrace();
    }
  }
}
