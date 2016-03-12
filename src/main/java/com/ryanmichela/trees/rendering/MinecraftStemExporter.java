package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.Leaf;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.TraversalException;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.tree.TreeTraversal;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftStemExporter implements TreeTraversal {

  private final Draw3d d3d;

  private final int    level;
  private Tree         tree;

  public MinecraftStemExporter(final Draw3d d3d, final int level) {
    this.d3d = d3d;
    this.level = level;
  }

  @Override
  public boolean enterStem(final Stem stem) throws TraversalException {
    if ((stem.stemlevel < this.level) || (this.level == -1)) {
      final MinecraftSegmentExporter exporter = new MinecraftSegmentExporter(
                                                                             this.d3d);
      stem.traverseStem(exporter);
    }
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
    // don't render leaves here
    return false;
  }
}
