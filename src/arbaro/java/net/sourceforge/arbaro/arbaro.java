/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
/*
 * Portions of this file are
 * Copyright (C) 2014 Ryan Michela
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

package net.sourceforge.arbaro;

import net.sourceforge.arbaro.tree.Tree;

import java.io.*;

/**
 * Main class for command line version of Arbaro 
 */

public class arbaro {
	static Tree tree;
	
	static int XMLinput = 0;
	static int CFGinput = 1;
	static int XMLoutput = 99;
	static void println(String s) { System.err.println(s); }
	static void println() { System.err.println(); }
	
	public static final String programName = 
		"Arbaro 2.0 - creates trees objects for rendering from xml parameter files\n"+
		"(c) 2003-2013 by Wolfram Diestel (wrote core code) <diestel@steloj.de> \n"+
		"(c) 2014 by Ryan Michela (adapted for Bukkit) <deltahat@gmail.com> \n"+
        "(GPL see file COPYING)\n";
	
	static void printProgramName() {
		println(programName);
		println();
	}
	
	// TODO need switch for adding uv-coordinates in output
	// switches should be more similar to the actual class
	// structure now, e.g. --exporter=OBJ (-x OBJ)
	static void usage () {
		println("syntax:"); 
		println("java -jar arbaro.jar [OPTIONS] <paramfile.xml> > <tree.inc>");
		println();
		println("options");
		println("     -h|--help           Show this helpscreen");
		println();
		println("     -q|--quiet          Only error messages are output to stderr no progress");
		println();
		println("     -d|--debug          Much debugging ouput should be interesting for developer only");
		println();
		println("     -o|--output <file>  Output Povray code to this file instead of STDOUT");
		println();
		println("     -s|--seed <seed>    Random seed for the tree, default is 13, but you won't all");
		println("                         trees look the same as mine, so giv something like -s 17 here");
		println("                         the seed is part of the  declaration string in the povray file");
		println();
		println("    -l|--levels <level>  1..Levels+1 -- calc and ouput only so much levels, usefull for");
		println("                         fast testing of parameter changes or to get a draft tree for");
		println("                         a first impression of a scene without all that small stems and");
		println("                         leaves. Levels+1 means calc alle Levels and Leaves, but this");
		println("                         is the same as not giving this option here");
		println();
		println("    -m|--mesh [<smooth>] Output stems as mesh2 objects. The optional smooth value influences");
		println("                         how much vertices are used for every stem section and for which");
		println("                         levels normals should be used to hide the triangle borders");
		println();
		println("    --dxf [<smooth>]     Output stems as DXF file. The optional smooth value influences");
		println("                         how much vertices are used for every stem section");
		println();
		println("    --obj [<smooth>]     Output stems as Wavefront OBJ file. The optional smooth value influences");
		println("                         how much vertices are used for every stem section");
		println();
		println("    -c|--cones           output stems as unions of cones and spheres, Lobes don't work");
		println("                         with this option, but source files are a little bit smaller.");
		println("                         Povray read Mesh2 objects faster. Cones are handy for use with");
		println("                         KPovmodeler, which doesn't support mesh2 objects yet.");
		println();
		println("    -r|--treecfg         Input file is a simple Param=Value list. Needs less typing for");
		println("                         a new tree than writing XML code");
		println();
		println("    -x|--xml             Output parameters as XML tree definition instead of creating");
		println("                         the tree and writing it as povray code. Useful for converting a");
		println("                         simple parameter list to a XML file: ");
		println("                            arbaro.py --treecfg -x < mytree.cfg > mytree.xml");
		println("    -p|--scene [<file>]  output Povray scene file");
		println();
		println("example:");
		println();
		println("    java -jar arbaro.jar trees/quaking_aspen.xml > pov/quaking_aspen.inc");
		println();
	}
	
	public static void main (String [] args) throws Exception{
		//	try {
		tree = new Tree();
		
		boolean quiet = false;
		boolean debug = false;
		int seed = 13;
		int levels = -1;
		int output=Tree.MESH;
		double smooth=-1;
		int input = XMLinput;
		String input_file = null;
		String output_file = null;
		String scene_file = null;
		
		for (int i=0; i<args.length; i++) {
			
			if (args[i].equals("-d") || args[i].equals("--debug")) {
				debug = true;
			} else if (args[i].equals("-h") || args[i].equals("--help")) {
				printProgramName();
				usage();
				System.exit(0);
			} else if (args[i].equals("-q") || args[i].equals("--quiet")) {
				quiet = true;
			} else if (args[i].equals("-o") || args[i].equals("--output")) {
				output_file = args[++i];
			} else if (args[i].equals("-s") || args[i].equals("--seed")) {
				seed = new Integer(args[++i]).intValue();
			} else if (args[i].equals("-l") || args[i].equals("--levels")) {
				levels = new Integer(args[++i]).intValue();
			} else if (args[i].equals("-c") || args[i].equals("--cones")) {
				output = Tree.CONES;
			} else if (args[i].equals("-m") || args[i].equals("--mesh")) {
				output = Tree.MESH;
				if (args[i+1].charAt(0) == '0' || args[i+1].charAt(0) == '1') {
					smooth = new Double(args[++i]).doubleValue();
				}
			} else if (args[i].equals("--dxf")) {
				output = Tree.DXF;
				if (args[i+1].charAt(0) == '0' || args[i+1].charAt(0) == '1') {
					smooth = new Double(args[++i]).doubleValue();
				}
			} else if (args[i].equals("--obj")) {
				output = Tree.OBJ;
				if (args[i+1].charAt(0) == '0' || args[i+1].charAt(0) == '1') {
					smooth = new Double(args[++i]).doubleValue();
				}
			} else if (args[i].equals("-x") || args[i].equals("--xml")) {
				output = XMLoutput;
			} else if (args[i].equals("-r") || args[i].equals("--treecfg")) {
				input = CFGinput;
			} else if (args[i].equals("-p") || args[i].equals("--scene")) {
				scene_file = args[++i];
			} else if (args[i].charAt(0) == '-') {
				printProgramName();
				usage();
				System.err.println("Invalid option "+args[i]+"!");
				System.exit(1);
			} else {
				// rest of args should be files 
				// input_files = new String[] = ...
				input_file = args[i];
				break;
			}
		}
		
		
		//########## read params from XML file ################
		
		if (! quiet) {
			printProgramName();
		}
		
		tree.params.debug=debug;
		tree.setOutputType(output);
		// put here or later?
		if (smooth>=0) tree.params.Smooth = smooth;
		
		InputStream in;
		if (input_file == null) {
			if (! quiet) { 
				System.err.println("No tree definition file given.");
				System.err.println("Reading parameters from STDIN...");
			}
			in = System.in;
		} else {
			if (! quiet) { 
				System.err.println("Reading parameters from "
					+ input_file + "...");
			}
			in = new FileInputStream(input_file);
		}
		
		// read parameters
		if (input == CFGinput) tree.params.readFromCfg(in);
		else tree.readFromXML(in);
		
		// FIXME: put here or earlier?
		if (smooth>=0) tree.params.setParam("Smooth",new Double(smooth).toString());
		
		tree.params.verbose=(! quiet);
		tree.params.Seed=seed;
		tree.params.stopLevel = levels;
		
		PrintWriter out;
		if (output_file == null) {
			out = new PrintWriter(new OutputStreamWriter(System.out));
		} else {
			out = new PrintWriter(new FileWriter(new File(output_file)));
		}
		
		if (output==XMLoutput) {
			// save parameters in XML file, don't create tree
			tree.params.toXML(out);
		} else {
			tree.make();
			tree.output(out);
		}
		
		if (scene_file != null) {
			if (! quiet) System.err.println("Writing Povray scene to "+scene_file+"...");
			PrintWriter scout = new PrintWriter(new FileWriter(new File(scene_file)));
			tree.outputScene(scout);
		}
		
	}
};













