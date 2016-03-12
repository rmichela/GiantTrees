package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.Leaf;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.TraversalException;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeTraversal;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftLeafExporter implements TreeTraversal {

  Tree                 tree;
  private final Draw3d d3d;

  public MinecraftLeafExporter(final Draw3d d3d) {
    this.d3d = d3d;
  }

  @Override
  public boolean enterStem(final Stem stem) throws TraversalException {
    return true;
  }

  @Override
  public boolean enterTree(final Tree tree) throws TraversalException {
    this.tree = tree;
    return true;
  }

  @Override
  public boolean leaveStem(final Stem stem) throws TraversalException {
    return true;
  }

  @Override
  public boolean leaveTree(final Tree tree) throws TraversalException {
    return true;
  }

  @Override
  public boolean visitLeaf(final Leaf leaf) throws TraversalException {
    this.d3d.drawLeafCluster(this.d3d.toMcVector(leaf.transf.vector()),
                             leaf.par.LeafScale / 2,
                             (leaf.par.LeafScaleX * leaf.par.LeafScale) / 2);
    return true;
  }
}
