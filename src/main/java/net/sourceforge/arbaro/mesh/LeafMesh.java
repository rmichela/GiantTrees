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

package net.sourceforge.arbaro.mesh;

import net.sourceforge.arbaro.transformation.*;

import java.util.regex.*;


/**
 * Base class for all mesh based leaves
 * 
 * @author wdiestel
 *
 */
abstract class LeafShape {
	Vertex[] vertices;
	Face[] faces;
	
	double length=1;
	double width=1;
	double stemLen=0.5;
	boolean useQuads;
	
	LeafShape(double len, double wid, double stem_len, boolean quads) {
		length = len;
		width = wid;
		stemLen = stem_len;
		useQuads = quads;
	}
	
	void setPoint(int i, double x, double y, double z) {
		Vector point = new Vector(x*width,y*width,(stemLen+z)*length);
		UVVector uv = new UVVector(x+0.5,z);
		if (vertices[i] == null) {
			// FIXME: add uv-mapping
			vertices[i] = new Vertex(point,null,uv);
		} else {
			vertices[i].point = point;
		}
	}
	
	
	int getVertexCount() {
		return vertices.length;
	}
	
	int getFaceCount() {
		return faces.length;
	}
};


/**
 * An oval leaf shape having the outline of an averag leaf.
 * 
 * @author wdiestel
 */
class DiscShape extends LeafShape {
	
	public DiscShape(int triangleCount, double len, double wid, double stem_len, boolean quads) {
		super(len,wid,stem_len,quads);
		
		vertices = new Vertex[triangleCount+2];
		if (quads) {
			faces = new Face[triangleCount / 2 + triangleCount % 2];
		} else {
			faces = new Face[triangleCount];
		}
		
		setCirclePoints();
		setFaces();
	}
	
	void setCirclePoints() {
		double angle;
		double x;
		double z;
		int cnt = vertices.length;
		// set vertices along a circular curve
		for (int i=0; i<cnt; i++) {
			angle = i * 2.0 * Math.PI / cnt;
			x = Math.sin(angle);
			z = Math.cos(angle);
			
			// add a peak to the leaf
			if (angle < Math.PI) {
				x -= leaffunc(angle);
			} else if (angle > Math.PI) {
				x += leaffunc(2*Math.PI-angle);
			}
			setPoint(i, 0.5*x, 0, 0.5*z + 0.5);
		}
	}
	
	double leaffunc(double angle) {
		return leaffuncaux(angle)
		- angle*leaffuncaux(Math.PI)/Math.PI;
	}
	
	double leaffuncaux(double x) {
		return 0.8 * Math.log(x+1)/Math.log(1.2) - 1.0*Math.sin(x);
	}
	
	void setFaces() {
		int left = 0;
		int right = vertices.length-1;
		//boolean alternate = false;
		// add triangles with an edge on alternating sides
		// of the leaf
		for (int i=0; i<faces.length; i++) {
			if (useQuads) {
				if (left+1<right-1) {
					faces[i] = new Face(left,left+1,right-1,right);
					left++; right--;
				} else {
					// last face is a triangle for odd vertex numbers
					faces[i] = new Face(left,left+1,right);
					left++;
				}
				
			} else {
				if (i % 2 == 0) {
					faces[i] = new Face(left,left+1,right);
					left++;
				} else {
					faces[i] = new Face(left,right-1,right);
					right--;
				}
			}
		}
	}
};

/**
 * An oval leaf shape having the outline of an averag leaf.
 * 
 * @author wdiestel
 */
class SquareShape extends LeafShape {
	
	public SquareShape(double len, double wid, double stem_len, boolean quads) {
		super(len,wid,stem_len,quads);
		
		vertices = new Vertex[4];
		if (quads) {
			faces = new Face[1];
		} else {
			faces = new Face[2];
		}
		
		setPoint(0, -0.5, 0, 0);
		setPoint(1, -0.5, 0, 1);
		setPoint(2, 0.5, 0, 1);
		setPoint(3, 0.5, 0, 0);

		if (useQuads) {
			faces[0] = new Face(0,3,2,1);
		} else {
			faces[0] = new Face(0,2,1);
			faces[1] = new Face(0,3,2);
		}
	}
	
};


/**
 * A spherical leaf shape. It is aproximated by an ikosaeder.
 * 
 * @author wdiestel
 */
class SphereShape extends LeafShape {
	// use ikosaeder as a "sphere"
	
	public SphereShape(double len, double wid, double stem_len) {
		super(len,wid,stem_len,false);
		
		vertices = new Vertex[12];
		faces = new Face[20];
		
		double s = (Math.sqrt(5)-1)/2*Math.sqrt(2/(5-Math.sqrt(5))) / 2; 
		// half edge length so, that the vertices are at distance of 0.5 from the center
		double t = Math.sqrt(2/(5-Math.sqrt(5))) / 2;
		
		setPoint(0, 0,s,-t+0.5);	setPoint(6, 0,-s,-t+0.5);
		setPoint(1, t,0,-s+0.5);	setPoint(7, t,0,s+0.5);
		setPoint(2, -s,t,0+0.5);	setPoint(8, s,t,0+0.5);
		setPoint(3, 0,s,t+0.5);	setPoint(9, 0,-s,t+0.5);
		setPoint(4, -t,0,-s+0.5);	setPoint(10,-t,0,s+0.5);
		setPoint(5,-s,-t,0+0.5);	setPoint(11, s,-t,0+0.5);
		
		faces[0] = new Face(0,1,6); faces[1] = new Face(0,6,4);
		faces[2] = new Face(1,8,7); faces[3] = new Face(1,7,11);
		faces[4] = new Face(2,3,0); faces[5] = new Face(2,3,8);
		
		faces[6] = new Face(3,9,7);  faces[7] = new Face(3,10,9);
		faces[8] = new Face(4,10,2); faces[9] = new Face(4,5,10);
		faces[10] = new Face(5,6,11);faces[11] = new Face(5,11,9);
		
		faces[12] = new Face(0,8,1); faces[13] = new Face(6,1,11);
		faces[14] = new Face(6,5,4); faces[15] = new Face(0,4,2);
		
		faces[16] = new Face(7,8,3); faces[17] = new Face(10,3,2);
		faces[18] = new Face(10,5,9);faces[19] = new Face(9,11,7);
	}
};

/**
 * A class for creation of a leaf in the mesh of all leaves.
 * It is initialized with a leaf shape name. The leaf mesh is
 * located at the origin. To create a leaf at it's position,
 * you have to apply the leafs transformation to the leaf mesh
 * points.
 * 
 * @author wdiestel
 */
public class LeafMesh {
	
	LeafShape shape;
	long faceOffset;
	
	public LeafMesh(String leafShape, double length, double width, double stemLen, boolean useQuads) {
		Pattern pattern = Pattern.compile("disc(\\d*)");
		Matcher m = pattern.matcher(leafShape);
		// disc shape
		if (m.matches()) {
			// FIXME: given "disc" without a number, the face count could 
			// be dependent from the smooth value
			int facecnt = 6;
			if (! m.group(1).equals("")) {
				facecnt = Integer.parseInt(m.group(1));
			}
			shape = new DiscShape(facecnt,length,width,stemLen,useQuads);
		} else if (leafShape.equals("square")) {
			shape = new SquareShape(length,width,stemLen,useQuads);
		} else if (leafShape.equals("sphere")) {
			shape = new SphereShape(length,width,stemLen);
		} else
			// test other shapes like  palm here
		{
			// any other leaf shape, like "0" a.s.o. - use normal disc
			
			// FIXME: given "disc" without a number, the face count could 
			// be dependent from the smooth value
			int facecnt = 6;
			shape = new DiscShape(facecnt,length,width,stemLen,useQuads);
		}
	}
	
	public boolean isFlat() {
		return (shape.getClass() != SphereShape.class);
	}
	
	/**
	 * Returns the i-th vertex.
	 * 
	 * @param i
	 * @return
	 */
	public Vertex shapeVertexAt(int i) {
		return shape.vertices[i];
	}
	
	/**
	 * Returns the i-th uv-vector.
	 * 
	 * @param i
	 * @return
	 */
	public UVVector shapeUVAt(int i) {
		return shape.vertices[i].uv;
	}
	
	/**
	 * Returns the i-th face (triangle).
	 * 
	 * @param i
	 * @return
	 */
	public Face shapeFaceAt(int i) {
		return shape.faces[i];
	}
	
	/**
	 * Returns the number of vertices the leaf mesh consist of.
	 * 
	 * @return
	 */
	public int getShapeVertexCount() {
		return shape.getVertexCount();
	}

	
	/**
	 * Returns the number ov faces the leaf mesh consists of.
	 * 
	 * @return
	 */
	public int getShapeFaceCount() {
		return shape.getFaceCount();
	}
	
};






