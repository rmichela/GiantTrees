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

    @Override
    public void onDisable() {
        super.onDisable();
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
