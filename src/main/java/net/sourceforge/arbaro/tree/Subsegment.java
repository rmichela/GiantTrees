//#**************************************************************************
//#
//#    Copyright (C) 2003-2006  Wolfram Diestel
//#
//#    This program is free software; you can redistribute it and/or modify
//#    it under the terms of the GNU General Public License as published by
//#    the Free Software Foundation; either version 2 of the License, or
//#    (at your option) any later version.
//#
//#    This program is distributed in the hope that it will be useful,
//#    but WITHOUT ANY WARRANTY; without even the implied warranty of
//#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//#    GNU General Public License for more details.
//#
//#    You should have received a copy of the GNU General Public License
//#    along with this program; if not, write to the Free Software
//#    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//#
//#    Send comments and bug fixes to diestel@steloj.de
//#
//#**************************************************************************/


package net.sourceforge.arbaro.tree;

import net.sourceforge.arbaro.transformation.*;

/**
 * Segments with helical curving or nonlinearly changing radius
 * can be broken into subsegments. Normal segments consist of only
 * one subsegment.
 * 
 * @author Wolfram Diestel
 */
public class Subsegment { 
	// a Segment can have one or more Subsegments
	public Vector pos; 
	public double height; // height relative to segment's base 
	public double rad; 
	
	public Subsegment(Vector p, double r, double h) {
		pos = p;
		rad = r;
		height = h;
	}
	
	public boolean traverseStem(StemTraversal traversal) throws TraversalException {
	    return traversal.visitSubsegment(this);
	}
}
