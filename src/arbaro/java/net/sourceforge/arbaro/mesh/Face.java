/*
 * This copy of Arbaro is redistributed to you under GPLv3 or (at your option)
 * any later version. The original copyright notice is retained below.
 */
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

package net.sourceforge.arbaro.mesh;

/**
 * A class holding three points forming a triangular face (of a mesh). 
 * 
 * @author Wolfram Diestel
 */

public final class Face {
    public long [] points;

    public Face(long i, long j, long k) {
    	points = new long[3];
    	points[0]=i;
    	points[1]=j;
    	points[2]=k;
    }
    
    public Face(long i, long j, long k, long m) {
		points = new long[4];
		points[0]=i;
		points[1]=j;
		points[2]=k;
		points[3]=m;
    }
}
