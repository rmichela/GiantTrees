package com.ryanmichela.trees.rendering;

import org.bukkit.util.Vector;

/**
 * Copyright 2014 Ryan Michela
 */
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
