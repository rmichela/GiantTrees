package com.ryanmichela.trees.rendering;

import org.bukkit.Material;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreeType {

  public byte     dataOffset;
  public Material leafMaterial;
  public Material woodMaterial;

  public TreeType(final String treeType) {
    // "Oak","Spruce","Birch","Jungle","Acacia","Dark Oak"
    if (treeType.equals("Oak")) {
      this.woodMaterial = Material.LOG;
      this.leafMaterial = Material.LEAVES;
      this.dataOffset = 0;
    } else if (treeType.equals("Spruce")) {
      this.woodMaterial = Material.LOG;
      this.leafMaterial = Material.LEAVES;
      this.dataOffset = 1;
    } else if (treeType.equals("Birch")) {
      this.woodMaterial = Material.LOG;
      this.leafMaterial = Material.LEAVES;
      this.dataOffset = 2;
    } else if (treeType.equals("Jungle")) {
      this.woodMaterial = Material.LOG;
      this.leafMaterial = Material.LEAVES;
      this.dataOffset = 3;
    } else if (treeType.equals("Acacia")) {
      this.woodMaterial = Material.LOG_2;
      this.leafMaterial = Material.LEAVES_2;
      this.dataOffset = 0;
    } else if (treeType.equals("Dark Oak")) {
      this.woodMaterial = Material.LOG_2;
      this.leafMaterial = Material.LEAVES_2;
      this.dataOffset = 1;
    } else {
      this.woodMaterial = Material.LOG;
      this.leafMaterial = Material.LEAVES;
      this.dataOffset = 0;
    }
  }
}
