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

import net.sourceforge.arbaro.export.*;
import net.sourceforge.arbaro.mesh.LeafMesh;
import net.sourceforge.arbaro.mesh.Mesh;
import net.sourceforge.arbaro.params.*;
import net.sourceforge.arbaro.transformation.Transformation;
import net.sourceforge.arbaro.transformation.Vector;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * A class for creation of threedimensional tree objects.
 * A tree has one or more trunks, with several levels of
 * branches all of which are instances of the Stem class.
 * <p>
 * See this class diagram for the parts of a Tree:
 * <p>
 * <img src="doc-files/Tree-1.png" />
 * <p>
 * 
 * @author Wolfram Diestel
 *
 */
public class Tree {
	// Outputformats
	public final static int MESH = 0;
	public final static int CONES = 1;
	public final static int DXF = 2;
	public final static int OBJ=3;
	
	final static String[] formats = { "Povray meshes","Povray primitives","AutoCAD DXF","Wavefront OBJ" };

	public Params params;

	int outputType = MESH;
	String outputPath = System.getProperty("user.dir")
		+System.getProperty("file.separator")+"pov";
	int renderW = 400;
	int renderH = 600;
	boolean outputStemUVs = false;
	boolean outputLeafUVs = false;
	
	// the trunks (one for trees, many for bushes)
	public java.util.Vector trunks;
	double trunk_rotangle = 0;
	
	Progress progress;
	
	Vector maxPoint;
	Vector minPoint;
	public Vector getMaxPoint() { return maxPoint; }
	public Vector getMinPoint() { return minPoint; }
	public double getHeight() { return maxPoint.getZ(); }
	public double getWidth() {
		return Math.sqrt(Math.max(
				 minPoint.getX()*minPoint.getX()
				+minPoint.getY()*minPoint.getY(),
				 maxPoint.getX()*maxPoint.getX()
				+maxPoint.getY()*maxPoint.getY())); 
	}

	// TODO: use hierachical visitor pattern instead of enumerators
	// Visitor methods: visit{Enter|Leave}(Tree), visit{Enter|Leave}(Stem), 
	//    visit(Leaf)
	// Some kinds of such visitors could be: StemCounter, LeafCounter,
	// MeshCreator, POVExporter ...
	
	// FIXME: may be could use StemEnumerator as a base
	// and overload only find_next_stem and getNext a little???
/*	
	private class LeafEnumerator implements Enumeration {
		private Enumeration stems;
		private Enumeration leaves;
		
		public LeafEnumerator() {
			stems = trunks.elements();
			leaves = ((Stem)stems.nextElement()).allLeaves();
		}
		
		public boolean hasMoreElements() {
			if (params.Leaves==0) return false;
			else if (leaves.hasMoreElements()) return true;
			else if (! stems.hasMoreElements()) return false;
			else {
				// goto next trunk with leaves
				while (! leaves.hasMoreElements() && stems.hasMoreElements()) {
					Stem s = (Stem)stems.nextElement();
					leaves = s.allLeaves();
				}
				return leaves.hasMoreElements();
			}
		}
		
		public Object nextElement() {
			// this will go to the next trunk with leaves,
			// if the current has no more of them
			if (hasMoreElements()) {
				return leaves.nextElement();
			} else {
				throw new NoSuchElementException("LeafEnumerator");
			}
		}
	}
	
	public Enumeration allStems(int level) {
		return new StemEnumerator(level,trunks.elements(),0);
	}
	
	public Enumeration allLeaves() {
		return new LeafEnumerator();
	}
	*/
	
	/**
	 * Creates a new tree object 
	 */
	public Tree() {
		params = new Params();
		trunks = new java.util.Vector();
		newProgress();
	}
	
	/**
	 * Creates a new tree object copying the parameters
	 * from an other tree
	 * 
	 * @param other the other tree, from wich parameters are taken
	 */
	public Tree(Tree other) {
		params = new Params(other.params);
		trunks = new java.util.Vector();
		outputType = other.getOutputType();
		outputPath = other.getOutputPath();
		renderW = other.getRenderW();
		renderH = other.getRenderH();
		outputStemUVs = other.outputStemUVs;
		outputLeafUVs = other.outputLeafUVs;
		newProgress();
	}
	
	public void clear() {
		trunks = new java.util.Vector();
		newProgress();
	}
	
	/**
	 * Generates the tree. The following collaboration diagram
	 * shows the recursion trough the make process:
	 * <p>
	 * <img src="doc-files/Tree-2.png" />
	 * <p> 
	 * 
	 * @throws Exception
	 */
	public void make() throws Exception {
		setupGenProgress();
		params.prepare();
		maxPoint = new Vector(-Double.MAX_VALUE,-Double.MAX_VALUE,-Double.MAX_VALUE);
		minPoint = new Vector(Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE);
		
		if (params.verbose) {
			// FIXME: move Seed back to Tree and give it to Params.prepare(Seed) only
			System.err.println("Tree species: " + params.Species + ", Seed: " 
					+ params.Seed);
			System.err.println("Output: " + (outputType == MESH? "mesh":"cones"));
			if (outputType==MESH) { 
				for (int l=0; l<Math.min(params.Levels,4); l++) {
					System.err.println("  Level " + l + ": vertices/section: " 
							+ params.levelParams[l].mesh_points + ", smooth: " 
							+ (params.smooth_mesh_level>=l? "yes" : "no"));
				}
			}
			
			System.err.println("making " + params.Species + "(" + params.Seed + ") ");
		}
		
		// create the trunk and all its stems and leaves
		Transformation transf = new Transformation();
		Transformation trf;
		double angle;
		double dist;
		LevelParams lpar = params.levelParams[0];
		for (int i=0; i<lpar.nBranches; i++) {
			trf = trunkDirection(transf,lpar);
			angle = lpar.var(360);
			dist = lpar.var(lpar.nBranchDist);
			trf = trf.translate(new Vector(dist*Math.sin(angle),
					dist*Math.cos(angle),0));
			Stem trunk = new Stem(this,null,0,trf,0);
			trunks.addElement(trunk);
			trunk.index=0;
			trunk.make();
		}
		
		// making finished
		if (params.verbose) System.err.println(".");
		progress.endPhase();
	}
	
	/**
	 * Calcs the trunk direction. Of special importance for plants with
	 * multiple trunks.
	 * 
	 * @param trf The original transformation
	 * @param lpar The parameters for the trunk (level 0)
	 * @return The transformation after giving the trunk a new direction
	 */
	Transformation trunkDirection(Transformation trf, LevelParams lpar) {
		
		// get rotation angle
		double rotangle;
		if (lpar.nRotate>=0) { // rotating trunk
			trunk_rotangle = (trunk_rotangle + lpar.nRotate+lpar.var(lpar.nRotateV)+360) % 360;
			rotangle = trunk_rotangle;
		} else { // alternating trunks
			if (Math.abs(trunk_rotangle) != 1) trunk_rotangle = 1;
			trunk_rotangle = -trunk_rotangle;
			rotangle = trunk_rotangle * (180+lpar.nRotate+lpar.var(lpar.nRotateV));
		}
		
		// get downangle
		double downangle;
		downangle = lpar.nDownAngle+lpar.var(lpar.nDownAngleV);
		
		return trf.rotxz(downangle,rotangle);
	}
	
	public boolean traverseTree(TreeTraversal traversal)  throws TraversalException{
	    if (traversal.enterTree(this))  // enter this tree?
        {
             Enumeration stems = trunks.elements();
             while (stems.hasMoreElements())
                if (! ((Stem)stems.nextElement()).traverseTree(traversal))
                        break;
        }

        return traversal.leaveTree(this);
	}

	
	public void output(PrintWriter w) throws Exception {
		progress.beginPhase("output tree code",-1);
		
		// output povray code
		if (params.verbose) System.err.print("writing tree code ");
		
		Exporter output;
		if (outputType == CONES) {
			output = new POVConeExporter(this,w);
		} else if (outputType == DXF) {
			output = new DXFExporter(this,w,progress);
		} else if (outputType == OBJ) {
			output = new OBJExporter(this,w);
			((OBJExporter)output).outputStemUVs = outputStemUVs;
			((OBJExporter)output).outputLeafUVs = outputLeafUVs;
		} else {
			output = new POVMeshExporter(this,w);
			((POVMeshExporter)output).outputStemUVs = outputStemUVs;
			((POVMeshExporter)output).outputLeafUVs = outputLeafUVs;
		}
		output.write();
		
		if (params.verbose) System.err.println();
		progress.endPhase();
	}
	
	// TODO should be moved to caller class, when TreeTraversals are working
	public Mesh createStemMesh(boolean useQuads) throws Exception {
		progress.beginPhase("Creating mesh",getStemCount());
		
		Mesh mesh = new Mesh(params.Levels);
/*		for (int t=0; t<trunks.size(); t++) {
			((Stem)trunks.elementAt(t)).addToMesh(mesh,true,useQuads);
		}
		getProgress().incProgress(trunks.size());
		*/
		MeshCreator meshCreator = new MeshCreator(mesh, -1, useQuads, progress);
		traverseTree(meshCreator);
		
		progress.endPhase();
		return mesh;
	}

	// TODO should be moved to caller class, when TreeTraversals are working
	public Mesh createStemMeshByLevel(boolean useQuads) throws Exception {
		progress.beginPhase("Creating mesh",getStemCount());
		
		Mesh mesh = new Mesh(params.Levels);
		
		/*
		for (int level=0; level < params.Levels; level++) {
			Enumeration stems = allStems(level);
			while (stems.hasMoreElements()) {
				((Stem)stems.nextElement()).addToMesh(mesh,false,useQuads);
				getProgress().incProgress(1);
			}
		}
		*/
		for (int level=0; level < params.Levels; level++) {
			MeshCreator meshCreator = new MeshCreator(mesh, level, useQuads, progress);
			traverseTree(meshCreator);
		}
			
		progress.endPhase();
		return mesh;
	}
	
	public LeafMesh createLeafMesh(boolean useQuads) {
		double leafLength = params.LeafScale/Math.sqrt(params.LeafQuality);
		double leafWidth = params.LeafScale*params.LeafScaleX/Math.sqrt(params.LeafQuality);
		return new LeafMesh(params.LeafShape,leafLength,leafWidth,params.LeafStemLen,useQuads);
	}
	
	public void minMaxTest(Vector pt) {
		maxPoint.setMaxCoord(pt);
		minPoint.setMinCoord(pt);
	}
	
	
	/**
	 * Outputs a simple Povray scene showing the generated tree
	 * 
	 * @param w
	 */
	public void outputScene(PrintWriter w) throws Exception {
		Exporter output = new PovSceneExporter(this,w);
		output.write();
	}
	
	
	/*
	 void Tree::dump() const {
	 cout << "TREE:\n";
	 // trunk.dump();
	  }
	  */
	
	/**
	 * Returns a parameter group
	 * 
	 * @param level The branch level (0..3)
	 * @param group The parameter group name
	 * @return A hash table with the parameters
	 */
	public java.util.TreeMap getParamGroup(int level, String group) {
		return params.getParamGroup(level,group);
	}
	
	/**
	 * Clear all parameter values of the tree.
	 */
	public void clearParams() {
		params.clearParams();
	}
	
	/**
	 * Read parameter values from an XML definition file
	 * 
	 * @param is The input XML stream
	 * @throws ParamError
	 */
	public void readFromXML(InputStream is) throws ParamError {
		params.readFromXML(is);
	}
	
	/**
	 * Writes out the parameters to an XML definition file
	 * 
	 * @param out The output stream
	 * @throws ParamError
	 */
	public void toXML(PrintWriter out) throws ParamError {
		params.toXML(out);
	}
	
//	/**
//	 * Sets the species name of the tree
//	 * 
//	 * @param sp
//	 */
//	public void setSpecies(String sp) {
//		params.setSpecies(sp);
//	}
//	
//	/**
//	 * Returns the species name of the tree
//	 * 
//	 * @return the species name
//	 */
//	public String getSpecies() {
//		return params.getSpecies();
//	}
//	
	/**
	 * Returns the random seed for the tree
	 * 
	 * @return the random seed
	 */
	public int getSeed() {
		return params.Seed;
	}
	
	/**
	 * Sets the random seed for the tree
	 * 
	 * @param s
	 */
	public void setSeed(int s) {
		params.Seed = s;
	}
	
//	/**
//	 * Returns the smooth value. It influences the number of vertices 
//	 * and usage of vertice normals in the generated mesh,
//	 * 
//	 * @return the smooth value (0.0...1.0)
//	 */
//	public double getSmooth() {
//		return params.Smooth;
//	}
//	
//	/**
//	 * Sets the smooth value. It influences the number of vertices 
//	 * and usage of vertice normals in the generated mesh,
//	 */
//	public void setSmooth(double s) {
//		params.Smooth = s;
//	}
	
	// TODO will be obsolet, when TreeTraversals are working
	public long getLeafCount() {
		if (params.Leaves==0) return 0;
		
		long leafCount = 0;
		
		for (int t=0; t<trunks.size(); t++) {
			leafCount += ((Stem)trunks.elementAt(t)).leafCount();
		}
		return leafCount;
	}
	
	public void setParam(String param, String value) throws ParamError {
		params.setParam(param,value);
	}
	
	public AbstractParam getParam(String param) {
		return params.getParam(param);
	}
	
	
	/**
	 * Sets the output type for the Povray code 
	 * (primitives like cones, spheres and discs or
	 * triangle meshes)
	 * 
	 * @param output
	 */
	public void setOutputType(int output) {
		outputType = output;
	}
	
	public int getOutputType() {
		return outputType;
	}
	
	public static String[] getOutputTypes() {
		return formats;
	}

	public String getOutputPath() {
		return outputPath;
	}
	
	public void setOutputPath(String p) {
		outputPath=p;
	}
	
	public void setRenderW(int w) {
		renderW = w;
	}
	
	public void setRenderH(int h) {
		renderH=h;
	}
	
	public int getRenderH() {
		return renderH;
	}
	
	public int getRenderW() {
		return renderW;
	}
	
	public void setOutputStemUVs(boolean oUV) {
		outputStemUVs = oUV;
	}
	
	public boolean getOutputStemUVs() {
		return outputStemUVs;
	}

	public void setOutputLeafUVs(boolean oUV) {
		outputLeafUVs = oUV;
	}

	public boolean getOutputLeafUVs() {
		return outputLeafUVs;
	}

	public Progress getProgress() {
		return progress;
	}
	
	public void newProgress() {
		progress = new Progress();
	}
	
	//    boolean writingCode; 
	//    String progressMsg = "";
	
	/**
	 * Sets the maximum for the progress while generating the tree 
	 */
	public void setupGenProgress() {
		if (progress != null) {
			// max progress = trunks * trunk segments * (first level branches + 1) 
			long maxGenProgress = 
				((IntParam)params.getParam("0Branches")).intValue()
				* ((IntParam)params.getParam("0CurveRes")).intValue()
				* (((IntParam)params.getParam("1Branches")).intValue()+1);
			
			progress.beginPhase("Creating tree structure",maxGenProgress);
		}
	}
	
	public long getStemCount() {
		long stemCount = trunks.size();
		for (int t=0; t<trunks.size(); t++) {
			stemCount += ((Stem)trunks.elementAt(t)).substemTotal();
		}
		return stemCount;
	}
	
	/**
	 * Sets (i.e. calcs) the progress for the process of making the tree
	 * object.
	 */
	long genProgress;
	
	public synchronized void updateGenProgress() {
		try {
			// how much of 0Branches*0CurveRes*(1Branches+1) are created yet
			long sum = 0;
			for (int i=0; i<trunks.size(); i++) {
				Stem trunk = ((Stem)trunks.elementAt(i));
				if (trunk.substems != null) {
					sum += trunk.segments.size() * (trunk.substems.size()+1);
				} else {
					sum += trunk.segments.size();
				}
			}
			
			if (sum-genProgress > progress.getMaxProgress()/100) {
				genProgress = sum;
				progress.setProgress(genProgress);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	
};























