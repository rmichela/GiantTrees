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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import me.desht.dhutils.cost.ItemCost;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.ryanmichela.trees.rendering.TreeRenderer;

public class PlantTreeEventHandler implements Listener {

  private int                    boneMealConsumed;
  private boolean                enabled;
  private final Plugin           plugin;
  private final PopupHandler     popup;
  private PhysicalCraftingRecipe recipe;
  private final TreeRenderer     renderer;

  public PlantTreeEventHandler(final Plugin plugin) {
    this.plugin = plugin;
    this.renderer = new TreeRenderer(plugin);
    this.popup = new PopupHandler(plugin);

    try {
      // Read the raw config
      final ConfigurationSection patternSection = plugin.getConfig()
                                                        .getConfigurationSection("planting-pattern");
      final List<String> rows = patternSection.getStringList("pattern");
      final ConfigurationSection materialsSection = patternSection.getConfigurationSection("materials");
      final Map<String, Object> configMaterialMap = materialsSection.getValues(false);

      final Map<Character, String> materialDataMap = new HashMap<Character, String>();
      for (final Map.Entry<String, Object> kvp : configMaterialMap.entrySet()) {
        materialDataMap.put(kvp.getKey().charAt(0), (String) kvp.getValue());
      }

      this.boneMealConsumed = patternSection.getInt("bone-meal-consumed");

      this.recipe = PhysicalCraftingRecipe.fromStringRepresentation(rows.toArray(new String[] {}),
                                                                    materialDataMap);
      if (!this.recipe.usedMaterials.contains(Material.SAPLING)) { throw new Exception(); }

      this.enabled = true;
    } catch (final Exception e) {
      plugin.getLogger()
            .severe("The planting-pattern config section is invalid! Disabling survival planting of giant trees.");
      this.enabled = false;
    }
  }

  @EventHandler
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (!this.enabled) { return; }
    if (!event.getPlayer().hasPermission("gianttrees.grow")) { return; }

    final ItemStack itemInHand = event.getItem();
    final Block clickedBlock = event.getClickedBlock();
    if ((itemInHand == null) || (clickedBlock == null)
        || (event.getAction() != Action.RIGHT_CLICK_BLOCK)) { return; }

    final ItemCost cost = new ItemCost(Material.INK_SACK, (short) 15,
                                       this.boneMealConsumed);
    if (cost.isAffordable(event.getPlayer()) && this.stackIsCorrect(itemInHand) && this.recipe.matches(clickedBlock)) {
      final String treeType = this.identifyTree(clickedBlock);
      final Random seed = new Random(clickedBlock.getWorld().getSeed());
      final File treeFile = new File(this.plugin.getDataFolder(), "tree."
                                                                  + treeType
                                                                  + ".xml");
      final File rootFile = new File(this.plugin.getDataFolder(),
                                     "tree." + treeType + ".root.xml");
      cost.apply(event.getPlayer());
      this.popup.sendPopup(event.getPlayer(), "Stand back!");

      this.renderer.renderTree(clickedBlock.getLocation(), treeFile,
                               rootFile, seed.nextInt(), true);
    }
  }

  private String identifyTree(final Block block) {
    if (block.getType() != Material.SAPLING) { throw new IllegalArgumentException(); }
    if (block.getData() == 0) { return "OAK"; }
    if (block.getData() == 1) { return "SPRUCE"; }
    if (block.getData() == 2) { return "BIRCH"; }
    if (block.getData() == 3) { return "JUNGLE"; }
    if (block.getData() == 4) { return "ACACIA"; }
    if (block.getData() == 5) { return "DARK_OAK"; }
    return "OAK";
  }

  private boolean stackIsCorrect(final ItemStack inHand) {
    return (inHand != null) && (inHand.getType() == Material.INK_SACK)
           && (inHand.getData().getData() == 15);
  }
}
