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

import org.bukkit.util.Vector;

public enum Orientation {
  xMajor, yMajor, zMajor;

  public static Orientation orient(final Vector l1, final Vector l2) {
    final double dx = Math.abs(l2.getX() - l1.getX());
    final double dy = Math.abs(l2.getY() - l1.getY());
    final double dz = Math.abs(l2.getZ() - l1.getZ());

    if (dx >= Math.max(dy, dz)) { return Orientation.xMajor; }
    if (dy >= Math.max(dx, dz)) { return Orientation.yMajor; }
    if (dz >= Math.max(dx, dy)) { return Orientation.zMajor; }
    return Orientation.yMajor;
  }
}
