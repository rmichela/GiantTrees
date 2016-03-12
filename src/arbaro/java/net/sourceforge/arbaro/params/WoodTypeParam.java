/*
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
package net.sourceforge.arbaro.params;

public final class WoodTypeParam extends StringParam {

    final static String[] items = {"Oak","Spruce","Birch","Jungle","Acacia","Dark Oak"};

    /**
     * @param nam
     * @param def
     * @param grp
     * @param lev
     * @param sh
     * @param lng
     */
    public WoodTypeParam(String nam, String def, String grp, int lev,
                          int ord, String sh, String lng) {
        super(nam, def, grp, lev, ord, sh, lng);
    }

    public static String[] values() {
        return items;
    }

}