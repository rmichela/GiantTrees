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
               biome == Biome.COLD_TAIGA ||
               biome == Biome.EXTREME_HILLS ||
               biome == Biome.TAIGA ||
               biome == Biome.MEGA_TAIGA ||
               biome == Biome.SAVANNA;
    }

    private Biome simplifyBiome(Biome biome) {
        switch (biome) {
            case FOREST:
            case FOREST_HILLS:
            case FLOWER_FOREST:
                return Biome.FOREST;
            case BIRCH_FOREST:
            case BIRCH_FOREST_HILLS:
            case BIRCH_FOREST_HILLS_MOUNTAINS:
            case BIRCH_FOREST_MOUNTAINS:
                return Biome.BIRCH_FOREST;
            case SWAMPLAND:
            case SWAMPLAND_MOUNTAINS:
                return Biome.SWAMPLAND;
            case JUNGLE:
            case JUNGLE_HILLS:
            case JUNGLE_MOUNTAINS:
                return Biome.JUNGLE;
            case ROOFED_FOREST:
            case ROOFED_FOREST_MOUNTAINS:
                return Biome.ROOFED_FOREST;
            case COLD_TAIGA:
            case COLD_TAIGA_HILLS:
            case COLD_TAIGA_MOUNTAINS:
                return Biome.COLD_TAIGA;
            case EXTREME_HILLS:
            case EXTREME_HILLS_MOUNTAINS:
            case EXTREME_HILLS_PLUS:
            case EXTREME_HILLS_PLUS_MOUNTAINS:
                return Biome.EXTREME_HILLS;
            case TAIGA:
            case TAIGA_HILLS:
            case TAIGA_MOUNTAINS:
                return Biome.TAIGA;
            case MEGA_SPRUCE_TAIGA:
            case MEGA_SPRUCE_TAIGA_HILLS:
            case MEGA_TAIGA:
            case MEGA_TAIGA_HILLS:
                return Biome.MEGA_TAIGA;
            case SAVANNA:
            case SAVANNA_MOUNTAINS:
            case SAVANNA_PLATEAU:
            case SAVANNA_PLATEAU_MOUNTAINS:
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
