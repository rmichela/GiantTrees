package com.ryanmichela.trees.rendering;

import org.bukkit.Material;

/**
* Copyright 2014 Ryan Michela
*/
class WorldChange {
    public int x;
    public int y;
    public int z;
    public Material material;
    public byte materialData;

    WorldChange(int x, int y, int z, Material material, byte materialData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
        this.materialData = materialData;
    }
}
