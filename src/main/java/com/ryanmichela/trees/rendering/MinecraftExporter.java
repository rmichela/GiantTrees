package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.export.ExportError;
import net.sourceforge.arbaro.export.Exporter;
import net.sourceforge.arbaro.tree.TraversalException;
import net.sourceforge.arbaro.tree.Tree;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftExporter extends Exporter {
    private Draw3d d3d;

    public MinecraftExporter(Tree tree, Draw3d draw3d) {
        super(tree, null, null);
        this.d3d = draw3d;
    }

    @Override
    public void write() throws ExportError {
        try {
            // stems
            MinecraftStemExporter exporter = new MinecraftStemExporter(d3d, tree.params.stopLevel);
            tree.traverseTree(exporter);

            // leaves
            if (tree.params.Leaves > 0) {
                MinecraftLeafExporter lexporter = new MinecraftLeafExporter(d3d);
                tree.traverseTree(lexporter);
            }
        } catch (TraversalException e) {
            e.printStackTrace();
        }
    }
}
