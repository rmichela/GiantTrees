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

public class FloatParam extends AbstractParam {
    private double min;
    private double max;
    private double deflt;
    private double value;

    FloatParam(String nam, double mn, double mx, double def, String grp, int lev,
	       int ord, String sh, String lng) {
	super(nam,grp,lev,ord,sh,lng);
	min = mn;
	max = mx;
	deflt = def;
	value = Double.NaN;
    }

    public String getDefaultValue() {
	Double d = new Double(deflt);
	return d.toString();
    }

    public void clear() {
	value = Double.NaN;
	fireStateChanged();
    }

    public void setValue(String val) throws ParamError {
    	double d;
    	try {
    		d = Double.parseDouble(val);
    	} catch (NumberFormatException e) {
    		throw new ParamError("Error setting value of "+name+". \""+val+"\" isn't a valid number.");
    	}
    	
    	if (d<min) {
    		throw new ParamError("Value of "+name+" should be greater then or equal to "+min);
    	}
    	if (d>max) {
    		throw new ParamError("Value of "+name+" should be less then or equal to "+max);
    	}
    	value = d;
    	fireStateChanged();
    }

    public String getValue() {
	Double d = new Double(value);
	return d.toString();
    }

    public boolean empty() {
	return Double.isNaN(value);
    }

    public double doubleValue() {
    	if (empty()) {
//    		warn(name+" not given, using default value("+deflt+")");
    		// set value to default, t.e. don't warn again
    		value=deflt;
    		fireStateChanged();
    	}
    	return value;
    }

    public String getLongDesc() {
    	String desc = super.getLongDesc();
    	desc += "<br><br>";
    	if (! Double.isNaN(min)) {
    		desc += "Minimum: "+min+"\n";
    	}
    	if (! Double.isNaN(max)) {
    		desc += "Maximum: "+max+"\n";
    	}
    	if (! Double.isNaN(deflt)) {
    		desc += "Default: "+deflt+"\n";
    	}
    	return desc;
    }
};




