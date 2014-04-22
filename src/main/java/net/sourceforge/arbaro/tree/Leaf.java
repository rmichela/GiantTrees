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

import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.transformation.Transformation;
import net.sourceforge.arbaro.transformation.Vector;

/**
 * A class for the leaves of the tree
 * 
 * @author Wolfram Diestel
 */
public class Leaf {
	
	public Transformation transf;
	public Params par;
	
//	double offset;    // how far from the parent's base
//	double length;    // the length of the leaf (without leaf stem)
//	double width;     // the width of the leaf
	
	public Leaf(Params params, Transformation trf/*, double offs*/) { /* offs = 0 */
		par = params;
		transf = trf;
//		offset = offs;
		
//		setLeafDimension();
		
		// FIXME: should stem radius be added?
		// print self.parent.stem_radius(self.offset)
		// self.direction.radius = self.parent.stem_radius(self.offset)+self.length/2
		
		// setLeafOrientation();
	}
	
//	/**
//	 *	Sets the length and width of a leaf
//	 */
//	private void setLeafDimension() {
//		// FIXME: this is made in tree at the moment
//		// it would be necessary only when leafs are
//		// different in length and width
//		length = par.LeafScale/Math.sqrt(par.LeafQuality);
//		width = par.LeafScale*par.LeafScaleX/Math.sqrt(par.LeafQuality);
//	}
	
	/**
	 *	Leaf rotation toward light
	 */
	private void setLeafOrientation() {
		if (par.LeafBend==0) return;
		
		
		// FIXME: make this function as fast as possible - a tree has a lot of leafs
		
		// rotation outside 
		Vector pos = transf.getT();
		// the z-vector of transf is parallel to the
		// axis of the leaf, the y-vector is the normal
		// (of the upper side) of the leaf
		Vector norm = transf.getY();
		
		double tpos = Vector.atan2(pos.getY(),pos.getX());
		double tbend = tpos - Vector.atan2(norm.getY(),norm.getX());
		// if (tbend>180) tbend = 360-tbend;
		
		double bend_angle = par.LeafBend*tbend;
		// transf = transf.rotz(bend_angle);
		// rotate about global z-axis
		transf = transf.rotaxis(bend_angle,Vector.Z_AXIS);
		
		// rotation up
		norm = transf.getY();
		double fbend = Vector.atan2(Math.sqrt(norm.getX()*norm.getX() + norm.getY()*norm.getY()),
				norm.getZ());
		
		bend_angle = par.LeafBend*fbend;
		
		transf = transf.rotx(bend_angle); 

//		this is from the paper, but is equivalent with
//      local x-rotation (upper code line)
//		
//		double orientation = Vector.atan2(norm.getY(),norm.getX());
//		transf = transf
//			.rotaxis(-orientation,Vector.Z_AXIS)
//			.rotx(bend_angle)
//			.rotaxis(orientation,Vector.Z_AXIS);
	}
	
		
	/**
	 * Makes the leave. Does nothing at the moment, because
	 * all the values can be calculated in the constructor 
	 */
	public void make() {
		setLeafOrientation();
	}
	
	public boolean traverseTree(TreeTraversal traversal) throws TraversalException{
	    return traversal.visitLeaf(this);
	}
};












