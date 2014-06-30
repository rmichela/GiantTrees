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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TreeMap;

/**
 * Read parameters from Config style text file
 * 
 * @author wolfram
 *
 */
class CfgTreeParser {
	
	public void parse(String fileName, Params params) throws Exception {
		File inputFile = new File(fileName);
		LineNumberReader r = 
			new LineNumberReader(new FileReader(inputFile));
		parse(r,params);
	}
	
	public void parse(InputStream is, Params params) throws Exception {
		LineNumberReader r = new LineNumberReader(new InputStreamReader(is));
		parse(r,params);
	}
	
	public void parse(LineNumberReader r, Params params) throws Exception {
		String line = r.readLine().trim();
		String param;
		String value;
		while (line != null) {
			if (line != "" && line.charAt(0) != '#') {
				int equ = line.indexOf('=');
				param = line.substring(0,equ).trim();
				value = line.substring(equ+1).trim();
				if (param.equals("species")) {
					params.setParam("Species",value);
				} else {
					params.setParam(param,value);
				}
				line = r.readLine();
			}
		}
	}
}

/**
 * Read parameters from XML file
 * 
 * @author wolfram
 *
 */

class XMLTreeFileHandler extends DefaultHandler {
	
	Params params;
	String errors = "";
	
	public XMLTreeFileHandler(Params par) {
		params = par;
	}
	
	public void startElement(String namespaceURI,String localName,
			String qName,Attributes atts) throws SAXException {
		
		try {
			
			if (qName.equals("species")) {
				params.setParam("Species",atts.getValue("name"));
			} else if (qName.equals("param")) {
				
				params.setParam(atts.getValue("name"),atts.getValue("value"));
			}
		} catch (ParamError e) {
			errors += e.getMessage()+"\n";
			// throw new SAXException(e.getMessage());
		}
		
	}
	
	/*
	 public void endElement(String namespaceURI,String localName,
	 String qName) {
	 System.out.println("</" + qName + ">");
	 }
	 */
}


class XMLTreeParser {
	SAXParser parser;
	
	public XMLTreeParser() 
	throws ParserConfigurationException, SAXException
	{
		// get a parser factory 
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// get a XMLReader 
		parser = spf.newSAXParser();
	}
	
	public void parse(InputSource is, Params params) throws SAXException, IOException, ParamError {
		// parse an XML tree file
		//InputSource is = new InputSource(sourceURI);
		XMLTreeFileHandler xml_handler = new XMLTreeFileHandler(params);
		parser.parse(is,xml_handler);
		if (xml_handler.errors != "") {
			throw new ParamError(xml_handler.errors);
		}
	}
}

/**
 * Holds the tree parameters and related methods.
 * (The params for the levels are in LevelParams, not here!)
 *
 */

public class Params {
	
	
	// Tree Shapes 
	public final static int CONICAL = 0;
	public final static int SPHERICAL = 1;
	public final static int HEMISPHERICAL = 2;
	public final static int CYLINDRICAL = 3;
	public final static int TAPERED_CYLINDRICAL = 4;
	public final static int FLAME = 5;
	public final static int INVERSE_CONICAL = 6;
	public final static int TEND_FLAME = 7;
	public final static int ENVELOPE = 8;
	
	public double leavesErrorValue;
	
	public LevelParams [] levelParams;
	public Random random;
	Hashtable paramDB;
	
	// debugging etc.
	public boolean debug=false;
	public boolean verbose=false;
	public boolean preview=false;
	public boolean ignoreVParams;
	public int stopLevel;
	
	// general params
	public String Species;

	public double LeafQuality;
    public String WoodType;
	
	// this mesh parameters are influenced by Smooth, 
	// this are only defaults here
	public double Smooth;
	public double mesh_quality;  // 0..1 - factor for mesh point number 
	// (1+mesh_quality)
	public int smooth_mesh_level; // -1..Levels - add average normals 
	// to mesh points of all levels below
	
	
	// the seed
	public int Seed;
	
	// defauls values for tree params
	public int Levels;
	
	// trunk&radius parameters
	public double Ratio;
	public double RatioPower;
	public int Shape;
	public double BaseSize;
	public double Flare;
	
	public int Lobes;
	public double LobeDepth;
	
	// leave parameters
	public int Leaves;
	public String LeafShape;
	public double LeafScale;
	public double LeafScaleX;
	
	// new introduced - not in the paper
	public double LeafStemLen;
	public double LeafBend;
	public int LeafDistrib;
	
	// tree scale
	public double Scale;
	public double ScaleV;
	
	// additional trunk scaling
	public double _0Scale; // only 0SCale used
	public double _0ScaleV; // only 0ScaleV used
	
	// attraction and pruning/envelope
	public double AttractionUp;
	public double PruneRatio;
	public double PrunePowerLow;
	public double PrunePowerHigh;
	public double PruneWidth;
	public double PruneWidthPeak;
	
	// base splits
	public int _0BaseSplits;
	
	// variables need for stem creation
	public double scale_tree = 0;
	
	// change events
	protected ChangeEvent changeEvent = null;
	protected EventListenerList listenerList = new EventListenerList();
	
	
	public Params() {
		
		debug = false;
		verbose = true;
		ignoreVParams = false;
		
		stopLevel = -1;
		
		Species = "default";
        WoodType = "Oak";
		
		LeafQuality = 1;
		
		Smooth = 0.5;
		
		// the default seed
		Seed = 13;
		
		// create paramDB
		paramDB = new Hashtable();
		levelParams = new LevelParams[4];
		for (int l=0; l<4; l++) {
			levelParams[l] = new LevelParams(l,paramDB);
		}
		registerParams();
	};
	
	public Params(Params other) {
		
		// copy values from other
		debug = other.debug;
		verbose = other.verbose;
		ignoreVParams = other.ignoreVParams;
		stopLevel = other.stopLevel;
		Species = other.Species;
        WoodType = other.WoodType;
		Seed = other.Seed;
		Smooth = other.Smooth;
		
		// create paramDB
		paramDB = new Hashtable();
		levelParams = new LevelParams[4];
		for (int l=0; l<4; l++) {
			levelParams[l] = new LevelParams(l,paramDB);
		}
		registerParams();
		
		// copy param values
		for (Enumeration e = paramDB.elements(); e.hasMoreElements();) {
			AbstractParam p = ((AbstractParam)e.nextElement());
			try {
				AbstractParam otherParam = other.getParam(p.name);
				if (! otherParam.empty()) {
					p.setValue(otherParam.getValue());
				} // else use default value
			} catch (ParamError err) {
				System.err.println("Error copying params: "+err.getMessage());
			}
		}
	}
	
	public void setSpecies(String sp) {
		Species = sp;
		fireStateChanged();
	}
	
	public String getSpecies() {
		return Species;
	}
	
	// help methods for output of params
	private void writeParamXML(PrintWriter w, String name, int value) {
		w.println("    <param name='" + name + "' value='"+value+"'/>");
	}
	
	private void writeParamXML(PrintWriter w, String name, double value) {
		w.println("    <param name='" + name + "' value='"+value+"'/>");
	}
	
	private void writeParamXML(PrintWriter w, String name, String value) {
		w.println("    <param name='" + name + "' value='"+value+"'/>");
	}
	
	public void toXML(PrintWriter w) throws ParamError {
		prepare(); // read parameters from paramDB
		w.println("<?xml version='1.0' ?>");
		w.println();
		w.println("<arbaro>");
		w.println("  <species name='" + Species + "'>");
		w.println("    <!-- general params -->");
		// FIXME: maybe use paramDB to print out params
		// thus no one could be forgotten?
        writeParamXML(w,"WoodType",WoodType);
		writeParamXML(w,"Shape",Shape);
		writeParamXML(w,"Levels",Levels);
		writeParamXML(w,"Scale",Scale);
		writeParamXML(w,"ScaleV",ScaleV);
		writeParamXML(w,"BaseSize",BaseSize);
		writeParamXML(w,"Ratio",Ratio);
		writeParamXML(w,"RatioPower",RatioPower);
		writeParamXML(w,"Flare",Flare);
		writeParamXML(w,"Lobes",Lobes);
		writeParamXML(w,"LobeDepth",LobeDepth);
		writeParamXML(w,"Smooth",Smooth);
		writeParamXML(w,"Leaves",Leaves);
		writeParamXML(w,"LeafShape",LeafShape);
		writeParamXML(w,"LeafScale",LeafScale);
		writeParamXML(w,"LeafScaleX",LeafScaleX);
		writeParamXML(w,"LeafQuality",LeafQuality);
		writeParamXML(w,"LeafStemLen",LeafStemLen);
		writeParamXML(w,"LeafDistrib",LeafDistrib);
		writeParamXML(w,"LeafBend",LeafBend);
		writeParamXML(w,"AttractionUp",AttractionUp);
		writeParamXML(w,"PruneRatio",PruneRatio);
		writeParamXML(w,"PrunePowerLow",PrunePowerLow);
		writeParamXML(w,"PrunePowerHigh",PrunePowerHigh);
		writeParamXML(w,"PruneWidth",PruneWidth);
		writeParamXML(w,"PruneWidthPeak",PruneWidthPeak);
		writeParamXML(w,"0Scale",_0Scale); 
		writeParamXML(w,"0ScaleV",_0ScaleV);
		writeParamXML(w,"0BaseSplits",_0BaseSplits);
		
		for (int i=0; i <= Math.min(Levels,3); i++) {
			levelParams[i].toXML(w,i==Levels); // i==Levels => leaf level only
		}
		w.println("  </species>");
		w.println("</arbaro>");
		w.flush();
	}
	
	public void clearParams() {
		for (Enumeration e = paramDB.elements(); e.hasMoreElements();) {
			((AbstractParam)e.nextElement()).clear();
		}
	}
	
	// help method for loading params
	private int getIntParam(String name) throws ParamError {
		IntParam par = (IntParam)paramDB.get(name);
		if (par != null) {
			return par.intValue();
		} else {
			throw new ParamError("bug: param "+name+" not found!");
		}
	}
	
	private double getDblParam(String name) throws ParamError {
		FloatParam par = (FloatParam)paramDB.get(name);
		if (par != null) {
			return par.doubleValue();
		} else {
			throw new ParamError("bug: param "+name+" not found!");
		}   
	}
	
	private String getStrParam(String name) throws ParamError {
		StringParam par = (StringParam)paramDB.get(name);
		if (par != null) {
			return par.getValue();
		} else {
			throw new ParamError("bug: param "+name+" not found!");
		}    
	}
	
	void fromDB() throws ParamError {
		LeafQuality = getDblParam("LeafQuality");
		Smooth = getDblParam("Smooth");
		Levels = getIntParam("Levels");
		Ratio = getDblParam("Ratio");
		RatioPower = getDblParam("RatioPower");
		Shape = getIntParam("Shape");
		BaseSize = getDblParam("BaseSize");
		Flare = getDblParam("Flare");
		Lobes = getIntParam("Lobes");
		LobeDepth = getDblParam("LobeDepth");
		Leaves = Leaves != -1 ? getIntParam("Leaves") : 0; // Don't render leaves if explicitly turning them off
		LeafShape = getStrParam("LeafShape");
		LeafScale = getDblParam("LeafScale");
		LeafScaleX = getDblParam("LeafScaleX");
		LeafStemLen = getDblParam("LeafStemLen");
		LeafDistrib = getIntParam("LeafDistrib");
		LeafBend = getDblParam("LeafBend");
		Scale = getDblParam("Scale");
		ScaleV = getDblParam("ScaleV");
		_0Scale = getDblParam("0Scale"); 
		_0ScaleV = getDblParam("0ScaleV");
		AttractionUp = getDblParam("AttractionUp");
		PruneRatio = getDblParam("PruneRatio");
		PrunePowerLow = getDblParam("PrunePowerLow");
		PrunePowerHigh = getDblParam("PrunePowerHigh");
		PruneWidth = getDblParam("PruneWidth");
		PruneWidthPeak = getDblParam("PruneWidthPeak");
		_0BaseSplits = getIntParam("0BaseSplits");
		Species = getStrParam("Species");
        WoodType = getStrParam("WoodType");
//		Seed = getIntParam("Seed");
//		outputType = getIntParam("OutFormat");
		
		for (int i=0; i<=Math.min(Levels,3); i++) {
			levelParams[i].fromDB(i==Levels); // i==Levels => leaf level only
		}
	}
	
	public void prepare() throws ParamError {
		if (debug) { verbose=false; }
		
		// read in parameter values from ParamDB
		fromDB();
		
		if (ignoreVParams) {
			ScaleV=0;
			for (int i=1; i<4; i++) {
				LevelParams lp = levelParams[i];
				lp.nCurveV = 0;
				lp.nLengthV = 0;
				lp.nSplitAngleV = 0;
				lp.nRotateV = 0;
				// lp.nBranchDistV = 0;
				if (lp.nDownAngle>0) { lp.nDownAngle=0; }
			}
		}
		
		// additional params checks
		for (int l=0; l < Math.min(Levels,4); l++) {
			LevelParams lp = levelParams[l];
			if (lp.nSegSplits>0 && lp.nSplitAngle==0) {
				throw new ParamError("nSplitAngle may not be 0.");
			}
		}
		
		// create one random generator for every level
		// so you can develop a tree level by level without
		// influences between the levels
		long l = levelParams[0].initRandom(Seed);
		for (int i=1; i<4; i++) {
			l = levelParams[i].initRandom(l);
		}
		
		// create a random generator for myself (used in stem_radius)
		random = new Random(Seed);
		
		// mesh settings
		if (Smooth <= 0.2) {
			smooth_mesh_level = -1;
		} else {
			smooth_mesh_level = (int)(Levels*Smooth);
		}
		mesh_quality = Smooth;
		
		// mesh points per cross-section for the levels
		// minima
		levelParams[0].mesh_points = 4;
		levelParams[1].mesh_points = 3;
		levelParams[2].mesh_points = 2;
		levelParams[3].mesh_points = 1;
		// set meshpoints with respect to mesh_quality and Lobes
		if (Lobes>0) {
			levelParams[0].mesh_points = (int)(Lobes*(Math.pow(2,(int)(1+2.5*mesh_quality))));
			levelParams[0].mesh_points = 
				Math.max(levelParams[0].mesh_points,(int)(4*(1+2*mesh_quality)));
		}
		for (int i=1; i<4; i++) {
			levelParams[i].mesh_points = 
				Math.max(3,(int)(levelParams[i].mesh_points*(1+1.5*mesh_quality)));
		}
		
		// stop generation at some level?
		if (stopLevel>=0 && stopLevel<=Levels) {
			Levels = stopLevel;
			Leaves = 0;
		}

        if (scale_tree == 0) {
		    scale_tree = Scale + levelParams[0].random.uniform(-ScaleV,ScaleV);
        }
	}
	
	public double getShapeRatio(double ratio) {
		return getShapeRatio(ratio,Shape);
	}
	
	public double getShapeRatio(double ratio, int shape) {
		
		switch (shape) { 
		//case CONICAL: return 0.2+0.8*ratio;
		// need real conical shape for lark, fir, etc.
		case CONICAL: return ratio; // FIXME: this would be better: 0.05+0.95*ratio; ?
		case SPHERICAL: return 0.2+0.8*Math.sin(Math.PI*ratio);
		case HEMISPHERICAL: return 0.2+0.8*Math.sin(0.5*Math.PI*ratio);
		case CYLINDRICAL: return 1.0;
		case TAPERED_CYLINDRICAL: return 0.5+0.5*ratio;
		case FLAME: 
			return ratio<=0.7? 
					ratio/0.7 : 
						(1-ratio)/0.3;
		case INVERSE_CONICAL: return 1-0.8*ratio;
		case TEND_FLAME: 
			return ratio<=0.7? 
					0.5+0.5*ratio/0.7 :
						0.5+0.5*(1-ratio)/0.3;
		case ENVELOPE:
			if (ratio<0 || ratio>1) {
				return 0;
			} else if (ratio<(1-PruneWidthPeak)) {
				return Math.pow(ratio/(1-PruneWidthPeak),PrunePowerHigh);
			} else {
				return Math.pow((1-ratio)/(1-PruneWidthPeak),PrunePowerLow);
			}
			// tested in prepare() default: throw new ErrorParam("Shape must be between 0 and 8");
		}
		return 0; // shouldn't reach here
	}
	
	public void setParam(String name, String value) throws ParamError {
		AbstractParam p = (AbstractParam)paramDB.get(name);
		if (p!=null) {
			p.setValue(value);
			if (debug) {
				System.err.println("Params.setParam(): set "+name+" to "+value);
			}
			
		} else {
			throw new ParamError("Unknown parameter "+name+"!");
		}
	}
	
	public TreeMap getParamGroup(int level, String group) {
		TreeMap result = new TreeMap();
		for (Enumeration e = paramDB.elements(); e.hasMoreElements();) {
			AbstractParam p = (AbstractParam)e.nextElement();
			if (p.getLevel() == level && p.getGroup().equals(group)) {
				result.put(new Integer(p.getOrder()),p);
			}
		}
		return result;
	}
	
	// help methods for createing param-db
	
	int order;
	private void intParam(String name, int min, int max, int deflt,
			String group, String short_desc, String long_desc) {
		paramDB.put(name,new IntParam(name,min,max,deflt,group,AbstractParam.GENERAL,
				order++,short_desc,long_desc));
	}
	
	private void shapeParam(String name, int min, int max, int deflt,
			String group, String short_desc, String long_desc) {
		paramDB.put(name,new ShapeParam(name,min,max,deflt,group,AbstractParam.GENERAL,
				order++,short_desc,long_desc));
	}	
	
	private void int4Param(String name, int min, int max, 
			int deflt0,int deflt1, int deflt2, int deflt3,
			String group, String short_desc, String long_desc) {
		int [] deflt = {deflt0,deflt1,deflt2,deflt3};
		order++;
		for (int i=0; i<4; i++) {
			name = "" + i + name.substring(1);
			paramDB.put(name,new IntParam(name,min,max,deflt[i],group,i,
					order,short_desc,long_desc));
		}
	}
	
	private void dblParam(String name, double min, double max, double deflt,
			String group, String short_desc, String long_desc) {
		paramDB.put(name,new FloatParam(name,min,max,deflt,group,AbstractParam.GENERAL,
				order++,short_desc,long_desc));
	}
	
	private void dbl4Param(String name, double min, double max, 
			double deflt0, double deflt1, double deflt2, double deflt3,
			String group, String short_desc, String long_desc) {
		double [] deflt = {deflt0,deflt1,deflt2,deflt3};
		order++;
		for (int i=0; i<4; i++) {
			name = "" + i + name.substring(1);
			paramDB.put(name,new FloatParam(name,min,max,deflt[i],group,i,
					order,short_desc,long_desc));
		}
	}
	
	private void lshParam(String name, String deflt,
			String group, String short_desc, String long_desc) {
		paramDB.put(name,new LeafShapeParam(name,deflt,group,AbstractParam.GENERAL,
				order++,short_desc,long_desc));
	}

    private void woodTypeParam(String name, String deflt,
            String group, String short_desc, String long_desc) {
        paramDB.put(name,new WoodTypeParam(name,deflt,group,AbstractParam.GENERAL,
                order++,short_desc,long_desc));
    }
	
	private void strParam(String name, String deflt,
			String group, String short_desc, String long_desc) {
		paramDB.put(name,new StringParam(name,deflt,group,AbstractParam.GENERAL,
				order++,short_desc,long_desc));
	}
	
	private void registerParams() {
		order=1;
		
		strParam("Species","default",
				"SHAPE","the tree's species",
				"<strong>Species</strong> is the kind of tree.<br>\n"+
				"It is used for declarations in the output file.<br>\n");

        woodTypeParam("WoodType", "Oak", "SHAPE", "the tree's wood texture",
                "<strong>WoodType</strong> is the name of the Minecraft wood texture to use when rendering the tree.");

		shapeParam ("Shape",0,8,0,"SHAPE","general tree shape id",
				"The <strong>Shape</strong> can be one of:<ul>\n"+
				"<li>0 - conical</li>\n"+
				"<li>1 - spherical</li>\n"+
				"<li>2 - hemispherical</li>\n"+
				"<li>3 - cylindrical</li>\n"+
				"<li>4 - tapered cylindrical</li>\n"+
				"<li>5 - flame</li>\n"+
				"<li>6 - inverse conical</li>\n"+
				"<li>7 - tend flame</li>\n"+
				"<li>8 - envelope - uses pruning envelope<br>\n"+
				"(see PruneWidth, PruneWidthPeak, PrunePowerLow, PrunePowerHigh)</li></ul>\n"
		);
		
		intParam("Levels",0,9,3,"SHAPE","levels of recursion",
				"<strong>Levels</strong> are the levels of recursion when creating the\n"+
				"stems of the tree.<ul>\n" +
				"<li>Levels=1 means the tree consist only of the (may be splitting) trunk</li>\n"+
				"<li>Levels=2 the tree consist of the trunk with one level of branches</li>\n"+
				"<li>Levels>4 seldom necessary, the parameters of the forth level are used\n"+
				"for all higher levels.</li></ul>\n"+
				"Leaves are considered to be one level above the last stem level.<br>\n"+
				"and uses it's down and rotation angles.\n"
		);
		
		dblParam("Scale",0.000001,Double.POSITIVE_INFINITY,50.0,"SHAPE","average tree size in meters",
				"<strong>Scale</strong> is the average tree size in meters.<br>\n"+
				"With Scale = 10.0 and ScaleV = 2.0 trees of this species\n"+
				"reach from 8.0 to 12.0 meters.<br>\n"+
				"Note, that the trunk length can be different from the tree size.\n"+
				"(See 0Length and 0LengthV)\n"
		);
		
		dblParam("ScaleV",0.0,Double.POSITIVE_INFINITY,0.0,"SHAPE","variation of tree size in meters",
				"<strong>ScaleV</strong> is the variation range of the tree size in meters.<br>\n"+
				"Scale = 10.0, ScaleV = 2.0 means trees of this species\n"+
				"reach from 8.0 to 12.0 meters.\n"+
				"(See Scale)\n"
		);
		
		dblParam ("BaseSize",0.0,1.0,0.25,"SHAPE","fractional branchless area at tree base",
				"<strong>BaseSize</strong> is the fractional branchless part of the trunk. E.g.\n<ul>"+
				"<li>BaseSize=&nbsp;&nbsp;0</code> means branches begin on the bottom of the tree,</li>\n"+
				"<li>BaseSize=0.5</code> means half of the trunk is branchless,</li>\n"+
				"<li>BaseSize=1.0</code> branches grow out from the peak of the trunk only.</li></ul>\n"
		);
		
		intParam("0BaseSplits",0,Integer.MAX_VALUE,0,"SHAPE",
				"stem splits at base of trunk",
				"<strong>BaseSplits</strong> are the stem splits at the top of the first trunk segment.<br>\n"+
				"So with BaseSplits=2 you get a trunk splitting into three parts. Other then<br>\n"+
				"with 0SegSplits the clones are evenly distributed over<br>\n"+
				"the 360&deg;. So, if you want to use splitting, you should<br>\n"+
				"use BaseSplits for the first splitting to get a circular<br>\n"+
				"stem distribution (seen from top).<br>\n"
		);
		
//		dblParam("ZScale",0.000001,Double.POSITIVE_INFINITY,1.0,"SHAPE",
//				"additional Z-scaling (not used)<br>",
//				"<strong>ZScale</strong> and ZScaleV are not described in the Weber/Penn paper.<br>\n"+
//				"so theire meaning is unclear and they aren't used at the moment\n"
//		);
//		
//		dblParam("ZScaleV",0.0,Double.POSITIVE_INFINITY,0.0,"SHAPE",
//				"additional Z-scaling variation (not used)<br>",
//				"ZScale and <strong>ZScaleV</strong> are not described in the Weber/Penn paper.<br>\n"+
//				"so theire meaning is unclear and they aren't used at the moment\n"
//		);
		
		dblParam("Ratio",0.000001,Double.POSITIVE_INFINITY,0.05,"TRUNK",
				"trunk radius/length ratio",
				"<strong>Ratio</strong> is the radius/length ratio of the trunk.<br>\n"+
				"Ratio=0.05 means the trunk is 1/20 as thick as it is long,<br>\n"+
				"t.e. a 10m long trunk has a base radius of 50cm.<br>\n"//+
//				"Note, that the real base radius could be greater, when Flare<br>\n"+
//				"and/or Lobes are used. (See Flare, Lobes, LobesDepth, RatioPower)\n"
		);
		
		dblParam("RatioPower",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,1.0,
				"SHAPE","radius reduction",
				"<strong>RatioPower</strong> is a reduction value for the radius of the\n"+
				"substems.\n<ul>"+
				"<li>RatioPower=1.0  means the radius decreases linearly with\n"+
				"decreasing stem length</li>\n"+
				"<li>RatioPower=2.0  means it decreases with the second power</li>\n"+
				"<li>RatioPower=0.0  means radius is the same as parent radius\n"+
				"(t.e. it doesn't depend of the length)</li>\n"+
				"<li>RatioPower=-1.0 means the shorter the stem the thicker it is\n"+
				"(radius = parent radius * 1 / length)</li></ul>\n"+
				"Note, that the radius of a stem cannot be greater then the parent radius at the stem offset.<br>\n"+
				"So with negative RatioPower you cannot create stems thicker than it's parent.<br>\n"+
				"Instead you can use it to make stems thinner, which are longer than it's parent.<br>\n"+
				"(See Ratio)\n"
		);
		
		dblParam("Flare",-1.0,Double.POSITIVE_INFINITY,0.5,
				"TRUNK","exponential expansion at base of tree",
				"<strong>Flare</strong> makes the trunk base thicker.<ul>\n"+
				"<li>Flare = 0.0 means base radius is used at trunk base</li>\n"+
				"<li>Flare = 1.0 means trunk base is twice as thick as it's base radius\n"+
				"(See Ratio)</li></ul>\n"//+
//				"Note, that using Lobes make the trunk base thicker too.\n"+
//				"(See Lobes, LobeDepth)\n"
		);
		
		intParam("Lobes",0,Integer.MAX_VALUE,0,"UNUSED",
				"sinusoidal cross-section variation",
				"With <strong>Lobes</strong> you define how much lobes (this are variations in it's<br>\n"+
				"cross-section) the trunk will have. This isn't supported for<br>\n"+
				"cones output, but for mesh only.<br>\n"+
				"(See LobeDepth too)\n"
		);
		
		dblParam("LobeDepth",0,Double.POSITIVE_INFINITY,0,
				"UNUSED","amplitude of cross-section variation",
				"<strong>LobeDepth</strong> defines, how deep the lobes of the trunk will be.<br>\n"+
				"This is the amplitude of the sinusoidal cross-section variations.<br>\n"+
				"(See Lobes)\n"
		);
		
		intParam("Leaves",Integer.MIN_VALUE,Integer.MAX_VALUE,0,
				"LEAVES","number of leaves per stem",
				"<strong>Leaves</strong> gives the maximal number of leaves per stem.<br>\n"+
				"Leaves grow only from stems of the last level. The actual number of leaves on a stem,<br>\n"+
				"depending on the stem offset and length, can be smaller than Leaves.<br>\n"+
				"When Leaves is negative, the leaves grow in a fan at\n"+
				"the end of the stem.\n"
		);
		
		lshParam("LeafShape","0","UNUSED","leaf shape id",
				"<strong>LeafShape</strong> is the shape of the leaf (\"0\" means oval shape).<br>\n"+
				"The length and width of the leaf are given by LeafScale and LeafScaleX.<br>\n"+

				"When creating a mesh at the moment you can use the following values:<ul>\n"+
				"<li>\"disc\" - a surface consisting of 6 triangles approximating an oval shape</li>\n"+
				"<li>\"sphere\" - an ikosaeder approximating a shperical shape,<br>\n"+
				"useful for making knots or seeds instead of leaves, or for high quality needles</li>\n"+
				"<li>\"disc1\", \"disc2\", ... - a surface consisting of 1, 2, ... triangles approximating an oval shape<br>\n"+
				"lower values are useful for low quality needles or leaves, to reduce mesh size,<br>\n"+
				"values between 6 and 10 are quite good for big, round leaves.</li>\n"+
				"<li>any other - same like disc</li></ul>\n"+

				"When using primitives output, the possible values of LeafShape references<br>\n"+
				"the declarations in arbaro.inc. At the moment there are:<ul>\n"+
				"<li>\"disc\" the standard oval form of a leaf, defined<br>\n"+
				"as a unit circle of radius 0.5m. The real<br>\n"+
				"length and width are given by the LeafScale parameters.</li>\n"+
				"<li>\"sphere\" a spherical form, you can use to<br>\n"+
				"simulate seeds on herbs or knots on branches like in the<br>\n"+
				"desert bush. You can use the sphere shape for needles too,<br>\n"+
				"thus they are visible from all sides</li>\n"+
				"<li>\"palm\" a palm leaf, this are two disc halfs put together<br>\n"+
				"with an angle between them. So they are visible<br>\n"+
				"also from the side and the light effects are<br>\n"+
				"more typically, especialy for fan palms seen from small distances.</li>\n"+
				"<li>any other - add your own leaf shape to the file arbaro.inc</li></ul>\n"
		);
		
		dblParam("LeafScale",0.000001,Double.POSITIVE_INFINITY,8,
				"LEAVES","leaf length/width",
				"<strong>LeafScale</strong> is the length of the leaf in meters.<br>\n"+
				"The unit leaf is scaled in x/z-direction \n"
		);
		
		dblParam("LeafScaleX",0.000001,Double.POSITIVE_INFINITY,0.75,"LEAVES",
				"fractional leaf height",
				"<strong>LeafScaleX</strong> is the fractional height of the leaf relative to it's length/width. So<ul>\n"+
				"<li>LeafScaleX=0.5 means the leaf is half as tall as long like an ellipsoid</li>\n"+
				"<li>LeafScaleX=1.0 means the leaf is like a sphere</li></ul>\n"
		);
		
		dblParam("LeafBend",0,1,0.3,"UNUSED","leaf orientation toward light",
				"With <strong>LeafBend</strong> you can influence, how much leaves are oriented<br>\n"+
				"outside and upwards.<br>Values near 0.5 are good. For low values the leaves<br>\n"+
				"are oriented to the stem, for high value to the light.<br>\n"+
				"For trees with long leaves like palms you should use lower values.\n"
		);
		
		dblParam("LeafStemLen",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0,
				"LEAVES","fractional leaf stem length",
				"<strong>LeafStemLen</strong is the length of the (virtual) leaf stem.<br>\n"+
				"It's not drawn, so this is the distance between the stem<br>\n"+
				"axis and the leaf. For normal trees with many nearly circular<br>\n"+
				"leaves the default value of 0.5 (meaning the stem has half of the length<br>\n"+
				"of the leaf) is quite good. For other trees like palms with long leaves<br>\n"+
				"or some herbs you need a LeafStemLen near 0. Negative stem length is<br>\n"+
				"allowed for special cases."
		);
		
		intParam ("LeafDistrib",0,8,4,"LEAVES","leaf distribution",
				"<strong>LeafDistrib</strong> determines how leaves are distributed over<br>\n"+
				"the branches of the last but one stem level. It takes the same<br>\n"+
				"values like Shape, meaning 3 = even distribution, 0 = most leaves<br>\n"+
				"outside. Default is 4 (some inside, more outside)."
		);
		
		dblParam("LeafQuality",0.000001,1.0,1.0,"UNUSED","leaf quality/leaf count reduction",
				"With a <strong>LeafQuality</strong> less then 1.0 you can reduce the number of leaves<br>\n"+
				"to improve rendering speed and memory usage. The leaves are scaled<br>\n"+
				"with the same amount to get the same coverage.<br>\n"+
				"For trees in the background of the scene you will use a reduced<br>\n"+
				"LeafQuality around 0.9. Very small values would cause strange results.<br>\n"+
				"(See LeafScale)"
		);
		
		dblParam("Smooth",0.0,1.0,0.02,"QUALITY","smooth value for mesh creation",
				"Higher <strong>Smooth</strong> values creates trees with more noise added<br>\n"+
				"to the trunk and leaves. Larger trees should use a higher value to<br>\n"+
                "achieve a more natural trunk texture.\n"
		);
		
		dblParam("AttractionUp",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0.0,
				"SHAPE","upward/downward growth tendency",
				"<strong>AttractionUp</strong> is the tendency of stems with level>=2 to grow upwards<br>\n"+
				"(downwards for negative values).<br>\n"+
				"A value of 1.0 for a horizontal stem means the last segment should point upwards.<br>\n"+
				"Greater values means earlier reaching of upward direction. Values of 10 and greater<br>\n"+
				"could cause overcorrection resulting in a snaking oscillation.<br>\n"+
				"As an example see the weeping willow, which has a negative AttractionUp value.\n"
		);
		
		dblParam("PruneRatio",0.0,1.0,0.0,"PRUNING",
				"fractional effect of pruning",
				"A <strong>PruneRatio</strong> of 1.0 means all branches are inside<br>\n"+
				"the envelope. 0.0 means no pruning.\n"
		);
		
		dblParam("PruneWidth",0.0,1.0,0.5,"PRUNING","width of envelope peak",
				"<strong>PruneWidth</strong> is the fractional width of the pruning envelope at the<br>\n"+
				"peak. A value of 0.5 means the tree is half as wide as high.<br>\n"+
				"This parameter is used for the shape \"envelope\" too, even if PruneRatio is off.\n"
		);
		
		dblParam("PruneWidthPeak",0.0,1.0,0.5,"PRUNING","position of envelope peak",
				"<strong>PruneWidthPeak</strong> is the fractional height of the envelope peak.<br>\n"+
				"A value of 0.5 means upper part and lower part of the envelope have the same height.<br>\n"+
				"This parameter is used for the shape \"envelope\" too, even if PruneRatio is off.\n"
		);
		
		dblParam("PrunePowerLow",0.0,Double.POSITIVE_INFINITY,0.5,"PRUNING",
				"curvature of envelope",
				"<strong>PrunePowerLow</strong> describes the envelope curve below the peak.<br>\n"+
				"A value of 1 means linear decreasing. Higher values means concave,<br>\n"+
				"lower values convex curve.<br>\n"+
				"This parameter is used for the shape \"envelope\" too, even if PruneRatio is off.\n"
		);
		
		dblParam("PrunePowerHigh",0.0,Double.POSITIVE_INFINITY,0.5,"PRUNING",
				"curvature of envelope",
				"<strong>PrunePowerHigh</strong> describes the envelope curve above the peak.<br>\n"+
				"A value of 1 means linear decreasing. Higher values means concave,<br>\n"+
				"lower values convex curve.<br>\n"+
				"This parameter is used for the shape \"envelope\" too, even if PruneRatio is off.\n"
		);
		
		dblParam("0Scale",0.000001,Double.POSITIVE_INFINITY,1.0,
				"TRUNK","extra trunk scaling",
				"<strong>0Scale</strong> and 0ScaleV makes the trunk thicker.<br>\n"+
				"This parameters exists for the level 0 only. From the Weber/Penn paper it is<br>\n"+
				"not clear, why there are two trunk scaling parameters<br> \n"+
				"0Scale and Ratio. See Ratio, 0ScaleV, Scale, ScaleV.<br>\n"+
				"In this implementation 0Scale does not influence the trunk base radius<br>\n"+
				"but is applied finally to the stem radius formular. Thus the<br>\n"+
				"trunk radius could be influenced independently from the<br>\n"+
				"Ratio/RatioPower parameters and the periodic tapering (0Taper > 2.0)<br>\n"+
				"could be scaled, so that the sections are elongated spheres.\n"
		);
		
		dblParam("0ScaleV",0.0,Double.POSITIVE_INFINITY,0.0,"TRUNK",
				"variation for extra trunk scaling",
				"0Scale and <strong>0ScaleV</strong> makes the trunk thicker. This parameters<br>\n"+
				"exists for the level 0 only. From the Weber/Penn paper it is<br>\n"+
				"not clear, why there are two trunk scaling parameters<br>\n"+
				"0Scale and Ratio. See Ratio, 0ScaleV, Scale, ScaleV.<br>\n"+
				"In this implementation 0ScaleV is used to perturb the<br>\n"+
				"mesh of the trunk. But use with care, because the mesh<br>\n"+
				"could got fissures when using too big values.<br>\n"
		);
		
		dbl4Param("nLength",0.0000001,Double.POSITIVE_INFINITY,1.0,0.5,0.5,0.5,
				"LENTAPER","fractional trunk scaling",
				"<strong>0Length</strong> and 0LengthV give the fractional length of the<br>\n"+
				"trunk. So with Scale=10 and 0Length=0.8 the length of the<br>\n"+
				"trunk will be 8m. Dont' confuse the height of the tree with<br>\n"+
				"the length of the trunk here.<br><br>\n"+
				"<strong>nLength</strong> and nLengthV define the fractional length of a stem<br>\n"+
				"relating to the length of theire parent.<br>\n"
		);
		
		dbl4Param("nLengthV",0.0,Double.POSITIVE_INFINITY,0.0,0.0,0.0,0.0,
				"LENTAPER","variation of fractional trunk scaling",
				"<strong>nLengthV</strong> is the variation of the length given by nLength.<br>\n"
		);
		
		dbl4Param("nTaper",0.0,2.99999999,1.0,1.0,1.0,1.0,
				"LENTAPER","cross-section scaling",
				"<strong>nTaper</strong> is the tapering of the stem along its length.<ul>\n"+
				"<li>0 - non-tapering cylinder</li>\n"+
				"<li>1 - taper to a point (cone)</li>\n"+
				"<li>2 - taper to a spherical end</li>\n"+
				"<li>3 - periodic tapering (concatenated spheres)</li></ul>\n"+
				"You can use fractional values, to get intermediate results.<br>\n"
		);
		
		dbl4Param("nSegSplits",0,Double.POSITIVE_INFINITY,0,0,0,0,
				"SPLITTING","stem splits per segment",
				"<strong>nSegSplits</strong> determines how much splits per segment occures.<br><br>\n"+
				"Normally you would use a value between 0.0 and 1.0. A value of<br>\n"+
				"0.5 means a split at every second segment. If you use splitting<br>\n"+
				"for the trunk you should use 0BaseSplits for the first split, <br>\n"+
				"otherwise the tree will tend to one side."
		);
		
		dbl4Param("nSplitAngle",0,180,0,0,0,0,"SPLITTING",
				"splitting angle",
				"<strong>nSplitAngle</strong> is the vertical splitting angle. A horizontal diverging<br>\n"+
				"angle will be added too, but this one you cannot influence with parameters.<br>\n"+
				"The declination of the splitting branches won't exceed the splitting angle.<br>\n"
		);
		
		dbl4Param("nSplitAngleV",0,180,0,0,0,0,"SPLITTING",
				"splitting angle variation",
				"<strong>nSplitAngleV</strong> is the variation of the splitting angle. See nSplitAngle.<br>\n"
		);
		
		int4Param("nCurveRes",1,Integer.MAX_VALUE,3,3,1,1,
				"CURVATURE","curvature resolution",
				"<strong>nCurveRes</strong> determines how many segments the branches consist of.<br><br>\n"+
				"Normally you will use higher values for the first levels, and low<br>\n"+
				"values for the higher levels.<br>\n"
		);
		
		dbl4Param("nCurve",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0,0,0,0,
				"CURVATURE","curving angle",
				"<strong>nCurve</strong> is the angle the branches are declined over theire whole length.<br>\n"+
				"If nCurveBack is used, the curving angle is distributed only over the<br>\n"+
				"first half of the stem.<br>\n"
		);
		
		dbl4Param("nCurveV",-90,Double.POSITIVE_INFINITY,0,0,0,0,
				"CURVATURE","curving angle variation",
				"<strong>nCurveV</strong> is the variation of the curving angle. See nCurve, nCurveBack.<br>\n"+
				"A negative value means helical curvature<br>\n"
		);
		
		dbl4Param("nCurveBack",Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,0,0,0,0,
				"CURVATURE","curving angle upper stem half",
				"Using <strong>nCurveBack</strong> you can give the stem an S-like shape.<br>\n"+
				"The first half of the stem the nCurve value is applied.<br>\n"+
				"The second half the nCurveBack value.<br><br>\n"+
				"It's also possible to give both parametera the same sign to<br>\n"+
				"get different curving over the stem length, instead of a S-shape<br>\n"
		);
		
		dbl4Param("nDownAngle",-179.9999999,179.999999,0,30,30,30,
				"BRANCHING","angle from parent",
				"<strong>nDownAngle</strong> is the angle between a stem and it's parent.<br>\n"
		);
		
		dbl4Param("nDownAngleV",-179.9999999,179.9999999,0,0,0,0,
				"BRANCHING","down angle variation",
				"<strong>nDownAngleV</strong> is the variation of the downangle. See nDownAngle.<br>\n"+
				"Using a negative value, the nDownAngleV is variated over the<br>\n"+
				"length of the stem, so that the lower branches have a bigger<br>\n"+
				"downangle then the higher branches.<br>\n"
		);
		
		dbl4Param("nRotate",-360,360,0,120,120,120,
				"BRANCHING","spiraling angle",
				"<strong>nRotate</strong> is the angle, the branches are rotating around the parent<br>\n"+
				"If nRotate is negative the branches are located on alternating<br>\n"+
				"sides of the parent.<br>\n"
		);
		
		dbl4Param("nRotateV",-360,360,0,0,0,0,
				"BRANCHING","spiraling angle variation",
				"<strong>nRotateV</strong> is the variation of nRotate.<br>\n"
		);
		
		int4Param("nBranches",0,Integer.MAX_VALUE,1,10,5,5,
				"BRANCHING","number of branches",
				"<strong>nBranches</strong> is the maximal number of branches on a parent stem.<br>\n"+
				"The number of branches are reduced proportional to the<br>\n"+
				"relative length of theire parent.<br>\n"
		);
		
		dbl4Param("nBranchDist",0,1,0,1,1,1,
				"BRANCHING","branch distribution along the segment",
				"<strong>nBranchDist</strong> is an additional parameter of Arbaro. It influences the<br>\n"+
				"distribution of branches over a segment of the parent stem.<br>\n"+
				"With 1.0 you get evenly distribution of branches like in the<br>\n"+
				"original model. With 0.0 all branches grow from the segments<br>\n"+
				"base like for conifers.<br>\n"
		);
		
		
//		outParam("OutFormat",MESH,CONES,MESH,
//				"RENDER","the output file format",
//				"<strong>OutFormat</strong> defines the format of the outputfile for rendering.<br>\n");
//		
//		intParam("RenderWidth",15,6000,600,
//				"RENDER","the width of the rendered image",
//				"<strong>RenderWidth</strong> is the width of the rendered image,<br>\n"+
//				"if you render a scene with the tree from Arbaro.");
//
//		intParam("RenderHeight",20,8000,800,
//				"RENDER","the height of the rendered image",
//				"<strong>RenderHeight</strong> is the height of the rendered image,<br>\n"+
//				"if you render a scene with the tree from Arbaro.");
//		
//		intParam("Seed",0,Integer.MAX_VALUE,13,
//				"RENDER","the random seed",
//				"<strong>Seed</strong> is the seed for initializing the random generator<br>\n"+
//				"making the tree individual. So you can think of it as the tree's seed too.");

	}
	
	public void readFromCfg(InputStream is) throws Exception {
		CfgTreeParser parser = new CfgTreeParser();
		parser.parse(is,this);
	}
	
	public void readFromXML(InputStream is) throws ParamError {
		try {
			XMLTreeParser parser = new XMLTreeParser();
			parser.parse(new InputSource(is),this);
		} catch (Exception e) {
			throw new ParamError(e.getMessage());
		}
	}
	
	public AbstractParam getParam(String parname) {
		return (AbstractParam)paramDB.get(parname);
	}
	
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}
	
	protected void fireStateChanged() {
		Object [] listeners = listenerList.getListenerList();
		for (int i = listeners.length -2; i>=0; i-=2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
			}
		}
	}
	
	/**
	 * Enables or disables params depending on other
	 * params values. 
	 */
	public void enableDisable() {
		boolean enable;
		
		// ############ general params ##############
		
		// disable Z-Scale parameters (they are not used)
//		getParam("ZScale").setEnabled(false);
//		getParam("ZScaleV").setEnabled(false);
		
		// enable RatioPower/Leaves if Levels>1
		enable = (((IntParam)getParam("Levels")).intValue() > 1);
		getParam("RatioPower").setEnabled(enable);
		getParam("Leaves").setEnabled(enable);
		
		// enable leaf params if Leaves != 0
		enable = (((IntParam)getParam("Leaves")).intValue() != 0 &&
				((IntParam)getParam("Levels")).intValue() > 1);
		getParam("LeafShape").setEnabled(enable);
		getParam("LeafScale").setEnabled(enable);
		getParam("LeafScaleX").setEnabled(enable);
		getParam("LeafBend").setEnabled(enable);
		getParam("LeafDistrib").setEnabled(enable);
		getParam("LeafQuality").setEnabled(enable);
		getParam("LeafStemLen").setEnabled(enable);
		
		// enable Pruning parameters, if PruneRatio>0 or Shape=envelope
		enable = (((IntParam)getParam("Shape")).intValue() == 8 ||
				((FloatParam)getParam("PruneRatio")).doubleValue()>0);
		getParam("PrunePowerHigh").setEnabled(enable);
		getParam("PrunePowerLow").setEnabled(enable);
		getParam("PruneWidth").setEnabled(enable);
		getParam("PruneWidthPeak").setEnabled(enable);
		
		// enable LobeDepth if Lobes>0
		enable = (((IntParam)getParam("Lobes")).intValue() > 0);
		getParam("LobeDepth").setEnabled(enable);
		
		// enable AttractionUp if Levels>2
		enable = (((IntParam)getParam("Levels")).intValue() > 2);
		getParam("AttractionUp").setEnabled(enable);
		
		// ############## disable unused levels ###########
		
		for (int i=0; i<4; i++) {
			
			enable = i<((IntParam)getParam("Levels")).intValue();
			
			getParam(""+i+"Length").setEnabled(enable);
			getParam(""+i+"LengthV").setEnabled(enable);
			getParam(""+i+"Taper").setEnabled(enable);
			
			getParam(""+i+"Curve").setEnabled(enable);
			getParam(""+i+"CurveV").setEnabled(enable);
			getParam(""+i+"CurveRes").setEnabled(enable);
			getParam(""+i+"CurveBack").setEnabled(enable);
			
			getParam(""+i+"SegSplits").setEnabled(enable);
			getParam(""+i+"SplitAngle").setEnabled(enable);
			getParam(""+i+"SplitAngleV").setEnabled(enable);
			
			getParam(""+i+"BranchDist").setEnabled(enable);
			getParam(""+i+"Branches").setEnabled(enable);
			
			// down and rotation angle of last level are
			// used for leaves
			enable = enable || 
			(((IntParam)getParam("Leaves")).intValue() != 0 &&
					i==((IntParam)getParam("Levels")).intValue());
			
			getParam(""+i+"DownAngle").setEnabled(enable);
			getParam(""+i+"DownAngleV").setEnabled(enable);
			getParam(""+i+"Rotate").setEnabled(enable);
			getParam(""+i+"RotateV").setEnabled(enable);
		}
		
		for (int i=0; i<((IntParam)getParam("Levels")).intValue() && i<4; i++) {
			
			// enable nSplitAngle/nSplitAngleV if nSegSplits>0
			enable = (((FloatParam)getParam(""+i+"SegSplits")).doubleValue()>0) ||
			(i==0 && ((IntParam)getParam("0BaseSplits")).intValue()>0);
			getParam(""+i+"SplitAngle").setEnabled(enable);
			getParam(""+i+"SplitAngleV").setEnabled(enable);
			
			// enable Curving parameters only when CurveRes>1
			enable = (((IntParam)getParam(""+i+"CurveRes")).intValue()>1);
			getParam(""+i+"Curve").setEnabled(enable);
			getParam(""+i+"CurveV").setEnabled(enable);
			getParam(""+i+"CurveBack").setEnabled(enable);
		}
		
	}
	
};
























