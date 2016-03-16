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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;

public class JungleVinePopulator {

  public static void populate(final WorldChangeTracker tracker, final Random r) {
    final WorldChangeKey north = new WorldChangeKey();
    final WorldChangeKey south = new WorldChangeKey();
    final WorldChangeKey east = new WorldChangeKey();
    final WorldChangeKey west = new WorldChangeKey();

    final List<WorldChange> newChanges = new LinkedList<>();

    for (final WorldChange change : tracker.getChanges()) {
      if ((change.material == Material.LOG)
          || (change.material == Material.LOG_2)) {
        north.x = change.location.getBlockX();
        north.y = change.location.getBlockY();
        north.z = change.location.getBlockZ() - 1;
        south.x = change.location.getBlockX();
        south.y = change.location.getBlockY();
        south.z = change.location.getBlockZ() + 1;
        east.x = change.location.getBlockX() + 1;
        east.y = change.location.getBlockY();
        east.z = change.location.getBlockZ();
        west.x = change.location.getBlockX() - 1;
        west.y = change.location.getBlockY();
        west.z = change.location.getBlockZ();

        if ((r.nextInt(3) > 0) && (tracker.getChange(north) == null)) {
          newChanges.add(new WorldChange(north.toVector(), Material.VINE,
                                         (byte) 1));
        }
        if ((r.nextInt(3) > 0) && (tracker.getChange(south) == null)) {
          newChanges.add(new WorldChange(south.toVector(), Material.VINE,
                                         (byte) 4));
        }
        if ((r.nextInt(3) > 0) && (tracker.getChange(east) == null)) {
          newChanges.add(new WorldChange(east.toVector(), Material.VINE,
                                         (byte) 2));
        }
        if ((r.nextInt(3) > 0) && (tracker.getChange(west) == null)) {
          newChanges.add(new WorldChange(west.toVector(), Material.VINE,
                                         (byte) 8));
        }
      }
    }

    for (final WorldChange newChange : newChanges) {
      tracker.addChange(newChange, false);
    }
  }
}
