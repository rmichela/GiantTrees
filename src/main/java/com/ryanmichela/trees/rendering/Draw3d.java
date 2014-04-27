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
    private Location refPoint;
    private WorldChangeTracker changeTracker = new WorldChangeTracker();

    public Draw3d(Location refPoint) {
        this.refPoint = refPoint;
    }

    public Location toLoc(Vector vec) {
        return new Location(refPoint.getWorld(), vec.getX(), vec.getZ(), vec.getY());
    }

    public void applyChanges() {
        changeTracker.applyChanges(refPoint.getWorld());
    }

    public void drawCone(Location l1, double rad1, Location l2, double rad2) {
        l1.add(refPoint);
        l2.add(refPoint);

        Orientation orientation = Orientation.orient(l1, l2);
        List<Location> centerLine = plotLine3d(l1, l2, orientation);

        // Circle stuff
        double h = l1.distance(l2);
        double ht = (-rad1 * h) / (rad2 - rad1);
        for (int i = 0; i < centerLine.size(); i++) {
            Location centerPoint = centerLine.get(i);
            int r = (int)Math.round(rad1 * (ht - i) / ht);

            List<Point2D> circle2d;
            switch(orientation) {
                case xMajor:
                    circle2d = plotCircle(centerPoint.getBlockY(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                centerPoint.getBlockX(),
                                l1.getBlockY() - centerPoint.getBlockY() + p.p,
                                l1.getBlockZ() - centerPoint.getBlockZ() + p.q,
                                Material.LOG,
                                LogData((byte)0, orientation),
                                true);
                    }
                    break;
                case yMajor:
                    circle2d = plotCircle(centerPoint.getBlockX(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                centerPoint.getBlockY(),
                                l1.getBlockZ() - centerPoint.getBlockZ() + p.q,
                                Material.LOG,
                                LogData((byte)0, orientation),
                                true);
                    }
                    break;
                case zMajor:
                    circle2d = plotCircle(centerPoint.getBlockX(), centerPoint.getBlockY(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                l1.getBlockY() - centerPoint.getBlockY() + p.q,
                                centerPoint.getBlockZ(),
                                Material.LOG,
                                LogData((byte)0, orientation),
                                true);
                    }
                    break;
            }
        }
    }

    public void drawWoodSphere(Location pos, double r, Orientation orientation) {
        for(Location loc : plotSphere(pos, r)) {
            changeTracker.addChange(
                    loc.getBlockX(),
                    loc.getBlockY(),
                    loc.getBlockZ(),
                    Material.LOG,
                    LogData((byte)0, orientation),
                    true);
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

    private List<Location> plotSphere(Location pos, double r) {
        List<Location> locations = new LinkedList<Location>();
        int x = pos.getBlockX();
        int y = pos.getBlockY();
        int z = pos.getBlockZ();
        int rCeil = (int)Math.ceil(r);
        double r2 = r*r;
        for (int xx = -rCeil; xx <= rCeil; xx++) {
            for (int yy = -rCeil; yy <= rCeil; yy++) {
                for (int zz = -rCeil; zz <= rCeil; zz++) {
                    double dist2 = xx*xx + yy*yy + zz*zz;
                    if (dist2 <= r2) {
                        locations.add(new Location(pos.getWorld(), x+xx, y+yy, z+zz));
                    }
                }
            }
        }
        return locations;
    }

    private List<Location> plotEllipsoid(Location pos, double a, double b, double c) {
        List<Location> locations = new LinkedList<Location>();
        int x = pos.getBlockX();
        int y = pos.getBlockY();
        int z = pos.getBlockZ();

        int halfSize = (int)Math.ceil(Math.max(Math.max(a, b), c));

        for (int xx = -halfSize; xx <= halfSize; xx++) {
            for (int yy = -halfSize; yy <= halfSize; yy++) {
                for (int zz = -halfSize; zz <= halfSize; zz++) {
                    double left = ((xx*xx)/(a*a)) + ((yy*yy)/(b*b)) + ((zz*zz)/(c*c));
                    if (left <= 1) {
                        locations.add(new Location(pos.getWorld(), x+xx, y+yy, z+zz));
                    }
                }
            }
        }
        return locations;
    }

    public void drawLeafCluster(Location pos, double length, double width) {
        pos.add(refPoint);
        for(Location loc: plotEllipsoid(pos, length, width, width)) {
            changeTracker.addChange(
                    loc.getBlockX(),
                    loc.getBlockY(),
                    loc.getBlockZ(),
                    Material.LEAVES,
                    (byte)4,
                    true);
        }
    }

    private List<Location> plotLine3d(Location l1, Location l2, Orientation orientation)
    {
        List<Location> locations = new LinkedList<Location>();
        if (orientation == Orientation.xMajor)            /* x dominant */
        {
            List<Point2D> xy = plotLine2d(l1.getBlockX(), l1.getBlockY(), l2.getBlockX(), l2.getBlockZ());
            List<Point2D> xz = plotLine2d(l1.getBlockX(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockZ());
            for(int i = 0; i < Math.min(xy.size(), xz.size()); i++) {
                locations.add(new Location(refPoint.getWorld(), l1.getBlockX() + i, xy.get(i).q, xz.get(i).q));
            }
        }
        else if (orientation == Orientation.yMajor)            /* y dominant */
        {
            List<Point2D> yx = plotLine2d(l1.getBlockY(), l1.getBlockX(), l2.getBlockY(), l2.getBlockX());
            List<Point2D> yz = plotLine2d(l1.getBlockY(), l1.getBlockZ(), l2.getBlockY(), l2.getBlockZ());
            for(int i = 0; i < Math.min(yx.size(), yz.size()); i++) {
                locations.add(new Location(refPoint.getWorld(), yx.get(i).q, l1.getBlockY() + i, yz.get(i).q));
            }
        }
        else if (orientation == Orientation.zMajor)            /* z dominant */
        {
            List<Point2D> zx = plotLine2d(l1.getBlockZ(), l1.getBlockX(), l2.getBlockZ(), l2.getBlockX());
            List<Point2D> zy = plotLine2d(l1.getBlockZ(), l1.getBlockY(), l2.getBlockZ(), l2.getBlockY());
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

    private List<Point2D> plotLine2d(int x1, int y1, int x2, int y2) {
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

    private List<Point2D> plotCircle(int cx, int cy, int r) {
        List<Point2D> points2d = new LinkedList<Point2D>();

        int rSquare = r*r;
        for (int xx = -r; xx <= r; xx++) {
            for (int yy = -r; yy <= r; yy++) {
                if ((xx == 0 && yy == 0) || (xx*xx + yy*yy <= rSquare)) {
                    points2d.add(new Point2D(cx + xx, cy + yy));
                }
            }
        }

        return points2d;
    }
}
