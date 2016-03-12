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

import net.sourceforge.arbaro.params.IntParam;
import net.sourceforge.arbaro.params.Params;
import net.sourceforge.arbaro.tree.Tree;
import net.sourceforge.arbaro.mesh.Mesh;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


/**
 * A tree used to preview the edited tree, it draws
 * the stems and leaves with lines to Graphics context
 * and modifies the level and branching parameters to
 * calculate and draw only parts of the tree, reducing
 * calculation time as well.
 * 
 * @author wdiestel
 *
 */
public final class PreviewTree extends Tree {
	// preview always shows this levels and 
	// the previous levels stems 
	int showLevel=1;
	Params originalParams;
	Mesh mesh;
	
	protected ChangeEvent changeEvent = null;
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * @param other
	 */
	public PreviewTree(Tree other) {
		super(other);
		originalParams=other.params;
	}
	
	public void setShowLevel(int l) {
		int Levels = ((IntParam)(originalParams.getParam("Levels"))).intValue(); 
		if (l>Levels) showLevel=Levels;
		else showLevel=l;
	}
	
	public int getShowLevel() {
		return showLevel;
	}

	public void remake() throws Exception {
			clear();
			params = new Params(originalParams);
			params.preview=true;
//			previewTree = new Tree(originalTree);
			
			// manipulate params to avoid making the whole tree
			// FIXME: previewTree.Levels <= tree.Levels
			int Levels = ((IntParam)(originalParams.getParam("Levels"))).intValue(); 
			if (Levels>showLevel+1) {
				setParam("Levels",""+(showLevel+1));
				setParam("Leaves","0");
			} 
			for (int i=0; i<showLevel; i++) {
				setParam(""+i+"Branches","1");
				// if (((FloatParam)previewTree.getParam(""+i+"DownAngleV")).doubleValue()>0)
				setParam(""+i+"DownAngleV","0");
			}
			
		    make();	
			mesh = createStemMesh(true);
			
			fireStateChanged();
	}
	
	public Mesh getMesh() {
		return mesh;
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

	
}
