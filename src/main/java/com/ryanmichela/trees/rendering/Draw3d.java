package com.ryanmichela.trees.rendering;


import net.sourceforge.arbaro.transformation.Vector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

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

    public void drawCone(Location l1, double rad1, Location l2, double rad2) {
        l1.add(refPoint);
        l2.add(refPoint);

        List<Location> locations = new LinkedList<Location>();
        Orientation orientation = orient(l1, l2);
        List<Location> centerLine = line3d(l1, l2, orientation);

        // Circle stuff
        double h = l1.distance(l2);
        double ht = (-rad1 * h) / (rad2 - rad1);
        for (int i = 0; i < centerLine.size(); i++) {
            Location centerPoint = centerLine.get(i);
            int r = (int)Math.ceil(rad1 * (ht - i) / ht);

            List<Point2D> circle2d;
            switch(orientation) {
                case xMajor:
                    circle2d = drawCircle(centerPoint.getBlockY(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        locations.add(new Location(centerPoint.getWorld(),
                                centerPoint.getBlockX(),
                                l1.getBlockY() - centerPoint.getBlockY() + p.p,
                                l1.getBlockZ() - centerPoint.getBlockZ() + p.q));
                    }
                    break;
                case yMajor:
                    circle2d = drawCircle(centerPoint.getBlockX(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        locations.add(new Location(centerPoint.getWorld(),
                                l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                centerPoint.getBlockY(),
                                l1.getBlockZ() - centerPoint.getBlockZ() + p.q));
                    }
                    break;
                case zMajor:
                    circle2d = drawCircle(centerPoint.getBlockX(), centerPoint.getBlockY(), r);
                    for (Point2D p : circle2d) {
                        locations.add(new Location(centerPoint.getWorld(),
                                l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                l1.getBlockY() - centerPoint.getBlockY() + p.q,
                                centerPoint.getBlockZ()));
                    }
                    break;
            }
        }

        for(Location loc : locations) {
            Block b = refPoint.getWorld().getBlockAt(loc);
            b.setType(Material.LOG);
            b.setData(LogData((byte)0, orientation));
        }
    }

    private byte LogData(byte baseData, Orientation orientation) {
        switch (orientation) {
            case xMajor:
                return (byte)(baseData + 4);
            case yMajor:
                return baseData;
            case zMajor:
                return (byte)(baseData + 8);
            default:
                return baseData;
        }
    }

    public void drawSphere(Location pos, double rad) {
        pos.add(refPoint);
        refPoint.getWorld().getBlockAt(pos).setType(Material.LOG);
    }

    public void drawLeafCluster(Location pos) {
        pos.add(refPoint);
        Block b = refPoint.getWorld().getBlockAt(pos);
        if (b.getType() == Material.AIR) {
            b.setType(Material.LEAVES);
            b.setData((byte)4);
        }
    }

    public Location toLoc(Vector vec) {
        return new Location(refPoint.getWorld(), vec.getX(), vec.getZ(), vec.getY());
    }

    public List<Location> line3d(Location l1, Location l2) {
        return line3d(l1, l2, orient(l1, l2));
    }

    private List<Location> line3d(Location l1, Location l2, Orientation orientation)
    {
        List<Location> locations = new LinkedList<Location>();
        if (orientation == Orientation.xMajor)            /* x dominant */
        {
            List<Point2D> xy = line2d(l1.getBlockX(), l1.getBlockY(), l2.getBlockX(), l2.getBlockZ());
            List<Point2D> xz = line2d(l1.getBlockX(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockZ());
            for(int i = 0; i < Math.min(xy.size(), xz.size()); i++) {
                locations.add(new Location(refPoint.getWorld(), l1.getBlockX() + i, xy.get(i).q, xz.get(i).q));
            }
        }
        else if (orientation == Orientation.yMajor)            /* y dominant */
        {
            List<Point2D> yx = line2d(l1.getBlockY(), l1.getBlockX(), l2.getBlockY(), l2.getBlockX());
            List<Point2D> yz = line2d(l1.getBlockY(), l1.getBlockZ(), l2.getBlockY(), l2.getBlockZ());
            for(int i = 0; i < Math.min(yx.size(), yz.size()); i++) {
                locations.add(new Location(refPoint.getWorld(), yx.get(i).q, l1.getBlockY() + i, yz.get(i).q));
            }
        }
        else if (orientation == Orientation.zMajor)            /* z dominant */
        {
            List<Point2D> zx = line2d(l1.getBlockZ(), l1.getBlockX(), l2.getBlockZ(), l2.getBlockX());
            List<Point2D> zy = line2d(l1.getBlockZ(), l1.getBlockY(), l2.getBlockZ(), l2.getBlockY());
            for(int i = 0; i < Math.min(zx.size(), zy.size()); i++) {
                locations.add(new Location(refPoint.getWorld(), zx.get(i).q, zy.get(i).q, l1.getBlockZ() + i));
            }
        }

        return locations;
    }

    public class Point2D {
        public int p;
        public int q;

        public Point2D(int p, int q) {
            this.p = p;
            this.q = q;
        }
    }

    public List<Point2D> line2d(int x1, int y1, int x2, int y2) {
        List<Point2D> points2d = new LinkedList<Point2D>();
        // Bresenham's Line Algorithm
        int currentX, currentY;
        int xInc, yInc;
        int dx, dy;
        int twoDx, twoDy;
        int twoDxAccumulatedError;
        int twoDyAccumulatedError;

        dx = x2 - x1;
        dy = y2 - y1;
        twoDx = dx + dx;
        twoDy = dy + dy;
        currentX = x1; // start at (X1,Y1) and move towards (X2,Y2)
        currentY = y1;
        xInc = 1; // X and/or Y are incremented/decremented by 1 only
        yInc = 1;
        if (dx < 0) {
            xInc = -1; // decrement X's
            dx = -dx;
            twoDx = -twoDx;
        }
        if (dy < 0) { // insure Dy >= 0
            yInc = -1;
            dy = -dy;
            twoDy = -twoDy;
        }
        points2d.add(new Point2D(x1, y1));
        if (dx != 0 || dy != 0) { // are other points on the line ?
            if (dy <= dx) { //  is the slope <= 1 ?
                twoDxAccumulatedError = 0; // initialize the error
                do {
                    currentX += xInc; // consider X's from X1 to X2
                    twoDxAccumulatedError += twoDy;
                    if (twoDxAccumulatedError > dx) {
                        currentY += yInc;
                        twoDxAccumulatedError -= twoDx;
                    }
                    points2d.add(new Point2D(currentX, currentY));
                } while (currentX != x2);
            } else { // then the slope is large, reverse roles of X & Y
                twoDyAccumulatedError = 0; // initialize the error
                do {
                    currentY += yInc; // consider Y's from Y1 to Y2
                    twoDyAccumulatedError += twoDx;
                    if (twoDyAccumulatedError > dy) {
                        currentX += xInc;
                        twoDyAccumulatedError -= twoDy;
                    }
                    points2d.add(new Point2D(currentX, currentY));
                } while (currentY != y2);
            }
        }
        return points2d;
    }

    public List<Point2D> drawCircle(int cx, int cy, int r) {
        List<Point2D> points2d = new LinkedList<Point2D>();
        int x, y;
        int xChange, yChange;
        int radiusError;
        x = r;
        y = 0;
        xChange = 1-2*r;
        yChange = 1;
        radiusError = 0;
        while (x >= y) {
            points2d.add(new Point2D(cx + x, cy + y));
            points2d.add(new Point2D(cx - x, cy + y));
            points2d.add(new Point2D(cx - x, cy - y));
            points2d.add(new Point2D(cx + x, cy - y));
            points2d.add(new Point2D(cx + y, cy + x));
            points2d.add(new Point2D(cx - y, cy + x));
            points2d.add(new Point2D(cx - y, cy - y));
            points2d.add(new Point2D(cx + y, cy - x));
            y++;
            radiusError += yChange;
            yChange += 2;
            if (2*radiusError + xChange > 0) {
                x--;
                radiusError += xChange;
                xChange += 2;
            }
        }
        return points2d;
    }

    private enum Orientation {
        xMajor,
        yMajor,
        zMajor
    }

    public Orientation orient(Location l1, Location l2) {
        List<Location> locations = new LinkedList<Location>();
        double dx = Math.abs(l2.getX() - l1.getX());
        double dy = Math.abs(l2.getY() - l1.getY());
        double dz = Math.abs(l2.getZ() - l1.getZ());

        if (dx >= Math.max(dy, dz)) return Orientation.xMajor;
        if (dy >= Math.max(dx, dz)) return Orientation.yMajor;
        if (dz >= Math.max(dx, dy)) return Orientation.zMajor;
        return Orientation.yMajor;
    }
}
