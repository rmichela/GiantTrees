package com.ryanmichela.trees.rendering;

import org.bukkit.util.Vector;

/**
 * Copyright 2014 Ryan Michela
 */
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
