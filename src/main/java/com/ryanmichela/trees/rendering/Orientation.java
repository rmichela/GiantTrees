package com.ryanmichela.trees.rendering;

import org.bukkit.Location;

/**
 * Copyright 2014 Ryan Michela
 */
public enum Orientation {
    xMajor,
    yMajor,
    zMajor;

    public static Orientation orient(Location l1, Location l2) {
        double dx = Math.abs(l2.getX() - l1.getX());
        double dy = Math.abs(l2.getY() - l1.getY());
        double dz = Math.abs(l2.getZ() - l1.getZ());

        if (dx >= Math.max(dy, dz)) return Orientation.xMajor;
        if (dy >= Math.max(dx, dz)) return Orientation.yMajor;
        if (dz >= Math.max(dx, dy)) return Orientation.zMajor;
        return Orientation.yMajor;
    }
}
