package com.ryanmichela.trees;

import me.desht.dhutils.nms.NMSHelper;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import java.io.IOException;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreePlugin extends JavaPlugin {
    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }

        try {
            NMSHelper.init(this, true);
            getServer().getPluginManager().registerEvents(new CreateTreeEventHandler(this), this);
        } catch (Exception e) {
            getLogger().severe("Failed to initialize plugin: " + e.getMessage());
        }

        getCommand("tree-edit").setExecutor(new EditTreeCommand(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return super.getDefaultWorldGenerator(worldName, id);
    }
}
