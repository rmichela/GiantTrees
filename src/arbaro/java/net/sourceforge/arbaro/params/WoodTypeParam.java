package net.sourceforge.arbaro.params;

/**
 * Copyright 2014 Ryan Michela
 */
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