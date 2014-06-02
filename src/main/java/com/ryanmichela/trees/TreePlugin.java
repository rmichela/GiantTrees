package com.ryanmichela.trees;

import me.desht.dhutils.nms.NMSHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
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
        // unpack basic trees
        saveResource("biome.BIRCH_FOREST.xml", false);
        saveResource("biome.BIRCH_FOREST.root.xml", false);
        saveResource("biome.FOREST.xml", false);
        saveResource("biome.FOREST.root.xml", false);
        saveResource("biome.JUNGLE.xml", false);
        saveResource("biome.JUNGLE.root.xml", false);
        saveResource("biome.ROOFED_FOREST.xml", false);
        saveResource("biome.ROOFED_FOREST.root.xml", false);
        saveResource("biome.SWAMPLAND.xml", false);
        saveResource("biome.SWAMPLAND.root.xml", false);
        saveResource("tree.ACACIA.xml", false);
        saveResource("tree.ACACIA.root.xml", false);
        saveResource("tree.BIRCH.xml", false);
        saveResource("tree.BIRCH.root.xml", false);
        saveResource("tree.DARK_OAK.xml", false);
        saveResource("tree.DARK_OAK.root.xml", false);
        saveResource("tree.JUNGLE.xml", false);
        saveResource("tree.JUNGLE.root.xml", false);
        saveResource("tree.OAK.xml", false);
        saveResource("tree.OAK.root.xml", false);
        saveResource("tree.SPRUCE.xml", false);
        saveResource("tree.SPRUCE.root.xml", false);
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
            getServer().getPluginManager().registerEvents(new PlantTreeEventHandler(this), this);
            // attach to worlds automatically when onlyUseWorldManagers is false
            if (!getConfig().getBoolean("naturallyGrowTrees", true)) {
                getServer().getPluginManager().registerEvents(new WorldInitListener(), this);
            }
        } catch (Exception e) {
            getLogger().severe("Failed to initialize plugin: " + e.getMessage());
        }

        getCommand("tree-edit").setExecutor(new EditTreeCommand(this));
        getCommand("tree-create").setExecutor(new CreateTreeCommand(this));
    }

    private class WorldInitListener implements Listener {
        @EventHandler
        public void onWorldInit(WorldInitEvent event) {
            for(String worldName : getConfig().getStringList("worlds")) {
                if (worldName.equals(event.getWorld().getName())) {
                    getLogger().info("Attaching giant tree populator to world \"" + event.getWorld().getName() + "\"");
                    event.getWorld().getPopulators().add(new TreePopulator(TreePlugin.this));
                    return;
                }
            }
        }
    }
}
