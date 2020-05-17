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

import java.io.File;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;

import com.ryanmichela.trees.rendering.TreeRenderer;

public class TreePopulator extends BlockPopulator {

  private final Plugin plugin;

  public TreePopulator(final Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void
      populate(final World world, final Random random, final Chunk chunk) {
    final Location refPoint = new Location(world, (chunk.getX() * 16)
                                                  + random.nextInt(16), 64,
                                           (chunk.getZ() * 16)
                                               + random.nextInt(16));
    refPoint.setY(this.getHighestSoil(world.getHighestBlockAt(refPoint)));

    final Biome biome = this.simplifyBiome(world.getBiome(refPoint.getBlockX(),
                                                          refPoint.getBlockZ()));
    if (this.isAcceptableBiome(biome) && this.treeCanGrow(random)) {
      final String treeType = biome.name();

      final File treeFile = new File(this.plugin.getDataFolder(), "biome."
                                                                  + treeType
                                                                  + ".xml");
      final File rootFile = new File(this.plugin.getDataFolder(), "biome."
                                                                  + treeType
                                                                  + ".root.xml");

      if (treeFile.exists()) {
        final TreeRenderer renderer = new TreeRenderer(this.plugin);
        renderer.renderTree(refPoint, treeFile, rootFile, random.nextInt(),
                            false);
      }
    }
  }

  int getHighestSoil(Block highestBlock) {
    while ((highestBlock.getY() > 0)
           && (highestBlock.getType() != Material.DIRT)
           && // Includes podzol
           (highestBlock.getType() != Material.GRASS)
           && (highestBlock.getType() != Material.MYCELIUM)
           && (highestBlock.getType() != Material.SAND)) {
      highestBlock = highestBlock.getRelative(BlockFace.DOWN);
    }
    return highestBlock.getY();
  }

  private boolean isAcceptableBiome(final Biome biome) {
    return (biome == Biome.FOREST) || (biome == Biome.BIRCH_FOREST)
           || (biome == Biome.SWAMP) || (biome == Biome.JUNGLE)
           || (biome == Biome.DARK_FOREST) || (biome == Biome.DARK_FOREST_HILLS)
           || (biome == Biome.MOUNTAINS) || (biome == Biome.TAIGA)
           || (biome == Biome.SAVANNA);
  }

  private Biome simplifyBiome(final Biome biome) {
    switch (biome) {
      case FOREST:
      case FLOWER_FOREST:
        return Biome.FOREST;
      case BIRCH_FOREST:
      case BIRCH_FOREST_HILLS:
      case TALL_BIRCH_FOREST:
      case TALL_BIRCH_HILLS:
        return Biome.BIRCH_FOREST;
      case SWAMP:
      case SWAMP_HILLS:
        return Biome.SWAMP;
      case JUNGLE:
      case JUNGLE_HILLS:
      case MODIFIED_JUNGLE:
        return Biome.JUNGLE;
      case DARK_FOREST:
      case DARK_FOREST_HILLS:
        return Biome.DARK_FOREST;
      case TAIGA:
      case TAIGA_HILLS:
      case TAIGA_MOUNTAINS:
      case SNOWY_TAIGA_MOUNTAINS:
      case SNOWY_TAIGA:
      case SNOWY_TAIGA_HILLS:
      case GIANT_SPRUCE_TAIGA:
      case GIANT_SPRUCE_TAIGA_HILLS:
      case GIANT_TREE_TAIGA:
      case GIANT_TREE_TAIGA_HILLS:
        return Biome.TAIGA;
      case MOUNTAINS:
      case WOODED_MOUNTAINS:
        return Biome.MOUNTAINS;
      case SAVANNA:
      case SAVANNA_PLATEAU:
      case SHATTERED_SAVANNA:
      case SHATTERED_SAVANNA_PLATEAU:
        return Biome.SAVANNA;
      default:
        return null;
    }
  }

  private boolean treeCanGrow(final Random random) {
    double growChance = this.plugin.getConfig()
                                   .getDouble("treeGrowthPercentChance");
    if (growChance > 100.0) {
      growChance = 100.0;
      this.plugin.getLogger()
                 .warning("treeGrowthPercentChance > 100. Assuming 100.");
    }
    if (growChance < 0.0) {
      growChance = 0.0;
      this.plugin.getLogger()
                 .warning("treeGrowthPercentChance < 0. Assuming zero.");
    }

    final double randomRoll = random.nextDouble() * 100.0;
    return growChance > randomRoll;
  }
}
