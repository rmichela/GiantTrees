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

package net.sourceforge.arbaro.params;

/**
 * Random class to add variations to the tree. It has methods
 * getState() and setState() to save and restore the random seed
 */
public class Random extends java.util.Random {
	
	public Random(long seed) {
		super(seed);
	}
	
	public double uniform(double low, double high) {
		return low+nextDouble()*(high-low);
	}
	
	public long getState() {
		// the original random generator doesn't provide an interface
		// to read, and reset it's state, so this is a hack here, to make
		// this possible. The random generator is reseeded here with a seed
		// got from the generator, this seed are returned as state.
		long state = nextLong();
		setSeed(state);
		return state;
	}
	
	public void setState(long state) {
		setSeed(state);
	}
};























