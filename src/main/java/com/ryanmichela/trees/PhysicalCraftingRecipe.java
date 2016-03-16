/*
 * Copyright (C) 2014 Ryan Michela
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
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
package com.ryanmichela.trees;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * PhysicalCraftingRecipe matches blocks laid in a pattern horizontally in the
 * workd.
 */
public class PhysicalCraftingRecipe {

  public final byte[][]      data;
  public final Material[][]  pattern;
  public final Set<Material> usedMaterials = new HashSet<>();

  /**
   * Creates a PhysicalCraftingRecipe from a 2D pattern of Materials. The
   * pattern must
   * be rectangular - all rows must be the same length.
   * 
   * @param pattern
   *          The rectangle of Materials to match.
   */
  public PhysicalCraftingRecipe(final Material[][] pattern) {
    this(pattern, PhysicalCraftingRecipe.bytesFromPattern(pattern));
  }

  /**
   * Creates a PhysicalCraftingRecipe from a 2D pattern of Materials and data
   * values. The pattern must
   * be rectangular - all rows must be the same length. Data must be the same
   * dimensions as pattern.
   * A data value of -1 matches all data values.
   * 
   * @param pattern
   *          The rectangle of Materials to match.
   * @param data
   *          The rectangle block data bytes to match.
   */
  public PhysicalCraftingRecipe(final Material[][] pattern, final byte[][] data) {
    Validate.notEmpty(pattern, "pattern cannot be null or empty");
    Validate.notEmpty(data, "data cannot be null or empty");

    // Validate that pattern and data are the same 'shape'
    if (pattern.length != data.length) { throw new IllegalArgumentException(
                                                                            "pattern and data must be the same 'shape'"); }
    for (int i = 0; i < pattern.length; i++) {
      if (pattern[i].length != data[i].length) { throw new IllegalArgumentException(
                                                                                    "pattern and data must be the same 'shape'"); }
    }

    this.pattern = pattern;
    this.data = data;
    for (final Material[] row : pattern) {
      for (final Material col : row) {
        this.usedMaterials.add(col);
      }
    }
  }

  /**
   * Constructs a PhysicalCraftingRecipe from an array of strings. Each
   * character in the array represents one
   * combination of material/data encoded in materialMap and dataMap. Data
   * values of -1 match any material value.
   * 
   * @param rows
   *          The characters making up the crafting recipe
   * @param materialMap
   *          The materials to match in the crafting recipe
   * @param dataMap
   *          The data values to match in the crafting recipe
   * @return
   */
  public static PhysicalCraftingRecipe
      fromStringRepresentation(final String[] rows,
                               final Map<Character, Material> materialMap,
                               final Map<Character, Byte> dataMap) {
    // Sanity check the input
    Validate.notEmpty(rows, "rows cannot be null or empty");
    Validate.notEmpty(materialMap, "materialMap cannot be null or empty");
    Validate.notEmpty(dataMap, "dataMap cannot be null or empty");
    Validate.isTrue(materialMap.size() == dataMap.size(),
                    "materialMap and dataMap must be the same length");
    materialMap.put(' ', null);

    // Validate the relationship between rows and maps
    final int rowLength = rows[0].length();
    for (final String row : rows) {
      if (row.length() != rowLength) { throw new IllegalArgumentException(
                                                                          "all strings in rows must be the same length"); }
      for (final char c : row.toCharArray()) {
        if (!materialMap.containsKey(c)) { throw new IllegalArgumentException(
                                                                              "all characters in rows must be in materialMap"); }
        if (!dataMap.containsKey(c)) { throw new IllegalArgumentException(
                                                                          "all characters in rows must be in dataMap"); }
      }
    }

    // Construct the pattern
    final Material[][] pattern = new Material[rows.length][rowLength];
    final byte[][] data = new byte[rows.length][rowLength];
    for (int i = 0; i < rows.length; i++) {
      for (int j = 0; j < rowLength; j++) {
        final char c = rows[i].toCharArray()[j];
        pattern[i][j] = materialMap.get(c);
        data[i][j] = dataMap.get(c);
      }
    }

    return new PhysicalCraftingRecipe(pattern, data);
  }

  /**
   * Constructs a PhysicalCraftingRecipe from an array of strings. Each
   * character in the array represents one
   * combination of material/data specified in the materialDataMap. The
   * materialDataMap map entries start with the
   * name of the associated material, followed by an optional data value
   * separated by a colon.
   * 
   * @param rows
   *          The characters making up the crafting recipe
   * @param materialDataMap
   *          A mapping between the crafting recipe characters and
   *          materials/data.
   * @return
   */
  public static PhysicalCraftingRecipe
      fromStringRepresentation(final String[] rows,
                               final Map<Character, String> materialDataMap) {
    Validate.notEmpty(rows, "rows cannot be null or empty");
    Validate.notEmpty(materialDataMap, "materialMap cannot be null or empty");

    // Break up materialDataMap into materialMap and dataMap
    final Map<Character, Material> materialMap = new HashMap<>();
    final Map<Character, Byte> dataMap = new HashMap<>();
    for (final Character c : materialDataMap.keySet()) {
      final String s = materialDataMap.get(c);
      final String[] splits = s.split(":", 2);
      final Material m = Material.matchMaterial(splits[0]);
      if (m == null) { throw new IllegalArgumentException(
                                                          splits[0]
                                                              + " is not a valid material"); }
      byte b = -1;
      if (splits.length > 1) {
        try {
          b = Byte.parseByte(splits[1]);
        } catch (final NumberFormatException e) {
          throw new IllegalArgumentException(splits[1]
                                             + " is not a valid data byte");
        }
      }
      materialMap.put(c, m);
      dataMap.put(c, b);
    }

    return PhysicalCraftingRecipe.fromStringRepresentation(rows, materialMap,
                                                           dataMap);
  }

  private static byte[][] bytesFromPattern(final Material[][] pattern) {
    Validate.notEmpty(pattern, "pattern cannot be null or empty");
    final byte[][] bytes = new byte[pattern.length][];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = new byte[pattern[i].length];
      Arrays.fill(bytes[i], (byte) -1);
    }
    return bytes;
  }

  /**
   * Determines if this PhysicalCraftingRecipe matches the blocks in the world.
   * 
   * @param lastPlaced
   *          A starting point for evaluating this recipe.
   * @return
   */
  public boolean matches(final Block lastPlaced) {
    Validate.notNull(lastPlaced, "lastPlaced cannot be null");

    // Verify that the block placed could be part of the pattern
    if (!this.usedMaterials.contains(lastPlaced.getType())) { return false; }
    // Scan the world looking for a match
    final int size = Math.max(this.pattern.length, this.pattern[0].length);
    int patternMatchCount = 0;
    final int y = lastPlaced.getY();
    for (int x = (lastPlaced.getX() - size) + 1; x <= lastPlaced.getX(); x++) {
      outer: for (int z = (lastPlaced.getZ() - size) + 1; z <= lastPlaced.getZ(); z++) {
        boolean allRowsPass = true;
        inner: for (int px = 0; px < this.pattern.length; px++) {
          for (int pz = 0; pz < this.pattern[0].length; pz++) {
            final Block b = lastPlaced.getWorld().getBlockAt(x + px, y, z + pz);
            if ((this.pattern[px][pz] != null)
                && (b.getType() != this.pattern[px][pz])) {
              allRowsPass = false;
              break inner;
            } else if ((this.data[px][pz] != -1)
                       && (b.getData() != this.data[px][pz])) {
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
        inner: for (int px = 0; px < this.pattern.length; px++) {
          for (int pz = 0; pz < this.pattern[0].length; pz++) {
            final Block b = lastPlaced.getWorld().getBlockAt(x + px, y, z + pz);
            if ((this.pattern[this.pattern.length - 1 - px][pz] != null)
                && (b.getType() != this.pattern[this.pattern.length - 1 - px][pz])) {
              allRowsPass = false;
              break inner;
            } else if ((this.data[this.pattern.length - 1 - px][pz] != -1)
                       && (b.getData() != this.data[this.pattern.length - 1
                                                    - px][pz])) {
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
        inner: for (int px = 0; px < this.pattern.length; px++) {
          for (int pz = 0; pz < this.pattern[0].length; pz++) {
            final Block b = lastPlaced.getWorld().getBlockAt(x + px, y, z + pz);
            if ((this.pattern[px][this.pattern[0].length - 1 - pz] != null)
                && (b.getType() != this.pattern[px][this.pattern[0].length - 1
                                                    - pz])) {
              allRowsPass = false;
              break inner;
            } else if ((this.data[px][this.pattern[0].length - 1 - pz] != -1)
                       && (b.getData() != this.data[px][this.pattern[0].length
                                                        - 1 - pz])) {
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
        inner: for (int px = 0; px < this.pattern.length; px++) {
          for (int pz = 0; pz < this.pattern[0].length; pz++) {
            final Block b = lastPlaced.getWorld().getBlockAt(x + px, y, z + pz);
            if ((this.pattern[this.pattern.length - 1 - px][this.pattern[0].length
                                                            - 1 - pz] != null)
                && (b.getType() != this.pattern[this.pattern.length - 1 - px][this.pattern[0].length
                                                                              - 1
                                                                              - pz])) {
              allRowsPass = false;
              break inner;
            } else if ((this.data[this.pattern.length - 1 - px][this.pattern[0].length
                                                                - 1 - pz] != -1)
                       && (b.getData() != this.data[this.pattern.length - 1
                                                    - px][this.pattern[0].length
                                                          - 1 - pz])) {
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
