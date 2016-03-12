package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.export.ExportError;
import net.sourceforge.arbaro.export.Exporter;
import net.sourceforge.arbaro.tree.TraversalException;
import net.sourceforge.arbaro.tree.Tree;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftExporter extends Exporter {

  private final Draw3d d3d;

  public MinecraftExporter(final Tree tree, final Draw3d draw3d) {
    super(tree, null, null);
    this.d3d = draw3d;
  }

  @Override
  public void write() throws ExportError {
    try {
      // stems
      final MinecraftStemExporter exporter = new MinecraftStemExporter(
                                                                       this.d3d,
                                                                       this.tree.params.stopLevel);
      this.tree.traverseTree(exporter);

      // leaves
      if (this.tree.params.Leaves > 0) {
        final MinecraftLeafExporter lexporter = new MinecraftLeafExporter(
                                                                          this.d3d);
        this.tree.traverseTree(lexporter);
      }
    } catch (final TraversalException e) {
      e.printStackTrace();
    }
  }
}
