/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
// #**************************************************************************
// #
// #    Copyright (C) 2003-2006  Wolfram Diestel
// #
// #    This program is free software; you can redistribute it and/or modify
// #    it under the terms of the GNU General Public License as published by
// #    the Free Software Foundation; either version 2 of the License, or
// #    (at your option) any later version.
// #
// #    This program is distributed in the hope that it will be useful,
// #    but WITHOUT ANY WARRANTY; without even the implied warranty of
// #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// #    GNU General Public License for more details.
// #
// #    You should have received a copy of the GNU General Public License
// #    along with this program; if not, write to the Free Software
// #    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
// #
// #    Send comments and bug fixes to diestel@steloj.de
// #
// #**************************************************************************/

package net.sourceforge.arbaro.transformation;

import java.lang.String;
import java.lang.Math;

import java.text.NumberFormat;
import net.sourceforge.arbaro.params.FloatFormat;

/**
 * A x,y,z-vector class
 * 
 * @author Wolfram Diestel
 */
public class Vector {
	final int X=0;
	final int Y=1;
	final int Z=2;
	
	public final static Vector X_AXIS = new Vector(1,0,0);
	public final static Vector Y_AXIS = new Vector(0,1,0);
	public final static Vector Z_AXIS = new Vector(0,0,1);
	
	private double[] coord = {0,0,0};
	
	public Vector() {
		coord = new double[Z+1];
		//coord = {0,0,0};
	}
	
	public Vector(double x, double y, double z) {
		coord = new double[Z+1];
		coord[X] = x;
		coord[Y] = y;
		coord[Z] = z;
	}
	
	public double abs() {
		//returns the length of the vector
		return Math.sqrt(coord[X]*coord[X] + coord[Y]*coord[Y] + coord[Z]*coord[Z]);
	}
	
//	public String povray() {
//		NumberFormat fmt = FloatFormat.getInstance();
//		return "<"+fmt.format(coord[X])+","
//		+fmt.format(coord[Z])+","
//		+fmt.format(coord[Y])+">";
//	}
	
	public String toString() {
		NumberFormat fmt = FloatFormat.getInstance();
		return "<"+fmt.format(coord[X])+","
		+fmt.format(coord[Y])+","
		+fmt.format(coord[Z])+">";
	}	
	
	public Vector normalize() {
		double abs = this.abs();
		return new Vector(coord[X]/abs,coord[Y]/abs,coord[Z]/abs);
	}
	
	public double getX() {
		return coord[X];
	}
	
	public double getY() {
		return coord[Y];
	}
	
	public double getZ() {
		return coord[Z];
	}
	
	public Vector mul(double factor) {
		// scales the vector
		return new Vector(coord[X]*factor,coord[Y]*factor,coord[Z]*factor);
	} 
	
	public double prod(Vector v) {
		// inner product of two vectors
		return coord[X]*v.getX() + coord[Y]*v.getY() + coord[Z]*v.getZ();
	}
	
	public Vector div (double factor)  {
		return this.mul(1/factor);
	}
	
	public Vector add(Vector v) {
		return new Vector(coord[X]+v.getX(), coord[Y]+v.getY(), coord[Z]+v.getZ());
	}
	
	public Vector sub(Vector v) {
		return this.add(v.mul(-1));
	}
	
	/**
	 * Returns the angle of a 2-dimensional vector (u,v) with the u-axis 
	 *
	 * @param v v-coordinate of the vector
	 * @param u u-coordinate of the vector
	 * @return a value from (-180..180)
	 */
	static public double atan2(double v, double u)  {
		if (u==0) {
			if (v>=0) return 90;
			else return -90;
		} 
		if (u>0)  return Math.atan(v/u)*180/Math.PI;
		if (v>=0) return 180 + Math.atan(v/u)*180/Math.PI;
		return Math.atan(v/u)*180/Math.PI-180;
	}
	
	public void setMaxCoord(Vector v) {
		if (v.getX() > coord[X]) coord[X] = v.getX();
		if (v.getY() > coord[Y]) coord[Y] = v.getY();
		if (v.getZ() > coord[Z]) coord[Z] = v.getZ();
	}
	
	public void setMinCoord(Vector v) {
		if (v.getX() < coord[X]) coord[X] = v.getX();
		if (v.getY() < coord[Y]) coord[Y] = v.getY();
		if (v.getZ() < coord[Z]) coord[Z] = v.getZ();
	}
};








