//#**************************************************************************
//#
//#    $Id:Progress.java 77 2006-11-05 11:46:01 +0000 (So, 05 Nov 2006) wolfram $  
//#      - class for holding progress information
//#          
//#
//#    Copyright (C) 2004  Wolfram Diestel
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

class ProgressError extends Exception{
	public ProgressError(String msg) {
		super(msg);
	}
};

/**
 * Shows progress while creating and exporting a tree
 */
public final class Progress {
	String phase;
	long maxProgress;
	long progress;
	
	public Progress() {
		maxProgress=100;
		progress=0;
	}
	
	synchronized public void beginPhase(String ph, long max) {
		phase = ph;
		maxProgress = max;
		progress = 0;
	}
	
	synchronized public void endPhase() {
		progress = maxProgress;
	}
	
	synchronized public void setProgress(long prog) throws ProgressError {
		if (prog>maxProgress)
			throw new ProgressError("Error in progress. The progress "+prog+ 
					" shouldn't exceed "+maxProgress+".");
		progress = prog;
	}
	
	synchronized public void incProgress(long inc) {
		progress += inc;
	}
	
	synchronized public int getPercent() {
		if (maxProgress<=0) return -1; // indeterminate
		else {
			int percent = (int)(progress/(float)maxProgress*100);
			if (percent<0) return 0;
			else if (percent>100) return 100;
			else return percent;
		}
	}
	
	synchronized public String getPhase() {
		return phase;
	}
	
	synchronized public long getMaxProgress() {
		return maxProgress;
	}
}
