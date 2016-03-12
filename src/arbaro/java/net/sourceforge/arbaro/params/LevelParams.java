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

package net.sourceforge.arbaro.params;

import java.io.PrintWriter;

public class LevelParams {
	// parameters for the 4 levels
	
	public int level;
	
	// stem length and appearance
	public double nTaper; // taper to a point (cone)
	public int nCurveRes;
	public double nCurve;
	public double nCurveV;
	public double nCurveBack;
	public double nLength;
	public double nLengthV;
	
	// splitting
	public double nSegSplits;
	public double nSplitAngle;
	public double nSplitAngleV;
	
	// substems
	public int nBranches;
	
	/**
	 * <code>nBranchDist</code>
	 * is the substem distance within a segment 
	 * <ul>
	 * <li>0: all substems at segment base</li>
	 * <li>1: distributed over full segment</li>
	 * </ul>
	 * 
	 */
	public double nBranchDist; 
	
	public double nDownAngle;
	public double nDownAngleV;
	public double nRotate;
	public double nRotateV;
	
	/**
	 * <code>mesh_points</code> -
	 * how many meshpoints per cross-section
	 */
	public int mesh_points; 
	
	// Error values for splitting, substem and leaf distribution
	public double splitErrorValue;
	public double substemErrorValue;
	
	/**
	 * random generators
	 */
	public Random random; 
	
	// param DB
	java.util.Hashtable paramDB;
	
	
	// variables to store state when making prune test
	private long randstate;
	private double spliterrval;
	
	public LevelParams(int l, java.util.Hashtable parDB) {
		level = l;
		paramDB = parDB;
		
		randstate = Long.MIN_VALUE;
		spliterrval = Double.NaN;
	}
	
	public long initRandom(long seed) {
		random = new Random(seed);
		return random.nextLong();
	}
	
	public double var(double variation) {
		// return a random variation value from (-variation,+variation)
		return random.uniform(-variation,variation);
	}
	
	public void saveState() {
		/*
		 if (Double.isNaN(spliterrval)) {
		 System.err.println("BUG: cannot override state earlier saved, "
		 + "invoke restoreState first");
		 System.exit(1);
		 }
		 */
		randstate = random.getState();
		spliterrval= splitErrorValue;
	}
	
	public void restoreState() {
		if (Double.isNaN(spliterrval)) {
			System.err.println("BUG: there is no state saved, cannot restore.");
			System.exit(1);
		}
		random.setState(randstate);
		splitErrorValue=spliterrval;
	}
	
	// help methods for output of params
	private void writeParamXml(PrintWriter w, String name, int value) {
		name = "" + level + name.substring(1);
		w.println("    <param name='" + name + "' value='"+value+"'/>");
	}
	
	private void writeParamXML(PrintWriter w, String name, double value) {
		name = "" + level + name.substring(1);
		w.println("    <param name='" + name + "' value='"+value+"'/>");
	}
	
	void toXML(PrintWriter w, boolean leafLevelOnly) {
		w.println("    <!-- level " + level  + " -->");
		writeParamXML(w,"nDownAngle",nDownAngle);
		writeParamXML(w,"nDownAngleV",nDownAngleV);
		writeParamXML(w,"nRotate",nRotate);
		writeParamXML(w,"nRotateV",nRotateV);
		if (! leafLevelOnly) {
			writeParamXml(w,"nBranches",nBranches);
			writeParamXML(w,"nBranchDist",nBranchDist);
			//	    xml_param(w,"nBranchDistV",nBranchDistV);
			writeParamXML(w,"nLength",nLength);
			writeParamXML(w,"nLengthV",nLengthV);
			writeParamXML(w,"nTaper",nTaper);
			writeParamXML(w,"nSegSplits",nSegSplits);
			writeParamXML(w,"nSplitAngle",nSplitAngle);
			writeParamXML(w,"nSplitAngleV",nSplitAngleV);
			writeParamXml(w,"nCurveRes",nCurveRes);
			writeParamXML(w,"nCurve",nCurve);
			writeParamXML(w,"nCurveBack",nCurveBack);
			writeParamXML(w,"nCurveV",nCurveV);
		}
	}
	
	// help method for loading params
	private int intParam(String name) throws ParamError {
		name = "" + level + name.substring(1);
		IntParam par = (IntParam)paramDB.get(name);
		if (par != null) {
			return par.intValue();
		} else {
			throw new ParamError("bug: param "+name+" not found!");
		}   
	}
	
	private double dblParam(String name) throws ParamError {
		name = "" + level + name.substring(1);
		FloatParam par = (FloatParam)paramDB.get(name);
		if (par != null) {
			return par.doubleValue();
		} else {
			throw new ParamError("bug: param "+name+" not found!");
		}   
	}
	
	void fromDB(boolean leafLevelOnly) throws ParamError {
		if (! leafLevelOnly) {
			nTaper = dblParam("nTaper");
			nCurveRes = intParam("nCurveRes");
			nCurve = dblParam("nCurve");
			nCurveV = dblParam("nCurveV");
			nCurveBack = dblParam("nCurveBack");
			nLength = dblParam("nLength");
			nLengthV = dblParam("nLengthV");
			nSegSplits = dblParam("nSegSplits");
			nSplitAngle = dblParam("nSplitAngle");
			nSplitAngleV = dblParam("nSplitAngleV");
			nBranches = intParam("nBranches");
		}
		nBranchDist = dblParam("nBranchDist");
		//	nBranchDistV = dbl_param("nBranchDistV");
		nDownAngle = dblParam("nDownAngle");
		nDownAngleV = dblParam("nDownAngleV");
		nRotate = dblParam("nRotate");
		nRotateV = dblParam("nRotateV");
	}
	
	
};

























