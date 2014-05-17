package com.ryanmichela.trees;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2014 Ryan Michela
 */
public class PhysicalCraftingRecipe {
    private Material[][] pattern;
    private Set<Material> usedMaterials = new HashSet<Material>();

    public PhysicalCraftingRecipe(Material[][] pattern) {
        this.pattern = pattern;
        for (Material[] row : pattern) {
            for (Material col : row) {
                usedMaterials.add(col);
            }
        }
    }

    public static PhysicalCraftingRecipe fromStringRepresentation(String[] rows, Map<Character, Material> materialMap) {
        // Sanity check the input
        if (rows == null || rows.length == 0) {
            throw new IllegalArgumentException("rows cannot be null or empty");
        }
        if (materialMap == null || materialMap.size() == 0) {
            throw new IllegalArgumentException("materialMap cannot be null or empty");
        }
        materialMap.put(' ', null);

        int rowLength = rows[0].length();
        for (String row : rows) {
            if (row.length() != rowLength) {
                throw new IllegalArgumentException("all strings in rows must be the same length");
            }
            for (char c : row.toCharArray()) {
                if (!materialMap.containsKey(c)) {
                    throw new IllegalArgumentException("all characters in rows must be in materialMap");
                }
            }
        }

        // Construct the pattern
        Material[][] pattern = new Material[rows.length][rowLength];
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rowLength; j++) {
                char c = rows[i].toCharArray()[j];
                pattern[i][j] = materialMap.get(c);
            }
        }

        return new PhysicalCraftingRecipe(pattern);
    }

    public boolean matches(Block lastPlaced) {
        if (lastPlaced == null) {
            throw new IllegalArgumentException("lastPlaced cannot be null");
        }

        // Verify that the block placed could be part of the pattern
        if (!usedMaterials.contains(lastPlaced.getType())) {
            return false;
        }
        // Scan the world looking for a match
        int size = Math.max(pattern.length, pattern[0].length);
        int patternMatchCount = 0;
        int y = lastPlaced.getY();
        for (int x = lastPlaced.getX() - size + 1; x <= lastPlaced.getX(); x++) {
            outer:for (int z = lastPlaced.getZ() - size + 1; z <= lastPlaced.getZ(); z++) {
                boolean allRowsPass = true;
                inner:for (int px = 0; px < pattern.length; px++) {
                    for (int pz = 0; pz < pattern[0].length; pz++) {
                        Material m = lastPlaced.getWorld().getBlockAt(x+px, y, z+pz).getType();
                        if (pattern[px][pz] != null && m != pattern[px][pz]) {
                            allRowsPass = false;
                            break inner;
                        }
                    }
                }
                if (allRowsPass) {
                    patternMatchCount++;
                    break outer;
                }

                allRowsPass = true;
                inner:for (int px = 0; px < pattern.length; px++) {
                    for (int pz = 0; pz < pattern[0].length; pz++) {
                        Material m = lastPlaced.getWorld().getBlockAt(x+px, y, z+pz).getType();
                        if (pattern[pattern.length - 1 - px][pz] != null && m != pattern[pattern.length - 1 - px][pz]) {
                            allRowsPass = false;
                            break inner;
                        }
                    }
                }
                if (allRowsPass) {
                    patternMatchCount++;
                    break outer;
                }

                allRowsPass = true;
                inner:for (int px = 0; px < pattern.length; px++) {
                    for (int pz = 0; pz < pattern[0].length; pz++) {
                        Material m = lastPlaced.getWorld().getBlockAt(x+px, y, z+pz).getType();
                        if (pattern[px][pattern[0].length - 1 - pz] != null && m != pattern[px][pattern[0].length - 1 - pz]) {
                            allRowsPass = false;
                            break inner;
                        }
                    }
                }
                if (allRowsPass) {
                    patternMatchCount++;
                    break outer;
                }

                allRowsPass = true;
                inner:for (int px = 0; px < pattern.length; px++) {
                    for (int pz = 0; pz < pattern[0].length; pz++) {
                        Material m = lastPlaced.getWorld().getBlockAt(x+px, y, z+pz).getType();
                        if (pattern[pattern.length - 1 - px][pattern[0].length - 1 - pz] != null && m != pattern[pattern.length - 1 - px][pattern[0].length - 1 - pz]) {
                            allRowsPass = false;
                            break inner;
                        }
                    }
                }
                if (allRowsPass) {
                    patternMatchCount++;
                    break outer;
                }
            }
        }
        return patternMatchCount == 1;
    }
}
