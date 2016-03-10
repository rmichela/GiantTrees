package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.TreeRenderer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreePopulator extends BlockPopulator {
    private Plugin plugin;

    public TreePopulator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        Location refPoint = new Location(world, chunk.getX() * 16 + random.nextInt(16), 64, chunk.getZ() * 16 + random.nextInt(16));
        refPoint.setY(getHighestSoil(world.getHighestBlockAt(refPoint)));

        Biome biome = simplifyBiome(world.getBiome(refPoint.getBlockX(), refPoint.getBlockZ()));
        if (isAcceptableBiome(biome) && treeCanGrow(random)) {
            String treeType = biome.name();

            File treeFile = new File(plugin.getDataFolder(), "biome." + treeType + ".xml");
            File rootFile = new File(plugin.getDataFolder(), "biome." + treeType + ".root.xml");

            if (treeFile.exists()) {
                TreeRenderer renderer = new TreeRenderer(plugin);
                renderer.renderTree(refPoint, treeFile, rootFile, random.nextInt(), false);
            }
        }
    }

    private boolean isAcceptableBiome(Biome biome) {
        return biome == Biome.FOREST ||
               biome == Biome.BIRCH_FOREST ||
               biome == Biome.SWAMPLAND ||
               biome == Biome.JUNGLE ||
               biome == Biome.ROOFED_FOREST ||
               biome == Biome.TAIGA_COLD ||
               biome == Biome.EXTREME_HILLS ||
               biome == Biome.TAIGA ||
               biome == Biome.REDWOOD_TAIGA ||
               biome == Biome.SAVANNA;
    }

    private Biome simplifyBiome(Biome biome) {
        switch (biome) {
            case FOREST:
            case FOREST_HILLS:
            case MUTATED_FOREST:
                return Biome.FOREST;
            case BIRCH_FOREST:
            case BIRCH_FOREST_HILLS:
            case MUTATED_BIRCH_FOREST:
            case MUTATED_BIRCH_FOREST_HILLS:
                return Biome.BIRCH_FOREST;
            case SWAMPLAND:
            case MUTATED_SWAMPLAND:
                return Biome.SWAMPLAND;
            case JUNGLE:
            case JUNGLE_EDGE:
            case JUNGLE_HILLS:
            case MUTATED_JUNGLE:
            case MUTATED_JUNGLE_EDGE:
                return Biome.JUNGLE;
            case ROOFED_FOREST:
            case MUTATED_ROOFED_FOREST:
                return Biome.ROOFED_FOREST;
            case TAIGA_COLD:
            case TAIGA_COLD_HILLS:
            case MUTATED_TAIGA_COLD:
                return Biome.TAIGA_COLD;
            case EXTREME_HILLS:
            case EXTREME_HILLS_WITH_TREES:
            case MUTATED_EXTREME_HILLS:
            case MUTATED_EXTREME_HILLS_WITH_TREES:
            case SMALLER_EXTREME_HILLS :
                return Biome.EXTREME_HILLS;
            case TAIGA:
            case TAIGA_HILLS:
            case MUTATED_TAIGA :
                return Biome.TAIGA;
            case REDWOOD_TAIGA:
            case REDWOOD_TAIGA_HILLS:
            case MUTATED_REDWOOD_TAIGA:
            case MUTATED_REDWOOD_TAIGA_HILLS:
                return Biome.REDWOOD_TAIGA;
            case SAVANNA:
            case SAVANNA_ROCK:
            case MUTATED_SAVANNA:
            case MUTATED_SAVANNA_ROCK:
                return Biome.SAVANNA;
            default:
                return null;
        }
    }

    private boolean treeCanGrow(Random random) {
        double growChance = plugin.getConfig().getDouble("treeGrowthPercentChance");
        if (growChance > 100.0) {
            growChance = 100.0;
            plugin.getLogger().warning("treeGrowthPercentChance > 100. Assuming 100.");
        }
        if (growChance < 0.0) {
            growChance = 0.0;
            plugin.getLogger().warning("treeGrowthPercentChance < 0. Assuming zero.");
        }

        double randomRoll = random.nextDouble() * 100.0;
        return growChance > randomRoll;
    }

    int getHighestSoil(Block highestBlock) {
        while (highestBlock.getY() > 0 &&
                highestBlock.getType() != Material.DIRT && // Includes podzol
                highestBlock.getType() != Material.GRASS &&
                highestBlock.getType() != Material.MYCEL &&
                highestBlock.getType() != Material.SAND) {
            highestBlock = highestBlock.getRelative(BlockFace.DOWN);
        }
        return highestBlock.getY();
    }
}
