package com.ryanmichela.trees.rendering;

import net.sourceforge.arbaro.tree.DefaultStemTraversal;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Subsegment;
import net.sourceforge.arbaro.tree.TraversalException;
import org.bukkit.Material;

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
        for (int i=0; i<s.subsegments.size()-1; i++) {
            Subsegment ss1 = (Subsegment)s.subsegments.elementAt(i);
            Subsegment ss2 = (Subsegment)s.subsegments.elementAt(i+1);
            d3d.drawCone(d3d.toLoc(ss1.pos), ss1.rad, d3d.toLoc(ss2.pos), ss2.rad);
            // for helix subsegs put spheres between
            if (s.lpar.nCurveV<0 && i<s.subsegments.size()-2) {
                d3d.drawSphere(d3d.toLoc(ss1.pos), Math.round(ss1.rad - 0.0001));
            }
        }

        // put sphere at segment end
        if ((s.rad2 > 0) && (! s.isLastStemSegment() ||
                (s.lpar.nTaper>1 && s.lpar.nTaper<=2)))
        {
            d3d.drawSphere(d3d.toLoc(s.posTo()), s.rad2 - 0.0001);
        }

        return true;
    }

    public boolean visitSubsegment(Subsegment subsegment)
            throws TraversalException {
        // do nothing with subsegments at the moment
        return false;
    }
}
