package com.ryanmichela.trees.rendering;

import org.bukkit.Material;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreeType {
    public Material woodMaterial;
    public Material leafMaterial;
    public byte dataOffset;

    public TreeType(String treeType) {
        //"Oak","Spruce","Birch","Jungle","Acacia","Dark Oak"
        if (treeType.equals("Oak")) {
            woodMaterial = Material.LOG;
            leafMaterial = Material.LEAVES;
            dataOffset = 0;
        } else if (treeType.equals("Spruce")) {
            woodMaterial = Material.LOG;
            leafMaterial = Material.LEAVES;
            dataOffset = 1;
        } else if (treeType.equals("Birch")) {
            woodMaterial = Material.LOG;
            leafMaterial = Material.LEAVES;
            dataOffset = 2;
        } else if (treeType.equals("Jungle")) {
            woodMaterial = Material.LOG;
            leafMaterial = Material.LEAVES;
            dataOffset = 3;
        } else if (treeType.equals("Acacia")) {
            woodMaterial = Material.LOG_2;
            leafMaterial = Material.LEAVES_2;
            dataOffset = 0;
        } else if (treeType.equals("Dark Oak")) {
            woodMaterial = Material.LOG_2;
            leafMaterial = Material.LEAVES_2;
            dataOffset = 1;
        } else {
            woodMaterial = Material.LOG;
            leafMaterial = Material.LEAVES;
            dataOffset = 0;
        }
    }
}
