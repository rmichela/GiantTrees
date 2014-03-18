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

import javax.swing.event.*;

public abstract class AbstractParam {
	public static final int GENERAL = -999; // no level - general params
	String name;
	String group;
	int level;
	int order;
	String shortDesc;
	String longDesc;
	boolean enabled;
	
	protected ChangeEvent changeEvent = null;
	protected EventListenerList listenerList = new EventListenerList();
	
	public AbstractParam(String nam, String grp, int lev, int ord, String sh, String lng) {
		name = nam;
		group = grp;
		level = lev;
		order = ord;
		shortDesc = sh;
		longDesc = lng;
		enabled=true;
	}
	
	public abstract void setValue(String val) throws ParamError;
	public abstract String getValue();
	public abstract String getDefaultValue();
	public abstract void clear();
	public abstract boolean empty();
	
	public static boolean loading=false;
	
	protected static void warn(String warning) {
		if (! loading) System.err.println("WARNING: "+warning);
	}
	
	public void setEnabled(boolean en) {
		enabled = en;
		fireStateChanged();
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public String getName() {
		return name;
	}
	
	public String getGroup() {
		return group;
	}
	
	public int getLevel() {
		return level;
	}

	public int getOrder() {
		return order;
	}
	
	public String getShortDesc() {
		return shortDesc;
	}
	
	public String toString() { 
		if (! empty()) {
			return getValue();
		} else {
			return getDefaultValue();
		}
	}
	
	public String getLongDesc() {
		return longDesc;
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







