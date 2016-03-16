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

package net.sourceforge.arbaro.export;

import java.io.PrintWriter;
import java.text.NumberFormat;
//import java.util.Enumeration;

import net.sourceforge.arbaro.mesh.*;
import net.sourceforge.arbaro.params.FloatFormat;
//import net.sourceforge.arbaro.tree.Leaf;
import net.sourceforge.arbaro.tree.DefaultTreeTraversal;
import net.sourceforge.arbaro.tree.Leaf;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.transformation.*;

/**
 *  Class with helper functions for writing a DXF file
 */

final class DXFWriter {
	PrintWriter w;
	NumberFormat frm = FloatFormat.getInstance();
	
	public DXFWriter(PrintWriter pw) {
		this.w = pw;
	}

	public void writeHeader(String comment, Vector minPoint, Vector maxPoint) {
		wg(999,comment);
		wg(0,"SECTION");
		wg(2,"HEADER");
		wg(9,"$INSBASE");
		writePoint(new Vector(),0);
		wg(9,"$EXTMIN");
		writePoint(minPoint,0);
		wg(9,"$EXTMAX");
		writePoint(maxPoint,0);
		wg(0,"ENDSEC");
	}
	
	public void writeTables() {
		wg(0,"SECTION");
		wg(2,"TABLES");
		writeLineTable();
		writeLayerTable();
		//writeStyleTable();
		wg(0,"ENDSEC");
	}
	
	public void writeLineTable() {
		wg(0,"TABLE");
		wg(2,"LTYPE");
		wg(70,"1");         // standard flag values
		wg(0,"LTYPE");
		wg(2,"CONTINUOUS"); // ltype name
		wg(70,"64");        // standard flag values
		wg(3,"Solid line"); // ltype text
		wg(72,"65");        // alignment code 'A'   
		wg(73,"0");         // dash length items
		wg(40,"0.000000");  // total pattern length
		wg(0,"ENDTAB");
	}
	
	public void writeLayerTable() {
		wg(0,"TABLE");
		wg(2,"LAYER");
		wg(70,"6");          // standard flag values
		// LAYER 1
		wg(0,"LAYER");
		wg(2,"1");           // layer name
		wg(70,"64");         // standard flag values
		wg(62,"7");          // color number
		wg(6,"CONTINUOUS");  // line type name
		// LAYER 2
		wg(0,"LAYER");       
		wg(2,"2");           // layer name
		wg(70,"64");         // standard flag values
		wg(62,"7");          // color number
		wg(6,"CONTINUOUS");  // line type name
		wg(0,"ENDTAB");
	}
	
	public void writeStyleTable() {
		wg(0,"TABLE");
		wg(2,"STYLE");
		wg(70,"0");           // standard flags
		wg(0,"ENDTAB");
	}
	
	
	public void writeBlocks() {
		wg(0,"SECTION");
		wg(2,"BLOCKS");
		wg(0,"ENDSEC");
	}
	
	public void writeEntitiesBegin() {
		wg(0,"SECTION");
		wg(2,"ENTITIES");
	}
	
	public void writeEntitiesEnd() {
		wg(0,"ENDSEC");
	}
	
	void wg(int code, String val) {
		w.println(Integer.toString(code));
		w.println(val);
	}
	
	public void writeFace(Vector u, Vector v, Vector w,
			String layer) {
		wg(0,"3DFACE");
		wg(8,layer);
		wg(62,"1"); // color
		writePoint(u,0);
		writePoint(v,1);
		writePoint(w,2);
		writePoint(w,3); // repeat last point
	}
	
	public void writeFace(VFace face,String layer) {
		// FIXME: maybe could be faster when putting
		// all into one string and then send to stream?
		wg(0,"3DFACE");
		wg(8,layer);
		wg(62,layer); // different colors for layer 1 and 2
		writePoint(face.points[0],0);
		writePoint(face.points[1],1);
		writePoint(face.points[2],2);
		writePoint(face.points[2],3); // repeat last point
	}
	
	public void writePoint(Vector v, int n) {
		wg(10+n,frm.format(v.getX()));
		wg(20+n,frm.format(v.getZ()));
		wg(30+n,frm.format(v.getY()));
	}

}

/**
 * Exports the mesh's faces to the DXF file
 *
 */
class DXFFaceExporter extends DefaultTreeTraversal {
	LeafMesh leafMesh;
	VFace vFace;
	String layer;
	DXFWriter writer;
	Progress progress;
	Tree tree;
	long leavesProgressCount=0;
	

	/**
	 * 
	 */
	public DXFFaceExporter(PrintWriter pw, String layer) {
		super();
		this.layer = layer;
		this.writer = new DXFWriter(pw);
		vFace = new VFace(new Vector(),new Vector(),new Vector());
	}
	
	public boolean enterTree(Tree tree) {
		this.tree = tree;
		this.leafMesh = tree.createLeafMesh(false);
		this.progress = tree.getProgress();
		return true;
	}

	public boolean visitLeaf(Leaf l) {
		for (int i=0; i<leafMesh.getShapeFaceCount(); i++) {

			Face face = leafMesh.shapeFaceAt(i);
			for (int k=0; k<3; k++) {
				vFace.points[k] = l.transf.apply(
						leafMesh.shapeVertexAt((int)face.points[k]).point);
			}
			
			writer.writeFace(vFace,layer);
		}
		
		incLeavesProgressCount();
		
		return true;
	}
	
	private void incLeavesProgressCount() {
		if (leavesProgressCount++ % 500 == 0) {
			progress.incProgress(500);
			if (tree.params.verbose) System.err.print(".");
		}
	}
	
}

/**
 * Exports a tree mesh as DXF file
 */
public class DXFExporter extends Exporter {
	long stemsProgressCount=0;
	//long leavesProgressCount=0;
	NumberFormat frm = FloatFormat.getInstance();

	/**
	 * @param aTree
	 * @param pw
	 */
	public DXFExporter(Tree aTree, PrintWriter pw, Progress prg) {
		super(aTree, pw, prg);
	}

	private void incStemsProgressCount() {
		if (stemsProgressCount++ % 100 == 0) {
			progress.incProgress(100);
			if (tree.params.verbose) System.err.print(".");
		}
	}
	/*
	private void incLeavesProgressCount() {
		if (leavesProgressCount++ % 500 == 0) {
			progress.incProgress(500);
			if (tree.params.verbose) System.err.print(".");
		}
	}
	*/

	
	public void write() throws ExportError {
		try{
			DXFWriter writer = new DXFWriter(w);
			writer.writeHeader("DXF created with Arbaro, tree species: "+tree.getParam("Species"),
					tree.getMinPoint(),tree.getMaxPoint());
			writer.writeTables();
			writer.writeBlocks();
			writer.writeEntitiesBegin();

			// Geometric entities go here

			// stems on layer 1
			writeStems("1");
			// leafs on layer 2
			writeLeafs("2");
			
			writer.writeEntitiesEnd();
			writer.wg(0,"EOF");
			w.flush();
		}
		catch (Exception e) {
			System.err.println(e);
			throw new ExportError(e.getMessage());
			//e.printStackTrace(System.err);
		}
	}
	
	private void writeStems(String layer) throws Exception {
		// FIXME: optimize speed, maybe using enumerations

		Mesh mesh = tree.createStemMesh(false);
		progress.beginPhase("Writing stem mesh",mesh.size());
		DXFWriter writer = new DXFWriter(w);

		for (int i=0; i<mesh.size(); i++) {
			
			MeshPart mp = (MeshPart)mesh.elementAt(i);
			
			for (int k=0; k<mp.size()-1; k++) { 
				java.util.Vector faces = mp.vFaces((MeshSection)mp.elementAt(k));

				for (int j=0; j<faces.size(); j++) {
					VFace face =(VFace)faces.elementAt(j);
					writer.writeFace(face,layer);
				}
			}
			
			incStemsProgressCount();
		}

		progress.endPhase();
	}
	
	private void writeLeafs(String layer) throws Exception {
		// FIXME: optimize speed, maybe using enumerations
//		LeafMesh leafMesh = tree.createLeafMesh(false);
//		VFace vFace = new VFace(new Vector(),new Vector(),new Vector());
		
		progress.beginPhase("Writing leaf mesh",tree.getLeafCount());

		DXFFaceExporter exporter = new DXFFaceExporter(w,layer);
		tree.traverseTree(exporter);
		
//		Enumeration leaves = tree.allLeaves();
//		
//		while (leaves.hasMoreElements()) {
//			Leaf l = (Leaf)leaves.nextElement();
//			
//			for (int i=0; i<leafMesh.getShapeFaceCount(); i++) {
//
//				Face face = leafMesh.shapeFaceAt(i);
//				for (int k=0; k<3; k++) {
//					vFace.points[k] = l.transf.apply(
//							leafMesh.shapeVertexAt((int)face.points[k]).point);
//				}
//				
//				writeFace(vFace,layer);
//			}
//			
//			incLeavesProgressCount();
//		}
		
		progress.endPhase();
	}


}
