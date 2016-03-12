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

/*
class TraversalException extends ArbaroError{
	public TraversalException(String errmsg) {
		super(errmsg);
	} 
};
*/

/**
 * An interface, for traversal of the stems and 
 * leaves of a tree. (Compare Hierarchical Visitor Pattern)
 * 
 * @author Wolfram Diestel
 */

public interface TreeTraversal {
	   boolean enterTree(Tree tree) throws TraversalException; // going into a Tree
       boolean leaveTree(Tree tree) throws TraversalException; // coming out of a Tree
	   boolean enterStem(Stem stem) throws TraversalException; // going into a Stem
       boolean leaveStem(Stem stem) throws TraversalException; // coming out of a Stem
	/*   boolean enterSegment(Segment segment) throws TraversalException; // going into a Stem
       boolean leaveSegment(Segment segment) throws TraversalException; // coming out of a Stem
       boolean visitSubsegment(Subsegment subsegment) throws TraversalException; // process a Subsegment
       */
       boolean visitLeaf(Leaf leaf) throws TraversalException; // process a Leaf
}
