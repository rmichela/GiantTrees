package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.TreeRenderer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
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
        refPoint.setY(world.getHighestBlockYAt(refPoint));

        Biome biome = world.getBiome(refPoint.getBlockX(), refPoint.getBlockZ());
        if (isAcceptableBiome(biome) && treeCanGrow(random)) {
            String treeType = simplifyBiome(biome).name();

            File treeFile = new File(plugin.getDataFolder(), "biome." + treeType + ".xml");
            File rootFile = new File(plugin.getDataFolder(), "biome." + treeType + ".root.xml");

            TreeRenderer renderer = new TreeRenderer(plugin);
            renderer.renderTree(refPoint, treeFile, rootFile, false, random.nextInt());
        }
    }

    private boolean isAcceptableBiome(Biome biome) {
        return biome == Biome.FOREST ||
               biome == Biome.FOREST_HILLS ||
               biome == Biome.BIRCH_FOREST ||
               biome == Biome.BIRCH_FOREST_HILLS_MOUNTAINS ||
               biome == Biome.BIRCH_FOREST_MOUNTAINS ||
               biome == Biome.SWAMPLAND ||
               biome == Biome.SWAMPLAND_MOUNTAINS ||
               biome == Biome.JUNGLE ||
               biome == Biome.JUNGLE_HILLS ||
               biome == Biome.JUNGLE_MOUNTAINS ||
               biome == Biome.ROOFED_FOREST ||
               biome == Biome.ROOFED_FOREST_MOUNTAINS;
    }

    private Biome simplifyBiome(Biome biome) {
        switch (biome) {
            case FOREST:
            case FOREST_HILLS:
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
}
