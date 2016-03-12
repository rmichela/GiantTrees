/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
//  #**************************************************************************
//  #
//  #    Copyright (C) 2003-2006  Wolfram Diestel
//  #
//  #    This program is free software; you can redistribute it and/or modify
//  #    it under the terms of the GNU General Public License as published by
//  #    the Free Software Foundation; either version 2 of the License, or
//  #    (at your option) any later version.
//  #
//  #    This program is distributed in the hope that it will be useful,
//  #    but WITHOUT ANY WARRANTY; without even the implied warranty of
//  #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  #    GNU General Public License for more details.
//  #
//  #    You should have received a copy of the GNU General Public License
//  #    along with this program; if not, write to the Free Software
//  #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//  #
//  #    Send comments and bug fixes to diestel@steloj.de
//  #
//  #**************************************************************************/

package net.sourceforge.arbaro.tree;

import net.sourceforge.arbaro.mesh.MeshPart;
import net.sourceforge.arbaro.mesh.MeshSection;
import net.sourceforge.arbaro.transformation.*;
import net.sourceforge.arbaro.params.*;


/**
 * Creates a MeshPart for a Stem
 * 
 * @author wolfram
 *
 */
public class MeshPartCreator implements StemTraversal {
	MeshPart meshPart;
	boolean useQuads;
	Stem stem;
	Segment segment;
	boolean firstSubsegment;
	
	/**
	 * 
	 */
	public MeshPartCreator(boolean useQuads) {
		super();
		this.useQuads = useQuads;
	}

	public MeshPart getMeshPart() {
		return meshPart;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.StemTraversal#enterSegment(net.sourceforge.arbaro.tree.Segment)
	 */
	public boolean enterSegment(Segment segment) throws TraversalException {
		//segment.addToMeshpart(meshPart);

		this.segment = segment;
		firstSubsegment = true; // ignore first subsegment of each segment, but the first
		return true;

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.StemTraversal#enterStem(net.sourceforge.arbaro.tree.Stem)
	 */
	public boolean enterStem(Stem stem) throws TraversalException {
		this.stem = stem;
		int smooth_mesh_level = stem.tree.params.smooth_mesh_level; 
		meshPart = new MeshPart(stem, stem.stemlevel<=smooth_mesh_level, useQuads);

		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.StemTraversal#leaveSegment(net.sourceforge.arbaro.tree.Segment)
	 */
	public boolean leaveSegment(Segment segment) throws TraversalException {
		try {
			
			// System.err.println("MESHCREATION, segmindex: "+index);
	
			// close mesh with normal in z-direction
			if (segment.isLastStemSegment()) {
				
				if (segment.rad2>0.000001) {
					createSectionMeshpoints(segment.posTo(),0,false,
							1);
				}
				
				//DBG System.err.println("LAST StemSegm, setting normals to Z-dir");
				((MeshSection)meshPart.lastElement()).setNormalsToVector(segment.transf.getZ());
			}
			
			return true;
		} catch (Exception e) {
			throw new TraversalException("Mesh creation error at tree pos: " 
					+ stem.getTreePosition() + " segment "+segment.index+": "
					+ e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.StemTraversal#leaveStem(net.sourceforge.arbaro.tree.Stem)
	 */
	public boolean leaveStem(Stem stem) throws TraversalException {
		return (meshPart.size()>0); // only use meshparts with sections
	}
	
	private void createSectionMeshpoints(Vector pos, double rad, 
			boolean donttrf, double vMap) {
		//h = (self.index+where)*self.stem.segment_len
		//rad = self.stem.stem_radius(h)
		// self.stem.DBG("MESH: pos: %s, rad: %f\n"%(str(pos),rad))
		
		// System.err.println("Segment-create meshpts, pos: "+pos+" rad: "+rad);
		
		Params par = segment.par;
		LevelParams lpar = segment.lpar;
		
		Transformation trf = segment.transf.translate(pos.sub(segment.posFrom()));
		//self.stem.TRF("MESH:",trf)
		
		// if radius = 0 create only one point
		if (rad<0.000001) {
			MeshSection section = new MeshSection(1,vMap,segment);
			section.addPoint(trf.apply(new Vector(0,0,0)),0.5);
			meshPart.addSection(section);
		} else { //create pt_cnt points
			int pt_cnt = lpar.mesh_points;
			MeshSection section = new MeshSection(pt_cnt,vMap,segment);
			//stem.DBG("MESH+LOBES: lobes: %d, depth: %f\n"%(self.tree.Lobes, self.tree.LobeDepth))
			
			for (int i=0; i<pt_cnt; i++) {
				double angle = i*360.0/pt_cnt;
				// for Lobes ensure that points are near lobes extrema, but not exactly there
				// otherwise there are to sharp corners at the extrema
				if (lpar.level==0 && par.Lobes != 0) {
					angle -= 10.0/par.Lobes;
				}
				
				// create some point on the unit circle
				Vector pt = new Vector(Math.cos(angle*Math.PI/180),Math.sin(angle*Math.PI/180),0);
				// scale it to stem radius
				if (lpar.level==0 && (par.Lobes != 0 || par._0ScaleV !=0)) {
					// self.stem.DBG("MESH+LOBES: angle: %f, sinarg: %f, rad: %f\n"%(angle, \
					//self.tree.Lobes*angle*pi/180.0, \
					//	rad*(1.0+self.tree.LobeDepth*cos(self.tree.Lobes*angle*pi/180.0))))
					double rad1 = rad * (1 + 
							par.random.uniform(-par._0ScaleV,par._0ScaleV)/
							segment.subsegments.size());
					pt = pt.mul(rad1*(1.0+par.LobeDepth*Math.cos(par.Lobes*angle*Math.PI/180.0))); 
				} else {
					pt = pt.mul(rad); // faster - no radius calculations
				}
				// apply transformation to it
				// (for the first trunk segment transformation shouldn't be applied to
				// the lower meshpoints, otherwise there would be a gap between 
				// ground and trunk)
				// FIXME: for helical stems may be/may be not a random rotation 
				// should applied additionally?
				
				if (! donttrf) {  // not (stem.level==0 && index==0):
					pt = trf.apply(pt);
				} else {
					// FIXME: should apply z-rotation and translation only here
					pt = trf.apply(pt); //FIXME: trf.getT(); // tranlate only
				}
				section.addPoint(pt,angle/360.0);
			}
			
			//add section to the mesh part
			meshPart.addSection(section);
		}
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.arbaro.tree.StemTraversal#visitSubsegment(net.sourceforge.arbaro.tree.Subsegment)
	 */
	public boolean visitSubsegment(Subsegment ss)
			throws TraversalException {
		
		try {
			double vLength = stem.getLength()+stem.stemRadius(0)+stem.stemRadius(stem.length);
			//double vBase = + stem.stemRadius(0);
			
			// if first segment, create lower meshpoints
			if (meshPart.size() == 0) {
				// one point at the stem origin, with normal in reverse z-direction
				createSectionMeshpoints(ss.pos,0,
						segment.isFirstStemSegment() && segment.lpar.level==0,0);
				((MeshSection)meshPart.firstElement()).setNormalsToVector(segment.transf.getZ().mul(-1));
				
				// more points around the stem origin
				createSectionMeshpoints(ss.pos,ss.rad,
						segment.isFirstStemSegment() && segment.lpar.level==0,
						0 /* vBase/vLength */);

				firstSubsegment=false;

			} else {
				
				if (firstSubsegment) {
					// first subsgement, ignore it,
					// but process all following
					firstSubsegment=false;
				} else {
					// create meshpoints on top of each subsegment
					createSectionMeshpoints(ss.pos,ss.rad,false,
						(/*vBase+*/segment.index*segment.length+ss.height)/vLength);
				}
			}
			
			return true;
			
		} catch (Exception e) {
			throw new TraversalException("Mesh creation error at tree pos: " 
					+ stem.getTreePosition() + " subseg of segment "+segment.index+": "
					+ e.toString());
		}		
	}

}
