package com.ryanmichela.trees.rendering;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2014 Ryan Michela
 */
public class Draw3d {
    private WorldChangeTracker changeTracker = new WorldChangeTracker();

    public static Vector toMcVector(net.sourceforge.arbaro.transformation.Vector arbVec) {
        return new Vector(arbVec.getX(), arbVec.getZ(), arbVec.getY());
    }

    public void applyChanges(Location refPoint) {
        changeTracker.applyChanges(refPoint);
    }

    public void drawCone(Vector l1, double rad1, Vector l2, double rad2) {
        Orientation orientation = Orientation.orient(l1, l2);
        List<Vector> centerLine = plotLine3d(l1, l2, orientation);

        // Circle stuff
        double h = l1.distance(l2);
        double ht = (-rad1 * h) / (rad2 - rad1);
        for (int i = 0; i < centerLine.size(); i++) {
            Vector centerPoint = centerLine.get(i);
            int r = (int)Math.round(rad1 * (ht - i) / ht);

            List<Point2D> circle2d;
            switch(orientation) {
                case xMajor:
                    circle2d = plotCircle(centerPoint.getBlockY(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                new Vector(
                                    centerPoint.getBlockX(),
                                    l1.getBlockY() - centerPoint.getBlockY() + p.p,
                                    l1.getBlockZ() - centerPoint.getBlockZ() + p.q),
                                Material.LOG,
                                LogData((byte)0, orientation),
                                true);
                    }
                    break;
                case yMajor:
                    circle2d = plotCircle(centerPoint.getBlockX(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                new Vector(
                                    l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                    centerPoint.getBlockY(),
                                    l1.getBlockZ() - centerPoint.getBlockZ() + p.q),
                                Material.LOG,
                                LogData((byte) 0, orientation),
                                true);
                    }
                    break;
                case zMajor:
                    circle2d = plotCircle(centerPoint.getBlockX(), centerPoint.getBlockY(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                new Vector(
                                    l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                    l1.getBlockY() - centerPoint.getBlockY() + p.q,
                                    centerPoint.getBlockZ()),
                                Material.LOG,
                                LogData((byte) 0, orientation),
                                true);
                    }
                    break;
            }
        }
    }

    public void drawWoodSphere(Vector pos, double r, Orientation orientation) {
        for(Vector loc : plotSphere(r)) {
            loc.add(pos);
            changeTracker.addChange(
                    loc,
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

    private List<Vector> plotSphere(double r) {
        List<Vector> points = new LinkedList<Vector>();
        int rCeil = (int)Math.ceil(r);
        double r2 = r*r;
        for (int x = -rCeil; x <= rCeil; x++) {
            for (int y = -rCeil; y <= rCeil; y++) {
                for (int z = -rCeil; z <= rCeil; z++) {
                    double dist2 = x*x + y*y + z*z;
                    if (dist2 <= r2) {
                        points.add(new Vector(x, y, z));
                    }
                }
            }
        }
        return points;
    }

    private List<Vector> plotEllipsoid(double a, double b, double c) {
        List<Vector> points = new LinkedList<Vector>();

        int halfSize = (int)Math.ceil(Math.max(Math.max(a, b), c));

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    double left = ((x*x)/(a*a)) + ((y*y)/(b*b)) + ((z*z)/(c*c));
                    if (left <= 1) {
                        points.add(new Vector(x, y, z));
                    }
                }
            }
        }
        return points;
    }

    public void drawLeafCluster(Vector pos, double length, double width) {
        for(Vector loc: plotEllipsoid(length, width, length)) {
            loc.add(pos);
            changeTracker.addChange(
                    loc,
                    Material.LEAVES,
                    (byte)4,
                    true);
        }
    }

    private List<Vector> plotLine3d(Vector l1, Vector l2, Orientation orientation)
    {
        List<Vector> locations = new LinkedList<Vector>();
        if (orientation == Orientation.xMajor)            /* x dominant */
        {
            List<Point2D> xy = plotLine2d(l1.getBlockX(), l1.getBlockY(), l2.getBlockX(), l2.getBlockZ());
            List<Point2D> xz = plotLine2d(l1.getBlockX(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockZ());
            for(int i = 0; i < Math.min(xy.size(), xz.size()); i++) {
                locations.add(new Vector(l1.getBlockX() + i, xy.get(i).q, xz.get(i).q));
            }
        }
        else if (orientation == Orientation.yMajor)            /* y dominant */
        {
            List<Point2D> yx = plotLine2d(l1.getBlockY(), l1.getBlockX(), l2.getBlockY(), l2.getBlockX());
            List<Point2D> yz = plotLine2d(l1.getBlockY(), l1.getBlockZ(), l2.getBlockY(), l2.getBlockZ());
            for(int i = 0; i < Math.min(yx.size(), yz.size()); i++) {
                locations.add(new Vector(yx.get(i).q, l1.getBlockY() + i, yz.get(i).q));
            }
        }
        else if (orientation == Orientation.zMajor)            /* z dominant */
        {
            List<Point2D> zx = plotLine2d(l1.getBlockZ(), l1.getBlockX(), l2.getBlockZ(), l2.getBlockX());
            List<Point2D> zy = plotLine2d(l1.getBlockZ(), l1.getBlockY(), l2.getBlockZ(), l2.getBlockY());
            for(int i = 0; i < Math.min(zx.size(), zy.size()); i++) {
                locations.add(new Vector(zx.get(i).q, zy.get(i).q, l1.getBlockZ() + i));
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
