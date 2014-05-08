package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.DefaultStemTraversal;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Subsegment;
import net.sourceforge.arbaro.tree.TraversalException;
import org.bukkit.util.Vector;

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
        int level = s.lpar.level;

        for (int i=0; i<s.subsegments.size()-1; i++) {
            Subsegment ss1 = (Subsegment)s.subsegments.elementAt(i);
            Subsegment ss2 = (Subsegment)s.subsegments.elementAt(i+1);

            Vector l1 = Draw3d.toMcVector(ss1.pos);
            Vector l2 = Draw3d.toMcVector(ss2.pos);
            Orientation orientation = Orientation.orient(l1, l2);

            d3d.drawCone(l1, ss1.rad, l2, ss2.rad, level);

            if (l1.subtract(l2).length() > 1) {
                d3d.drawWoodSphere(l2, ss2.rad, orientation, level);
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
