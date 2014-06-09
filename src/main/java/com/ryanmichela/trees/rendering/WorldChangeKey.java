package com.ryanmichela.trees.rendering;

import org.bukkit.util.Vector;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeKey {
    public int x;
    public int y;
    public int z;

    public WorldChangeKey() {}

    public WorldChangeKey(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldChangeKey that = (WorldChangeKey) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
