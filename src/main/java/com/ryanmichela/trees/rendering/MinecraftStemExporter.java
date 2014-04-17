package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.*;
import org.bukkit.Material;

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

            Material material = Material.LOG;
            if (stem.stemlevel == 0) material = Material.WOOL;
            if (stem.stemlevel == 1) material = Material.SANDSTONE;
            if (stem.stemlevel == 2) material = Material.BRICK;

            MinecraftSegmentExporter exporter = new MinecraftSegmentExporter(d3d, material);
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
