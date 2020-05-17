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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ImmutableList;

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
    }
    // unpack basic trees
    for (final String biome : ImmutableList.of("BIRCH_FOREST", "FOREST",
                                               "JUNGLE", "ROOFED_FOREST",
                                               "SAVANNA", "TAIGA")) {
      ensureTreeFileExists("biome." + biome);
    }
    for (final String tree : ImmutableList.of("ACACIA", "BIRCH", "DARK_OAK",
                                              "JUNGLE", "OAK", "SPRUCE")) {
      ensureTreeFileExists("tree." + tree);
    }
  }

  private void ensureTreeFileExists(String filePrefix) {
    String treeFile = filePrefix + ".xml";
    String treeRootFile = filePrefix + ".root.xml";
    // Check before creation, to silence meaningless warnings
    if (!new File(this.getDataFolder(), treeFile).exists()) {
      this.saveResource(treeFile, true);
    }
    if (!new File(this.getDataFolder(), treeRootFile).exists()) {
      this.saveResource(treeRootFile, true);
    }
  }

}
