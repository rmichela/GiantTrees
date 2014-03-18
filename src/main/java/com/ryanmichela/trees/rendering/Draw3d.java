package com.ryanmichela.trees.rendering;


import net.sourceforge.arbaro.transformation.Vector;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2014 Ryan Michela
 */
public class Draw3d {
    Location refPoint;

    public Draw3d(Location refPoint) {
        this.refPoint = refPoint;
    }

    public void drawCone(Location pos1, double rad1, Location pos2, double rad2) {
        pos1.add(refPoint);
        pos2.add(refPoint);
        for(Location loc : line3d(pos1, pos2)) {
            refPoint.getWorld().getBlockAt(loc).setType(Material.LOG_2);
        }
    }

    public void drawSphere(Location pos, double rad) {
        pos.add(refPoint);
        refPoint.getWorld().getBlockAt(pos).setType(Material.LOG_2);
    }

    public void drawLeafCluster(Location pos) {
        pos.add(refPoint);
        refPoint.getWorld().getBlockAt(pos).setType(Material.LEAVES_2);
    }

    public Location toLoc(Vector vec) {
        return new Location(refPoint.getWorld(), vec.getX(), vec.getZ(), vec.getY());
    }

    private List<Location> line3d(Location l1, Location l2)
    {
        List<Location> locations = new LinkedList<Location>();

        int xd, yd, zd;
        int x, y, z;
        int ax, ay, az;
        int sx, sy, sz;
        int dx, dy, dz;

        dx = l2.getBlockX() - l1.getBlockX();
        dy = l2.getBlockY() - l1.getBlockY();
        dz = l2.getBlockZ() - l1.getBlockZ();

        ax = Math.abs(dx) << 1;
        ay = Math.abs(dy) << 1;
        az = Math.abs(dz) << 1;

        sx = Integer.signum(dx);
        sy = Integer.signum(dy);
        sz = Integer.signum(dz);

        x = l1.getBlockX();
        y = l1.getBlockY();
        z = l1.getBlockZ();

        if (ax >= Math.max(ay, az))            /* x dominant */
        {
            yd = ay - (ax >> 1);
            zd = az - (ax >> 1);
            for (;;)
            {
                locations.add(new Location(refPoint.getWorld(), x, y, z));
                if (x == l1.getBlockX())
                {
                    break;
                }

                if (yd >= 0)
                {
                    y += sy;
                    yd -= ax;
                }

                if (zd >= 0)
                {
                    z += sz;
                    zd -= ax;
                }

                x += sx;
                yd += ay;
                zd += az;
            }
        }
        else if (ay >= Math.max(ax, az))            /* y dominant */
        {
            xd = ax - (ay >> 1);
            zd = az - (ay >> 1);
            for (;;)
            {
                locations.add(new Location(refPoint.getWorld(), x, y, z));
                if (y == l2.getBlockY())
                {
                    break;
                }

                if (xd >= 0)
                {
                    x += sx;
                    xd -= ay;
                }

                if (zd >= 0)
                {
                    z += sz;
                    zd -= ay;
                }

                y += sy;
                xd += ax;
                zd += az;
            }
        }
        else if (az >= Math.max(ax, ay))            /* z dominant */
        {
            xd = ax - (az >> 1);
            yd = ay - (az >> 1);
            for (;;)
            {
                locations.add(new Location(refPoint.getWorld(), x, y, z));
                if (z == l2.getBlockZ())
                {
                    break;
                }

                if (xd >= 0)
                {
                    x += sx;
                    xd -= az;
                }

                if (yd >= 0)
                {
                    y += sy;
                    yd -= az;
                }

                z += sz;
                xd += ax;
                yd += ay;
            }
        }

        return locations;
    }
}
