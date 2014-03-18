//#**************************************************************************
//#
//#    Copyright (C) 2004-2006  Wolfram Diestel
//#
//#    This program is free software; you can redistribute it and/or modify
//#    it under the terms of the GNU General Public License as published by
//#    the Free Software Foundation; either version 2 of the License, or
//#    (at your option) any later version.
//#
//#    This program is distributed in the hope that it will be useful,
//#    but WITHOUT ANY WARRANTY; without even the implied warranty of
//#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//#    GNU General Public License for more details.
//#
//#    You should have received a copy of the GNU General Public License
//#    along with this program; if not, write to the Free Software
//#    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//#
//#    Send comments and bug fixes to diestel@steloj.de
//#
//#**************************************************************************/

package net.sourceforge.arbaro.export;

// explaining of 3D graphic formats at
// http://www.dcs.ed.ac.uk/home/mxr/gfx/3d-hi.html

import net.sourceforge.arbaro.tree.Tree;

import java.io.PrintWriter;

/**
 * Base class for tree output to 3D-formats like Povray, DXF 
 * and Wavefront OBJ file formats
 * 
 * @author Wolfram Diestel
 *
 */
public abstract class Exporter {
	protected Tree tree;
	protected PrintWriter w;
	protected Progress progress;
	
	public Exporter(Tree tree, PrintWriter printwriter, Progress progress) {
		this.tree = tree;
		this.w = printwriter;
		this.progress = progress;
	}
	
	public abstract void write() throws ExportError;
}
