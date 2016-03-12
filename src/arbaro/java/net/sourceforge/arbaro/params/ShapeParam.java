/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
//  #**************************************************************************
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

/**
 * @author wdiestel
 *
 */
public final class ShapeParam extends IntParam {

	//Integer [] values;
	final static String[] items = { "conical", "spherical", "hemispherical", "cylindrical", 
			"tapered cylindrical","flame","inverse conical","tend flame","envelope" };
	
	/**
	 * @param nam
	 * @param mn
	 * @param mx
	 * @param def
	 * @param grp
	 * @param lev
	 * @param sh
	 * @param lng
	 */
	public ShapeParam(String nam, int mn, int mx, int def, String grp, int lev,
			int ord, String sh, String lng) {
		super(nam, mn, mx, def, grp, lev, ord, sh, lng);
	}

	public String toString() {
		return items[intValue()];
	}
	
	public static String[] values() {
		return items;
	}
	
}
