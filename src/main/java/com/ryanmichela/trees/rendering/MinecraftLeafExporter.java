package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.*;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftLeafExporter implements TreeTraversal {
    private Draw3d d3d;
    Tree tree;

    public MinecraftLeafExporter(Draw3d d3d) {
        this.d3d = d3d;
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
        return true;
    }

    @Override
    public boolean leaveStem(Stem stem) throws TraversalException {
        return true;
    }

    @Override
    public boolean visitLeaf(Leaf leaf) throws TraversalException {
        d3d.drawLeafCluster(Draw3d.toMcVector(leaf.transf.vector()),
                leaf.par.LeafScale/2, leaf.par.LeafScaleX*leaf.par.LeafScale/2);
        return true;
    }
}
