package com.ryanmichela.trees.rendering;

import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * Copyright 2014 Ryan Michela
 */
class WorldChange {

  public Material material;
  public byte     materialData;
  Vector          location;

  WorldChange(final Vector location, final Material material,
              final byte materialData) {
    this.location = location;
    this.material = material;
    this.materialData = materialData;
  }
}
