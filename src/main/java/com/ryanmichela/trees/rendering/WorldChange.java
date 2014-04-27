package com.ryanmichela.trees.rendering;

import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
* Copyright 2014 Ryan Michela
*/
class WorldChange {
    Vector location;
    public Material material;
    public byte materialData;

    WorldChange(Vector location, Material material, byte materialData) {
        this.location = location;
        this.material = material;
        this.materialData = materialData;
    }
}
