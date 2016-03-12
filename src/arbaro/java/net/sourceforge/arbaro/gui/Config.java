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

package net.sourceforge.arbaro.gui;

import java.io.*;
import java.util.Properties;


class Config extends Properties {
	String configFileName;
	
	public Config() {
		
		// config file name
		String folder = System.getProperty("user.home");
		String os = System.getProperty("os.name");
		String filesep = System.getProperty("file.separator");
		if (os.indexOf("Windows") >= 0) {
			configFileName = folder + filesep + "arbaro.cfg";	
		} else {
			configFileName = folder + filesep + ".arbarorc";	
		}
		
		// load properties
		FileInputStream in=null;
		
		try {
			in = new FileInputStream(configFileName);
			load(in);
		} catch (java.io.FileNotFoundException e) {
			in = null;
			System.err.println("Can't find config file. Please do setup Arbaro "+
			"using the setup dialog.");
		} catch (java.io.IOException e) {
			System.err.println("I/O Error. Can't read config file!");
		} finally {
			if (in != null) {
				try { in.close(); } catch (java.io.IOException e) { }
				in = null;
			}
		}
	}
	
	public void store() throws Exception {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(configFileName);
			store(out,"Arbaro setup");
		} catch (java.io.IOException e) {
			throw new Exception("Can't save config file.");
		} finally {
			if (out != null) {
				try { out.close(); } catch (java.io.IOException e) { }
				out = null;
			}
		}
	}
	
	static public String defaultPovrayExe() {
		String os = System.getProperty("os.name");
		if (os.indexOf("Windows") >= 0) {
			return "pvengine.exe";
		} else {
			return "povray"; // Unix
		}
	}
	
};













