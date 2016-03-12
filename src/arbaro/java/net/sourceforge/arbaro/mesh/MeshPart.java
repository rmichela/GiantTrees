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

import java.lang.Math;
import java.util.Enumeration;
//import java.util.NoSuchElementException;

import net.sourceforge.arbaro.tree.Stem;

/**
 * 
 * A class for creation, handling and output of a piece of a mesh object.
 * A mesh part represents a stem of the tree.
 * 
 * @author wdiestel
 *
 */
public class MeshPart extends java.util.Vector {
	Stem stem;
	boolean useNormals;
	boolean useQuads;
	
	private class VertexEnumerator implements Enumeration {
		private Enumeration sections;
		private Enumeration sectionVertices;
		private boolean UVVertices;
		
		public VertexEnumerator(boolean uv) {
			UVVertices = uv;
			sections = elements();
			
			// ignore root point
			sections.nextElement();
			sectionVertices = ((MeshSection)sections.nextElement()).allVertices(UVVertices);
		}
		
		public boolean hasMoreElements() {
			if (! sectionVertices.hasMoreElements() && sections.hasMoreElements()) {
				sectionVertices = ((MeshSection)sections.nextElement()).allVertices(UVVertices);
			}
			return sectionVertices.hasMoreElements();
		}
		
		public Object nextElement() {
			if (! sectionVertices.hasMoreElements() && sections.hasMoreElements()) {
				sectionVertices = ((MeshSection)sections.nextElement()).allVertices(UVVertices);
			}
			return sectionVertices.nextElement();
		}
	}

	private class FaceEnumerator implements Enumeration {
		private Enumeration sections;
		private Enumeration sectionFaces;
		private MeshSection section;
		private boolean UVFaces;
		private boolean useQuads;
		private int startIndex;
		private int sectionSize;
		
		public FaceEnumerator(Mesh mesh, int startInx, boolean uv, boolean quads) {
			UVFaces = uv;
			startIndex = startInx;
			useQuads = quads;
			sections = elements();
			
			// ignore root point
			MeshSection sec = (MeshSection)sections.nextElement();
			
			// if it's a clone calculate a vertex offset
			// finding the corresponding segment in the parent stem's mesh
			int uvVertexOffset=0; 
			if (uv && stem.isClone()) {
				MeshPart mp = ((MeshPart)mesh.elementAt(mesh.firstMeshPart[stem.stemlevel]));
				for (MeshSection ms=((MeshSection)mp.elementAt(1)); // ignore root vertex
					ms.next.segment.index < sec.segment.index;
					ms = ms.next) {
					
					uvVertexOffset += ms.size()==1? 1 : ms.size()+1;
				}
				
				startIndex += uvVertexOffset;
			}
			
			nextSection(true);
		}
		
		private void nextSection(boolean firstSection) {
			if (! firstSection) {
				if (UVFaces) 
					startIndex += section.size()==1? 1 : section.size()+1;
				else
					startIndex += section.size();
			}
			
			section = (MeshSection)sections.nextElement(); 
			sectionFaces = section.allFaces(startIndex,UVFaces,useQuads);
		}
		
		public boolean hasMoreElements() {
			if (! sectionFaces.hasMoreElements() && sections.hasMoreElements()) {
				nextSection(false);
			}
			return sectionFaces.hasMoreElements();
		}
		
		public Object nextElement() {
			if (! sectionFaces.hasMoreElements() && sections.hasMoreElements()) {
				nextSection(false);
			}
			return sectionFaces.nextElement();
		}
	}
	
	
	public MeshPart(Stem aStem, boolean normals, boolean quads) { 
		// FIXME normals not yet used,
		// other mesh output format needed if theire are
		// less normals then vertices
		useNormals = normals;
		useQuads = quads;
		stem = aStem;
	}
	
	public Stem getStem() {
		return stem;
	}
	
	public String getTreePosition() {
		return stem.getTreePosition();
	}
	
	public int getLevel() {
		return stem.stemlevel;
	}
	
	/**
	 * Adds a mesh section to the mesh part.
	 * 
	 * @param section
	 */
	public void addSection(MeshSection section) {
		//if (stem.tree.verbose) cerr << (".");
		
		if (size() > 0) {
			// connect section with last of sections
			((MeshSection)lastElement()).next = section;
			section.previous = (MeshSection)lastElement();
		}
		
		addElement(section);
	}
	
	public Enumeration allVertices(boolean UVVertices) {
		return new VertexEnumerator(UVVertices);
	}
	
	public Enumeration allFaces(Mesh mesh, int startIndex, boolean UVFaces) {
		return new FaceEnumerator(mesh,startIndex,UVFaces,useQuads);
	}
	
	
	/**
	 * Sets the normals in all mesh sections
	 * 
	 */
	public void setNormals() {
		// normals for begin and end point (first and last section)
		// are set to a z-Vector by Segment.mesh()
		//System.err.println("MESHSECT 1: "+((MeshSection)elementAt(1)).size());
		
		if (size()>1) {
			((MeshSection)elementAt(1)).setNormalsUp();
			for (int i=2; i<size()-1; i++) {
				((MeshSection)elementAt(i)).setNormalsUpDown();
			}
			//DBG
			//MeshSection m = (MeshSection)elementAt(i);
			//System.err.println("MeshSection "+i+" vert: "+m.size());
			
		} else {
			System.err.println("WARNING: degnerated MeshPart with only "+size()+" sections at"+
					" tree position "+stem.getTreePosition()+".");
		}
		
		//DBG
		/*
		 try {
		 MeshSection m = (MeshSection)lastElement();
		 System.err.println("MeshSection last vert: "+m.size()+" normal[0] "+m.normal_at(0).povray());
		 } catch (Exception e) {}
		 */
		// sections.last().set_normals_down();
	}
	
	
	
	/**
	 * Returns the number of all meshpoints of all sections
	 * 
	 * @return
	 */
	public int vertexCount() {
		int cnt=0;
		
		for (int i = 1; i<size(); i++) {
			cnt += ((MeshSection)elementAt(i)).size();
		}
		return cnt;
	}
	
	/**
	 * Returns the number of uv vectors for the mesh part
	 * 
	 * @return
	 */
	public int uvCount() {
		int cnt=0;
		
		for (int i = 1; i<size(); i++) {
			cnt += ((MeshSection)elementAt(i)).size()==1 ?
					1 : ((MeshSection)elementAt(i)).size()+1;
		}
		return cnt;
	}
	
	
	/**
	 * Returns the number of faces, that have to be created - povray wants 
	 * to know this before the faces itself
	 *
	 * @return
	 */
	public int faceCount()  {
		int cnt = 0;
		for (int i=1; i<size()-1; i++) {
			int c_i = ((MeshSection)elementAt(i)).size();
			int c_i1 = ((MeshSection)elementAt(i+1)).size();
			
			if (c_i != c_i1) {
				cnt += Math.max(c_i,c_i1);
			} else if (c_i > 1) {
				cnt += 2 * c_i;
			}
		}
		return cnt;
	}
	
	/**
	 * Returns the triangles between a section and the next
	 * section.
	 * 
	 * @param inx
	 * @param section
	 * @return
	 * @throws MeshError
	 */
	public java.util.Vector faces(long inx, MeshSection section) throws MeshError {
		MeshSection next = section.next;
		java.util.Vector faces = new java.util.Vector();
		
		if (section.size() ==1 && next.size() == 1) {
			// normaly this shouldn't occur, only for very small radius?
			// I should warn about this
			System.err.println("WARNING: two adjacent mesh sections with only one point.");
			return faces;
		}
		
		// if it's the first section (root vertex of the stem)
		// make triangles to fill the base section
		if (section.isFirst()) {
			for (int i=1; i<next.size()-1; i++) {
				faces.addElement(new Face(inx,inx+i,inx+i+1));
			}
		}

		// if the section has only one vertex, draw triangles
		// to every point of the next secion
		else if (section.size() == 1) {
			for (int i=0; i<next.size(); i++) {
				faces.addElement(new Face(inx,inx+1+i,inx+1+(i+1)%next.size()));
			}
			
		} else if (next.size() == 1) {
			long ninx = inx+section.size();
			for (int i=0; i<section.size(); i++) {
				faces.addElement(new Face(inx+i,ninx,inx+(i+1)%section.size()));
			}
			
		} else { // section and next must have same point_cnt>1!!!
			long ninx = inx+section.size();
			if (section.size() != next.size()) {
				throw new MeshError("Error: vertice numbers of two sections "
						+ "differ ("+inx+","+ninx+")");
			}
			for (int i=0; i<section.size(); i++) {
				if (useQuads) {
					faces.addElement(new Face(inx+i,ninx+i,
							ninx+(i+1)%next.size(),inx+(i+1)%section.size()));
				} else {
					faces.addElement(new Face(inx+i,ninx+i,inx+(i+1)%section.size()));
					faces.addElement(new Face(inx+(i+1)%section.size(),ninx+i,
						ninx+(i+1)%next.size()));
				}
			}
		}
		
		return faces;
	}
	

	/**
	 * Returns the triangles between a section and the next
	 * section.
	 * 
	 * @param section
	 * @return
	 * @throws MeshError
	 */
	public java.util.Vector vFaces(MeshSection section) throws MeshError {
		MeshSection next = section.next;
		java.util.Vector faces = new java.util.Vector();
		
		if (section.size() ==1 && next.size() == 1) {
			// normaly this shouldn't occur, only for very small radius?
			// I should warn about this
			System.err.println("WARNING: two adjacent mesh sections with only one point.");
			return faces;
		}
		
		// if it's the first section (root vertex of the stem)
		// make triangles to fill the base section
		if (section.isFirst()) {
			for (int i=1; i<next.size()-1; i++) {
				faces.addElement(new VFace(
							next.pointAt(0),
							next.pointAt(i),
							next.pointAt(i+1)));
			}
		}		
		
		else if (section.size() == 1) {
			for (int i=0; i<next.size(); i++) {
				faces.addElement(new VFace(
							section.pointAt(0),
							next.pointAt(i),
// FIXME: this %next.size should be handled in MeshSection.pointAt,
// this would be easier to use							
							next.pointAt((i+1)%next.size())));
			}
		} else if (next.size() == 1) {
			for (int i=0; i<section.size(); i++) {
				faces.addElement(new VFace(
						section.pointAt(i),
						next.pointAt(0),
						section.pointAt((i+1)%section.size())
						));
			}
		} else { // section and next must have same point_cnt>1!!!
			if (section.size() != next.size()) {
				throw new MeshError("Error: vertice numbers of two sections "
						+ "differ ("+section.size()+","+next.size()+")");
			}
			for (int i=0; i<section.size(); i++) {
				faces.addElement(new VFace(
						section.pointAt(i),
						next.pointAt(i),
						section.pointAt((i+1)%section.size())
						));
				faces.addElement(new VFace(
						section.pointAt((i+1)%section.size()),
						next.pointAt(i),
						next.pointAt((i+1)%next.size())));
			}
		}
		return faces;
	}

	
	/**
	 * Returns the texture's uv-coordinates of the triangles between a section and the next
	 * section.
	 * 
	 * @param inx
	 * @param section
	 * @return
	 * @throws MeshError
	 */
	public java.util.Vector uvFaces(long inx, MeshSection section, Mesh mesh) throws MeshError {
		MeshSection next = section.next;
		java.util.Vector faces = new java.util.Vector();
		
		if (section.size() ==1 && next.size() == 1) {
			// normaly this shouldn't occur, only for very small radius?
			// I should warn about this
			System.err.println("WARNING: two adjacent mesh sections with only one point.");
			return faces;
		}
		

		// if it's a clone calculate a vertex offset
		// finding the corresponding segment in the parent stem's mesh
		int uvVertexOffset=0; 
		if (stem.isClone()) {
			MeshPart mp = ((MeshPart)mesh.elementAt(mesh.firstMeshPart[stem.stemlevel]));
			for (MeshSection ms=((MeshSection)mp.elementAt(1)); // ignore root vertex
				ms.next.segment.index < section.segment.index;
				ms = ms.next) {
				
				uvVertexOffset += ms.size()==1? 1 : ms.size()+1;
			}
			
		}

		long ninx; // start index of next section

		
		// first section of clone starts at the bottom of the texture
		// but all following are raised to the corresponding segment
		// of the parent stem
		if (section.size()>1) {
			ninx = inx+section.size()+1+uvVertexOffset;
		} else {
			ninx = 1+uvVertexOffset;
		}
		if (section.isFirst()) {
			inx += uvVertexOffset;
		} 

		// if it's the first section (root vertex of the stem)
		// make triangles to fill the base section
		if (section.isFirst()) {
			for (int i=1; i<next.size()-1; i++) {
				faces.addElement(new Face(inx,inx+i,inx+i+1));
			}
		}
		
		else if (section.size() == 1) {
			for (int i=0; i<next.size(); i++) {
				faces.addElement(new Face(inx,inx+ninx+i,inx+ninx+(i+1)));
			}
		} 
		
		else if (next.size() == 1) {
			for (int i=0; i<section.size(); i++) {
				faces.addElement(new Face(inx+i,ninx,inx+(i+1)));
			}
		} 
		
		else { // section and next must have same point_cnt>1!!!
			if (section.size() != next.size()) {
				throw new MeshError("Error: vertex numbers of two sections "
						+ "differ ("+inx+","+ninx+")");
			}
			for (int i=0; i<section.size(); i++) {
				if (useQuads) {
					faces.addElement(new Face(inx+i,ninx+i,ninx+(i+1),inx+(i+1)));
				} else {
					faces.addElement(new Face(inx+i,ninx+i,inx+(i+1)));
					faces.addElement(new Face(inx+(i+1),ninx+i,
							ninx+(i+1)));
				}
			}
		}
		
		return faces;
	}

/*	
	public java.util.Vector uvFaces(MeshSection section) throws ErrorMesh {
		MeshSection next = section.next;
		java.util.Vector faces = new java.util.Vector();
		
		if (section.size() ==1 && next.size() == 1) {
			// normaly this shouldn't occur, only for very small radius?
			// I should warn about this
			System.err.println("WARNING: two adjacent mesh sections with only one point.");
			return faces;
		}
		
		if (section.size() == 1) {
			for (int i=0; i<next.size(); i++) {
				faces.addElement(new UVFace(
							section.uvAt(0),
							next.uvAt(i),
							next.uvAt(i+1)
							));
			}
		} else if (next.size() == 1) {
			for (int i=0; i<section.size(); i++) {
				faces.addElement(new UVFace(
						section.uvAt(i),
						next.uvAt(0),
						section.uvAt(i+1)
						));
			}
		} else { // section and next must have same point_cnt>1!!!
			if (section.size() != next.size()) {
				throw new ErrorMesh("Error: vertice numbers of two sections "
						+ "differ ("+section.size()+","+next.size()+")");
			}
			for (int i=0; i<section.size(); i++) {
				faces.addElement(new UVFace(
						section.uvAt(i),
						next.uvAt(i),
						section.uvAt(i+1)
						));
				faces.addElement(new UVFace(
						section.uvAt(i+1),
						next.uvAt(i),
						next.uvAt(i+1)));
			}
		}
		return faces;
	}
*/
	
};












