package com.ryanmichela.trees;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreePlugin extends JavaPlugin implements Listener {
    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem().getType() == Material.GOLD_HOE && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Chunk chunk = event.getClickedBlock().getChunk();
            World world = chunk.getWorld();
            Random seed = new Random();

            event.getPlayer().sendMessage("Let there be trees!");

            TreePopulator populator = new TreePopulator(this);
            populator.populate(world, seed, chunk);
        }
    }

    private Random getChunkSeed(Chunk chunk) {
        Random random = new Random();
        random.setSeed(chunk.getWorld().getSeed());
        long xRand = random.nextLong() / 2L * 2L + 1L;
        long zRand = random.nextLong() / 2L * 2L + 1L;
        random.setSeed((long) chunk.getX() * xRand + (long) chunk.getZ() * zRand ^ chunk.getWorld().getSeed());
        return random;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return super.getDefaultWorldGenerator(worldName, id);
    }
}
