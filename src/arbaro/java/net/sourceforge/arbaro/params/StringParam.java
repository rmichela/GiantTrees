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

package net.sourceforge.arbaro.params;

public class StringParam extends AbstractParam {
    private String deflt;
    private  String value;

    StringParam(String nam, String def, String grp, int lev, 
    		int ord, String sh, String lng) {
	super(nam,grp,lev,ord,sh,lng);
	deflt = def;
	value = "";
    }

    public String getDefaultValue() {
	return deflt;
    }

    public void clear() {
	value = "";
	fireStateChanged();
    }

    public void setValue(String val) {
	value = val;
	fireStateChanged();
    }

    public boolean empty() {
	return value.equals("");
    }

    public String getValue() {
	if (empty()) {
//	    warn(name+" not given, using default value("+deflt+")");
	    // set value to default, t.e. don't warn again
	    value=deflt;
	    fireStateChanged();
	}
	return value;
    }
}
