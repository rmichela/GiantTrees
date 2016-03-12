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
//  #**************************************************************************

package net.sourceforge.arbaro.mesh;

import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import net.sourceforge.arbaro.params.FloatFormat;
import net.sourceforge.arbaro.transformation.Vector;
import net.sourceforge.arbaro.tree.Segment;
//import net.sourceforge.arbaro.tree.Stem;

/**
 * A class holding a section of a mesh.
 * 
 * This is a number of vertices in one layer. 
 * Several layers build the mesh part for a stem.
 * 
 * @author wdiestel
 */
/**
 * @author wdiestel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MeshSection extends java.util.Vector {
	
	public MeshSection previous;
	public MeshSection next;
	public double mapV; // v-coordinate of uv-map
	public Segment segment;
	
	private class UVVertexEnumerator implements Enumeration {
		private int i;
		
		public UVVertexEnumerator() {
			i=0;
		}
		
		public boolean hasMoreElements() {
			return (i==0 && size()==1) || (i<=size() && size()>1);
		}
		
		public Object nextElement() {
			if (i<size())
				return ((Vertex)elementAt(i++)).uv;
			else if (i==size() && size()>1) {
				i++;
				return new UVVector(1.0,mapV);
			} else 
				throw new NoSuchElementException();
		}
	}
	
	private class FaceEnumerator implements Enumeration {
		private int i;
		private int ni;
		private int cnt_i;
		private int cnt_ni;
		private int inx;
		private int ninx;
		private boolean quads;
		private boolean uv;
		private Face face;

		public FaceEnumerator(int startIndex, boolean UVFaces, boolean useQuads) {
			if (next==null) return;
			
			i=0;
			ni=0;
			inx=startIndex;
			if (UVFaces) 
				ninx = inx + (size()==1? 1 : size()+1);
			else
				ninx=inx+size();
			quads = useQuads;
			uv = UVFaces;
			
			if (uv) {
				cnt_i = size()+1;
				cnt_ni = next.size()+1;
			} else {
				cnt_i = size();
				cnt_ni = next.size();
			}

			if (size() == 1 && next.size() == 1) {
				// normaly this shouldn't occur, only for very small radius?
				// I should warn about this
				System.err.println("WARNING: two adjacent mesh sections with only one point.");
			}
			
//			System.err.println("new section enum");
		}
		
		public boolean hasMoreElements() {
			return ! (next==null || 
					(size()==1 &&  ni >= next.size()) ||
					(next.size()==1 && i >= size()) ||
					(ni >= next.size() && i >= size()));
		}
		
		public Object nextElement() {
//			System.err.println("section enum "+i+"/"+cnt_i+"--"+ni+"/"+cnt_ni);
			
			if (! hasMoreElements())
				throw new NoSuchElementException();
			
			if (quads && size()>1 && next.size()==size()) {
				face = new Face(inx+i,
						ninx+ni,
						ninx+(++ni)%cnt_ni,
						inx+(++i)%cnt_i);
			} else {
				if (i<=ni || next.size()==1) {
					face = new Face(inx+i,
							ninx+ni,
							inx+(++i)%cnt_i);
				} else {
					face = new Face(
							inx+i%cnt_i,
							ninx+ni,
							ninx+(++ni)%cnt_ni);
				}
			}
			
			return face;
		}
	}

	
	public MeshSection(/*Stem st,*/ int ptcnt, double v, Segment seg) {
		super(ptcnt);
		mapV = v;
		segment = seg; 
		/*stem = st;*/
	}
	
	/**
	 * Adds a point to the mesh section
	 * 
	 * @param pt
	 */
	public void addPoint(Vector pt, double mapU) {
		addElement(new Vertex(pt,null,new UVVector(mapU,mapV)));
	} 
	
	/**
	 * Returns the location point of the vertex i.
	 * 
	 * @param i
	 * @return
	 */
	public Vector pointAt(int i) {
		return ((Vertex)elementAt(i)).point;
	}
	
	public Enumeration allVertices(boolean UVVertices) {
		if (UVVertices)
			return new UVVertexEnumerator();
		else
			return elements();
	}
	
	public Enumeration allFaces(int startIndex, boolean UVFaces, boolean useQuads) {
		return new FaceEnumerator(startIndex, UVFaces, useQuads);
	}
	
	/**
	 * Returns the texture's uv-coordinates of the point.
	 * 
	 * @param i
	 * @return
	 */
	public UVVector uvAt(int i) {
		if (i<size())
			return ((Vertex)elementAt(i)).uv;
		else
			return new UVVector(1.0,mapV);
	}
	
	public boolean isFirst() {
		return (previous==null);
	}
	
	public boolean isLast() {
		return (next==null);
	}
	
	/**
	 * Returns the number of faces between this section and the next one.
	 * 
	 * @return
	 */
	public int faceCount(boolean useQuads) {
		if (size() == 1) return next.size();
		else if (next.size()==1 || useQuads) return size();
		else return size()*2; // two triangles per vertice (quad)
	}
	
	
	/**
	 * Returns the normal of the vertex i.
	 * 
	 * @param i
	 * @return
	 * @throws Exception
	 */
	public Vector normalAt(int i) throws Exception {
		Vertex v = (Vertex)elementAt(i);
		if (v.normal == null) throw new MeshError("Error: Normal not set for point "
				+vectorStr(v.point));
		return v.normal;
	}
	
	private String vectorStr(Vector v) {
		NumberFormat fmt = FloatFormat.getInstance();
		return "<"+fmt.format(v.getX())+","
		+fmt.format(v.getZ())+","
		+fmt.format(v.getY())+">";
	}

	
	/**
	 * Returns the point number i.
	 * 
	 * @param i
	 * @return
	 */
	public Vector here(int i) {
		
		return ((Vertex)elementAt(i)).point;
	}
	
	/**
	 * Returns the point to the left of the point number i
	 * 
	 * @param i
	 * @return
	 */
	public Vector left(int i) {
		return ((Vertex)elementAt((i-1+size())%size())).point;
	}
	
	/**
	 * Returns the point to the right of the point number i
	 * 
	 * @param i
	 * @return
	 */
	public Vector right(int i) {
		return ((Vertex)elementAt((i+1)%size())).point;
	}
	
	/**
	 * Returns the point on top of the point number i (from the next section).
	 * The next section has the same number of points or only one point.
	 * @param i
	 * @return
	 */
	public Vector up(int i) {
		return ((Vertex)(next.elementAt(i%next.size()))).point;
	}
	
	/**
	 * Returns the point below the point number i (from the previous section).
	 * The next section has the same number of points or only one point.
	 * @param i
	 * @return
	 */
	public Vector down(int i) {
		return ((Vertex)(previous.elementAt(i%previous.size()))).point;
	}	  	  
	
	/**
	 * Returns the normal of the plane built by the vectors a-b and c-b
	 * 
	 * @param a  
	 * @param b 
	 * @param c
	 * @return 
	 */
	public Vector normal(Vector a, Vector b, Vector c) {
		Vector u = (a.sub(b)).normalize();
		Vector v = (c.sub(b)).normalize();
		Vector norm = new Vector(u.getY()*v.getZ() - u.getZ()*v.getY(),
				u.getZ()*v.getX() - u.getX()*v.getZ(),
				u.getX()*v.getY() - u.getY()*v.getX()).normalize();
		if (Double.isNaN(norm.getX()) && Double.isNaN(norm.getY()) 
				&& Double.isNaN(norm.getZ())) {
			System.err.println("WARNING: invalid normal vector - stem radius too small?");
			norm = new Vector(0,0,1);
		}
		return norm;
	}
	
	/**
	 * Sets all normals to the vector vec
	 *
	 * @param vec
	 */
	public void setNormalsToVector(Vector vec) {
		for (int i=0; i<size(); i++) {
			((Vertex)elementAt(i)).normal=vec;
		}
	}
	
	/**
	 * Sets all normals to the average
	 * of the two left and right upper triangles
	 * 
	 */
	public void setNormalsUp() {
		for (int i=0; i<size(); i++) {
			((Vertex)elementAt(i)).normal = 
				(normal(up(i),here(i),left(i)).add(
						normal(right(i),here(i),up(i)))).normalize();
		}
	}
	
	/**
	 * Sets all normals to the average
	 * of the two left and right lower triangles
	 * 
	 */
	public void setNormalsDown() {
		for (int i=0; i<size(); i++) {
			((Vertex)elementAt(i)).normal = 
				(normal(down(i),here(i),right(i)).add(
						normal(left(i),here(i),down(i)))).normalize();
		}
	}
	
	/**
	 * Sets all normals to the average
	 * of the four left and right upper and lower triangles
	 */
	public void setNormalsUpDown() {
		for (int i=0; i<size(); i++) {
			((Vertex)elementAt(i)).normal = 
				(normal(up(i),here(i),left(i)).add(
						normal(right(i),here(i),up(i))).add(
								normal(down(i),here(i),right(i))).add(
										normal(left(i),here(i),down(i)))).normalize();
		}
	}
	
};








