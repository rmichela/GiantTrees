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

public class WorldChangeKey {

  public int x;
  public int y;
  public int z;

  public WorldChangeKey() {
  }

  public WorldChangeKey(final int x, final int y, final int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if ((o == null) || (this.getClass() != o.getClass())) { return false; }

    final WorldChangeKey that = (WorldChangeKey) o;

    if (this.x != that.x) { return false; }
    if (this.y != that.y) { return false; }
    if (this.z != that.z) { return false; }

    return true;
  }

  @Override
  public int hashCode() {
    int result = this.x;
    result = (31 * result) + this.y;
    result = (31 * result) + this.z;
    return result;
  }

  public Vector toVector() {
    return new Vector(this.x, this.y, this.z);
  }
}
