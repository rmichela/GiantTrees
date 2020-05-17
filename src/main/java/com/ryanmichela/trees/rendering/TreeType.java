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

public class TreeType {

  public Material leafMaterial;
  public Material woodMaterial;

  public TreeType(final String treeType) {
    // "Oak","Spruce","Birch","Jungle","Acacia","Dark Oak"
    if (treeType.equals("Oak")) {
      this.woodMaterial = Material.OAK_LOG;
      this.leafMaterial = Material.OAK_LEAVES;
    } else if (treeType.equals("Spruce")) {
      this.woodMaterial = Material.SPRUCE_LOG;
      this.leafMaterial = Material.SPRUCE_LEAVES;
    } else if (treeType.equals("Birch")) {
      this.woodMaterial = Material.BIRCH_LOG;
      this.leafMaterial = Material.BIRCH_LEAVES;
    } else if (treeType.equals("Jungle")) {
      this.woodMaterial = Material.JUNGLE_LOG;
      this.leafMaterial = Material.JUNGLE_LEAVES;
    } else if (treeType.equals("Acacia")) {
      this.woodMaterial = Material.ACACIA_LOG;
      this.leafMaterial = Material.ACACIA_LEAVES;
    } else if (treeType.equals("Dark Oak")) {
      this.woodMaterial = Material.DARK_OAK_LOG;
      this.leafMaterial = Material.DARK_OAK_LEAVES;
    } else {
      this.woodMaterial = Material.OAK_LOG;
      this.leafMaterial = Material.OAK_LEAVES;
    }
  }
}
