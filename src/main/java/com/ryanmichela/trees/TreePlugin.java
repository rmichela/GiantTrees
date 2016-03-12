package com.ryanmichela.trees;

import java.io.IOException;

import me.desht.dhutils.nms.NMSHelper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreePlugin extends JavaPlugin {

  private class WorldInitListener implements Listener {

    @EventHandler
    public void onWorldInit(final WorldInitEvent event) {
      for (final String worldName : TreePlugin.this.getConfig()
                                                   .getStringList("worlds")) {
        if (worldName.equals(event.getWorld().getName())) {
          TreePlugin.this.getLogger()
                         .info("Attaching giant tree populator to world \""
                                   + event.getWorld().getName() + "\"");
          event.getWorld().getPopulators()
               .add(new TreePopulator(TreePlugin.this));
          return;
        }
      }
    }
  }

  @Override
  public void onEnable() {
    try {
      final Metrics metrics = new Metrics(this);
      metrics.start();
    } catch (final IOException e) {
      // Failed to submit the stats :-(
    }

    try {
      NMSHelper.init(this, true);
      this.getServer().getPluginManager()
          .registerEvents(new PlantTreeEventHandler(this), this);
      // attach to worlds automatically when onlyUseWorldManagers is false
      if (this.getConfig().getBoolean("naturallyGrowTrees", true)) {
        this.getServer().getPluginManager()
            .registerEvents(new WorldInitListener(), this);
      }
    } catch (final Exception e) {
      this.getLogger().severe("Failed to initialize plugin: " + e.getMessage());
    }

    if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
      this.getLogger()
          .warning("WorldEdit not installed. Undo capability disabled.");
    }

    this.getCommand("tree-edit").setExecutor(new EditTreeCommand(this));
    this.getCommand("tree-create").setExecutor(new CreateTreeCommand(this));
  }

  @Override
  public void onLoad() {
    if (!this.getDataFolder().exists()) {
      this.saveDefaultConfig();
      // unpack basic trees
      this.saveResource("biome.BIRCH_FOREST.xml", false);
      this.saveResource("biome.BIRCH_FOREST.root.xml", false);
      this.saveResource("biome.FOREST.xml", false);
      this.saveResource("biome.FOREST.root.xml", false);
      this.saveResource("biome.JUNGLE.xml", false);
      this.saveResource("biome.JUNGLE.root.xml", false);
      this.saveResource("biome.ROOFED_FOREST.xml", false);
      this.saveResource("biome.ROOFED_FOREST.root.xml", false);
      // saveResource("biome.SWAMPLAND.xml", false);
      // saveResource("biome.SWAMPLAND.root.xml", false);
      this.saveResource("tree.ACACIA.xml", false);
      this.saveResource("tree.ACACIA.root.xml", false);
      this.saveResource("tree.BIRCH.xml", false);
      this.saveResource("tree.BIRCH.root.xml", false);
      this.saveResource("tree.DARK_OAK.xml", false);
      this.saveResource("tree.DARK_OAK.root.xml", false);
      this.saveResource("tree.JUNGLE.xml", false);
      this.saveResource("tree.JUNGLE.root.xml", false);
      this.saveResource("tree.OAK.xml", false);
      this.saveResource("tree.OAK.root.xml", false);
      this.saveResource("tree.SPRUCE.xml", false);
      this.saveResource("tree.SPRUCE.root.xml", false);
    }
  }
}
