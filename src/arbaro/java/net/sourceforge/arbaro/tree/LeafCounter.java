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

/**
 * Counts the number of all leaves of a tree using the TreeTraversal interface
 *
 */
public class LeafCounter extends DefaultTreeTraversal {
	long leafCount;
	
	public long getLeafCount() {
		return leafCount;
	}

	public boolean enterStem(Stem stem) {
		// add leaves of this stem
		leafCount += stem.getLeafCount(); 
		return true;
	}

	public boolean enterTree(Tree tree) {
		leafCount=0; // start counting leaves
		return true;
	}

	public boolean visitLeaf(Leaf leaf) {
		return false; // don't visit more leaves
		  // for efficency leaf count was got from the parent stem 
	}


}
