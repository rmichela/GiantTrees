package com.ryanmichela.trees.rendering;


import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.LinkedList;
import java.util.List;

/**
 * Copyright 2014 Ryan Michela
 */
public class Draw3d {
    private Location refPoint;
    private WorldChangeTracker changeTracker;
    private NoiseGenerator noise;
    private double noiseIntensity;
    private TreeType treeType;

    public Draw3d(Location refPoint, double noiseIntensity, TreeType treeType, WorldChangeTracker changeTracker) {
        this.refPoint = refPoint;
        this.noise = new SimplexNoiseGenerator(refPoint.hashCode());
        this.noiseIntensity = noiseIntensity;
        this.changeTracker = changeTracker;
        this.treeType = treeType;
    }

    public static Vector toMcVector(net.sourceforge.arbaro.transformation.Vector arbVec) {
        return new Vector(arbVec.getX(), arbVec.getZ(), arbVec.getY());
    }

    public void applyChanges() {
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
                    circle2d = plotCircle(centerPoint.getBlockY(), centerPoint.getBlockZ(), centerPoint.getBlockX(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                new Vector(
                                    centerPoint.getBlockX(),
                                    l1.getBlockY() - centerPoint.getBlockY() + p.p,
                                    l1.getBlockZ() - centerPoint.getBlockZ() + p.q),
                                treeType.woodMaterial,
                                LogData(treeType.dataOffset, orientation),
                                true);
                    }
                    break;
                case yMajor:
                    circle2d = plotCircle(centerPoint.getBlockX(), centerPoint.getBlockZ(),centerPoint.getBlockY(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                new Vector(
                                    l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                    centerPoint.getBlockY(),
                                    l1.getBlockZ() - centerPoint.getBlockZ() + p.q),
                                treeType.woodMaterial,
                                LogData(treeType.dataOffset, orientation),
                                true);
                    }
                    break;
                case zMajor:
                    circle2d = plotCircle(centerPoint.getBlockX(), centerPoint.getBlockY(), centerPoint.getBlockZ(), r);
                    for (Point2D p : circle2d) {
                        changeTracker.addChange(
                                new Vector(
                                    l1.getBlockX() - centerPoint.getBlockX() + p.p,
                                    l1.getBlockY() - centerPoint.getBlockY() + p.q,
                                    centerPoint.getBlockZ()),
                                treeType.woodMaterial,
                                LogData(treeType.dataOffset, orientation),
                                true);
                    }
                    break;
            }
        }
    }

    public void drawWoodSphere(Vector pos, double r, Orientation orientation) {
        for(Vector loc : plotSphere(pos, r)) {
            changeTracker.addChange(
                    loc,
                    treeType.woodMaterial,
                    LogData(treeType.dataOffset, orientation),
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

    private List<Vector> plotSphere(Vector pos, double r) {
        List<Vector> points = new LinkedList<Vector>();
        int rCeil = (int)Math.ceil(r) + 4;
        double r2 = r*r;
        for (int x = -rCeil; x <= rCeil; x++) {
            for (int y = -rCeil; y <= rCeil; y++) {
                for (int z = -rCeil; z <= rCeil; z++) {
                    double left = x*x + y*y + z*z;
                    double noiseOffset = (noise.noise((x + pos.getBlockX())*.25, (y + pos.getBlockY())*.25, (z + pos.getBlockZ())*.25) + 1) * noiseIntensity * 2;
                    if (left <= r2 + noiseOffset*noiseOffset) {
                        points.add(new Vector(x, y, z).add(pos));
                    }
                }
            }
        }
        return points;
    }

    private List<Vector> plotEllipsoid(Vector pos, double a, double b, double c) {
        List<Vector> points = new LinkedList<Vector>();

        int halfSize = (int)Math.ceil(Math.max(Math.max(a, b), c)) + 2;

        for (int x = -halfSize; x <= halfSize; x++) {
            for (int y = -halfSize; y <= halfSize; y++) {
                for (int z = -halfSize; z <= halfSize; z++) {
                    double left = ((x*x)/(a*a)) + ((y*y)/(b*b)) + ((z*z)/(c*c));
                    double noiseOffset = (noise.noise((x + pos.getBlockX())*.25, (y + pos.getBlockY())*.25, (z + pos.getBlockZ())*.25) + 1) * noiseIntensity;
                    if (left <= 1 + noiseOffset) {
                        points.add(new Vector(x, y, z).add(pos));
                    }
                }
            }
        }
        return points;
    }

    public void drawLeafCluster(Vector pos, double length, double width) {
        for(Vector loc: plotEllipsoid(pos, length, width, length)) {
            changeTracker.addChange(
                    loc,
                    treeType.leafMaterial,
                    (byte)(treeType.dataOffset + 4),
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

    private List<Point2D> plotCircle(int cx, int cy, int z, int r) {
        List<Point2D> points2d = new LinkedList<Point2D>();

        int rSquare = r*r;
        for (int x = -r-8; x <= r+8; x++) {
            for (int y = -r-8; y <= r+8; y++) {
                double noiseOffset = (noise.noise((x + cx)*.25, (y + cy)*.25, (z)*.25) + 1) * noiseIntensity * 4;
                if ((x == 0 && y == 0) || (x*x + y*y <= rSquare + noiseOffset*noiseOffset)) {
                    points2d.add(new Point2D(cx + x, cy + y));
                }
            }
        }

        return points2d;
    }
}
