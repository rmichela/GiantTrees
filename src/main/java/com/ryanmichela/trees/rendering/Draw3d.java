/*
 * Copyright (C) 2014 Ryan Michela
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ryanmichela.trees.rendering;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class Draw3d {

  public class Point2D {

    public int p;
    public int q;

    public Point2D(final int p, final int q) {
      this.p = p;
      this.q = q;
    }
  }

  public enum RenderOrientation {
    INVERTED, NORMAL
  }

  private final WorldChangeTracker changeTracker;
  private final NoiseGenerator     noise;
  private final double             noiseIntensity;
  private final Location           refPoint;

  private final RenderOrientation  renderOrientation;

  private final TreeType           treeType;

  public Draw3d(final Location refPoint, final double noiseIntensity,
                final TreeType treeType,
                final WorldChangeTracker changeTracker,
                final RenderOrientation renderOrientation) {
    this.refPoint = refPoint;
    this.noise = new SimplexNoiseGenerator(refPoint.hashCode());
    this.noiseIntensity = noiseIntensity;
    this.changeTracker = changeTracker;
    this.treeType = treeType;
    this.renderOrientation = renderOrientation;
  }

  public void applyChanges() {
    changeTracker.applyChanges(refPoint);
  }

  public void drawCone(final Vector l1, final double rad1, final Vector l2,
                       final double rad2, final int level) {
    final Orientation orientation = Orientation.orient(l1, l2);
    final List<Vector> centerLine = plotLine3d(l1, l2, orientation);

    // Circle stuff
    final double h = l1.distance(l2);
    final double ht = (-rad1 * h) / (rad2 - rad1);
    for (int i = 0; i < centerLine.size(); i++) {
      final Vector centerPoint = centerLine.get(i);
      final int r = (int) Math.round((rad1 * (ht - i)) / ht);

      List<Point2D> circle2d;
      switch (orientation) {
        case xMajor:
          circle2d = plotCircle(centerPoint.getBlockY(),
                                     centerPoint.getBlockZ(),
                                     centerPoint.getBlockX(), r, level);
          for (final Point2D p : circle2d) {
            changeTracker.addChange(new Vector(
                                      centerPoint.getBlockX(),
                                      (l1.getBlockY() - centerPoint.getBlockY())
                                          + p.p,
                                      (l1.getBlockZ() - centerPoint.getBlockZ())
                                          + p.q),
                           treeType.woodMaterial,
                    Orient(orientation), true);
          }
          break;
        case yMajor:
          circle2d = plotCircle(centerPoint.getBlockX(),
                                     centerPoint.getBlockZ(),
                                     centerPoint.getBlockY(), r, level);
          for (final Point2D p : circle2d) {
            changeTracker.addChange(new Vector(
                                                    (l1.getBlockX() - centerPoint.getBlockX())
                                                        + p.p,
                                                    centerPoint.getBlockY(),
                                                    (l1.getBlockZ() - centerPoint.getBlockZ())
                                                        + p.q),
                                         treeType.woodMaterial,
                    Orient(orientation), true);
          }
          break;
        case zMajor:
          circle2d = plotCircle(centerPoint.getBlockX(),
                                     centerPoint.getBlockY(),
                                     centerPoint.getBlockZ(), r, level);
          for (final Point2D p : circle2d) {
            changeTracker.addChange(new Vector(
                                                    (l1.getBlockX() - centerPoint.getBlockX())
                                                        + p.p,
                                                    (l1.getBlockY() - centerPoint.getBlockY())
                                                        + p.q,
                                                    centerPoint.getBlockZ()),
                                         treeType.woodMaterial,
                    Orient(orientation), true);
          }
          break;
      }
    }
  }

  public void drawLeafCluster(final Vector pos, final double length,
                              final double width) {
    for (final Vector loc : plotEllipsoid(pos, length, width, length, 0)) {
      changeTracker.addChange(loc, treeType.leafMaterial,
                                   Persist(), false);
    }
  }

  public void drawRootJunction(final Vector pos, final double r) {
    for (final Vector loc : plotDownwardHemisphere(pos, r)) {
      changeTracker.addChange(loc, treeType.woodMaterial,
              Orient(Orientation.yMajor), true);
    }
  }

  public void drawWoodSphere(final Vector pos, final double r,
                             final Orientation orientation, final int level) {
    for (final Vector loc : plotSphere(pos, r, level)) {
      changeTracker.addChange(loc, treeType.woodMaterial,
              Orient(orientation), true);
    }
  }

  public Vector
      toMcVector(final net.sourceforge.arbaro.transformation.Vector arbVec) {
    final boolean invertY = renderOrientation == RenderOrientation.INVERTED;
    return new Vector(arbVec.getX(), invertY ? -arbVec.getZ() : arbVec.getZ(),
                      arbVec.getY());
  }

  private double calculateNoiseOffset(final int x, final int y, final int z,
                                      final int multiplier, final int level) {
    final double levelScale = ((double) (4 - level) / (double) 4);
    return (noise.noise(x * .25, y * .25, z * .25) + 1)
           * noiseIntensity * multiplier * levelScale;
  }

  private Consumer<BlockData> Orient(Orientation orientation) {
    return blockData -> {
      if (blockData instanceof Orientable) {
        Orientable orientable = (Orientable) blockData;
        switch (orientation) {
          case xMajor: orientable.setAxis(Axis.X);
          case yMajor: orientable.setAxis(Axis.Y);
          case zMajor: orientable.setAxis(Axis.Z);
        }
      }
    };
  }

  private Consumer<BlockData> Persist() {
    return blockData -> {
      if (blockData instanceof Leaves) {
        Leaves leaves = (Leaves) blockData;
        leaves.setPersistent(true);
      }
    };
  }

  private List<Point2D> plotCircle(final int cx, final int cy, final int z,
                                   final int r, final int level) {
    final List<Point2D> points2d = new LinkedList<>();

    final int rSquare = r * r;
    for (int x = -r - 8; x <= (r + 8); x++) {
      for (int y = -r - 8; y <= (r + 8); y++) {
        final double noiseOffset = calculateNoiseOffset((x + cx),
                                                             (y + cy), (z), 4,
                                                             level);
        if (((x == 0) && (y == 0))
            || (((x * x) + (y * y)) <= (rSquare + (noiseOffset * noiseOffset)))) {
          points2d.add(new Point2D(cx + x, cy + y));
        }
      }
    }

    return points2d;
  }

  private List<Vector> plotDownwardHemisphere(final Vector pos, final double r) {
    final List<Vector> points = new LinkedList<>();
    final int rCeil = (int) Math.ceil(r) + 4;
    final double r2 = r * r;
    for (int x = -rCeil; x <= rCeil; x++) {
      for (int y = -rCeil; y <= 0; y++) {
        for (int z = -rCeil; z <= rCeil; z++) {
          final double left = (x * x) + (y * y) + (z * z);
          final double noiseOffset = calculateNoiseOffset((x + pos.getBlockX()),
                                                               (y + pos.getBlockY()),
                                                               (z + pos.getBlockZ()),
                                                               2, 0);
          if (left <= (r2 + (noiseOffset * noiseOffset))) {
            points.add(new Vector(x, y, z).add(pos));
          }
        }
      }
    }
    return points;
  }

  private List<Vector> plotEllipsoid(final Vector pos, final double a,
                                     final double b, final double c,
                                     final int level) {
    final List<Vector> points = new LinkedList<>();

    final int halfSize = (int) Math.ceil(Math.max(Math.max(a, b), c)) + 2;

    for (int x = -halfSize; x <= halfSize; x++) {
      for (int y = -halfSize; y <= halfSize; y++) {
        for (int z = -halfSize; z <= halfSize; z++) {
          final double left = ((x * x) / (a * a)) + ((y * y) / (b * b))
                              + ((z * z) / (c * c));
          final double noiseOffset = calculateNoiseOffset((x + pos.getBlockX()),
                                                               (y + pos.getBlockY()),
                                                               (z + pos.getBlockZ()),
                                                               1, level);
          if (left <= (1 + noiseOffset)) {
            points.add(new Vector(x, y, z).add(pos));
          }
        }
      }
    }
    return points;
  }

  private List<Point2D> plotLine2d(final int x1, final int y1, final int x2,
                                   final int y2) {
    final List<Point2D> points2d = new LinkedList<>();
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
    if ((dx != 0) || (dy != 0)) { // are other points on the line ?
      if (dy <= dx) { // is the slope <= 1 ?
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

  private List<Vector> plotLine3d(final Vector l1, final Vector l2,
                                  final Orientation orientation) {
    final List<Vector> locations = new LinkedList<>();
    if (orientation == Orientation.xMajor) /* x dominant */
    {
      final List<Point2D> xy = plotLine2d(l1.getBlockX(), l1.getBlockY(),
                                               l2.getBlockX(), l2.getBlockZ());
      final List<Point2D> xz = plotLine2d(l1.getBlockX(), l1.getBlockZ(),
                                               l2.getBlockX(), l2.getBlockZ());
      for (int i = 0; i < Math.min(xy.size(), xz.size()); i++) {
        locations.add(new Vector(l1.getBlockX() + i, xy.get(i).q, xz.get(i).q));
      }
    } else if (orientation == Orientation.yMajor) /* y dominant */
    {
      final List<Point2D> yx = plotLine2d(l1.getBlockY(), l1.getBlockX(),
                                               l2.getBlockY(), l2.getBlockX());
      final List<Point2D> yz = plotLine2d(l1.getBlockY(), l1.getBlockZ(),
                                               l2.getBlockY(), l2.getBlockZ());
      for (int i = 0; i < Math.min(yx.size(), yz.size()); i++) {
        locations.add(new Vector(yx.get(i).q, l1.getBlockY() + i, yz.get(i).q));
      }
    } else if (orientation == Orientation.zMajor) /* z dominant */
    {
      final List<Point2D> zx = plotLine2d(l1.getBlockZ(), l1.getBlockX(),
                                               l2.getBlockZ(), l2.getBlockX());
      final List<Point2D> zy = plotLine2d(l1.getBlockZ(), l1.getBlockY(),
                                               l2.getBlockZ(), l2.getBlockY());
      for (int i = 0; i < Math.min(zx.size(), zy.size()); i++) {
        locations.add(new Vector(zx.get(i).q, zy.get(i).q, l1.getBlockZ() + i));
      }
    }

    return locations;
  }

  private List<Vector> plotSphere(final Vector pos, final double r,
                                  final int level) {
    final List<Vector> points = new LinkedList<>();
    final int rCeil = (int) Math.ceil(r) + 4;
    final double r2 = r * r;
    for (int x = -rCeil; x <= rCeil; x++) {
      for (int y = -rCeil; y <= rCeil; y++) {
        for (int z = -rCeil; z <= rCeil; z++) {
          final double left = (x * x) + (y * y) + (z * z);
          final double noiseOffset = calculateNoiseOffset((x + pos.getBlockX()),
                                                               (y + pos.getBlockY()),
                                                               (z + pos.getBlockZ()),
                                                               2, level);
          if (left <= (r2 + (noiseOffset * noiseOffset))) {
            points.add(new Vector(x, y, z).add(pos));
          }
        }
      }
    }
    return points;
  }
}
