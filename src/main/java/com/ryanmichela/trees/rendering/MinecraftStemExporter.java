package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.*;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftStemExporter implements TreeTraversal {
    private Draw3d d3d;

    private Tree tree;
    private int level;

    public MinecraftStemExporter(Draw3d d3d, int level) {
        this.d3d = d3d;
        this.level = level;
    }

    @Override
    public boolean enterTree(Tree tree) throws TraversalException {
        this.tree = tree;
        return true;
    }

    @Override
    public boolean leaveTree(Tree tree) throws TraversalException {
        return true;
    }

    @Override
    public boolean enterStem(Stem stem) throws TraversalException {
        if (level >= 0 && stem.stemlevel < level) {
            return true; // look further for stems

        } else if (level >= 0 && stem.stemlevel > level) {
            return false; // go back to higher level

        } else {

            MinecraftSegmentExporter exporter = new MinecraftSegmentExporter(d3d);
            stem.traverseStem(exporter);

            return true;
        }
    }

    @Override
    public boolean leaveStem(Stem stem) throws TraversalException {
        return false;
    }

    @Override
    public boolean visitLeaf(Leaf leaf) throws TraversalException {
        // don't render leaves here
        return false;
    }
}
