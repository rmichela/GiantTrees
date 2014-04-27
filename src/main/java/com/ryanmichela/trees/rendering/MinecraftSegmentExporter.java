package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.DefaultStemTraversal;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Subsegment;
import net.sourceforge.arbaro.tree.TraversalException;
import org.bukkit.Location;

/**
 * Copyright 2014 Ryan Michela
 */
public class MinecraftSegmentExporter extends DefaultStemTraversal {
    private Draw3d d3d;

    public MinecraftSegmentExporter(Draw3d d3d) {
        super();
        this.d3d = d3d;
    }

    public boolean enterSegment(Segment s) throws TraversalException {
        // TODO instead of accessing subsegments this way
        // it would be nicer to use visitSubsegment, but
        // how to see when we visit the last but one subsegment?
        // may be need an index in Subsegment
        Orientation orientation = Orientation.yMajor;
        for (int i=0; i<s.subsegments.size()-1; i++) {
            Subsegment ss1 = (Subsegment)s.subsegments.elementAt(i);
            Subsegment ss2 = (Subsegment)s.subsegments.elementAt(i+1);

            Location l1 = d3d.toLoc(ss1.pos);
            Location l2 = d3d.toLoc(ss2.pos);
            orientation = Orientation.orient(l1, l2);

            d3d.drawCone(l1, ss1.rad, l2, ss2.rad);

            if (l1.subtract(l2).length() > 1) {
                d3d.drawWoodSphere(l2, ss2.rad, orientation);
            }
        }

        return true;
    }

    public boolean visitSubsegment(Subsegment subsegment)
            throws TraversalException {
        // do nothing with subsegments at the moment
        return false;
    }
}
