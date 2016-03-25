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

import net.sourceforge.arbaro.tree.DefaultStemTraversal;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Subsegment;
import net.sourceforge.arbaro.tree.TraversalException;

import org.bukkit.util.Vector;

public class MinecraftSegmentExporter extends DefaultStemTraversal {

  private final Draw3d d3d;

  public MinecraftSegmentExporter(final Draw3d d3d) {
    super();
    this.d3d = d3d;
  }

  @Override
  public boolean enterSegment(final Segment s) throws TraversalException {
    final int level = s.lpar.level;

    for (int i = 0; i < (s.subsegments.size() - 1); i++) {
      final Subsegment ss1 = (Subsegment) s.subsegments.elementAt(i);
      final Subsegment ss2 = (Subsegment) s.subsegments.elementAt(i + 1);

      final Vector l1 = this.d3d.toMcVector(ss1.pos);
      final Vector l2 = this.d3d.toMcVector(ss2.pos);
      final Orientation orientation = Orientation.orient(l1, l2);

      this.d3d.drawCone(l1, ss1.rad, l2, ss2.rad, level);

      if (l1.subtract(l2).length() > 1) {
        this.d3d.drawWoodSphere(l2, ss2.rad, orientation, level);
      }
    }

    return true;
  }

  @Override
  public boolean
      visitSubsegment(final Subsegment subsegment) throws TraversalException {
    // do nothing with subsegments at the moment
    return false;
  }
}
