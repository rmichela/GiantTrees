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

import com.google.common.collect.Lists;
import com.ryanmichela.trees.cost.ItemCost;

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
  private List<PhysicalCraftingRecipe> recipes;
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

      final Map<Character, String> materialDataMap = new HashMap<>();
      for (final Map.Entry<String, Object> kvp : configMaterialMap.entrySet()) {
        materialDataMap.put(kvp.getKey().charAt(0), (String) kvp.getValue());
      }

      if (materialDataMap.values().stream().noneMatch(s -> s.equals("SAPLING"))) {
        throw new Exception("Must have at least one 'SAPLING' in the recipe.");
      }

      boneMealConsumed = patternSection.getInt("bone-meal-consumed");

      // Create a PhysicalCraftingRecipe for each kind of sapling
      List<Material> saplings = Lists.newArrayList(Material.OAK_SAPLING, Material.SPRUCE_SAPLING, Material.BIRCH_SAPLING, Material.JUNGLE_SAPLING, Material.ACACIA_SAPLING, Material.DARK_OAK_SAPLING);

      for (Material sapling : saplings) {
        Map<Character, String> patchedMaterialDataMap = new HashMap<>(materialDataMap);
        for (Character c : patchedMaterialDataMap.keySet()) {
          if (patchedMaterialDataMap.get(c).equals("SAPLING")) {
            patchedMaterialDataMap.put(c, sapling.name());
          }
        }

        recipes.add(PhysicalCraftingRecipe.fromStringRepresentation(rows.toArray(new String[] {}),
                patchedMaterialDataMap));
      }

      enabled = true;
    } catch (final Exception e) {
      plugin.getLogger().severe("The planting-pattern config section is invalid! Disabling survival planting of giant trees. " + e.getMessage());
      enabled = false;
    }
  }

  @EventHandler
  public void onPlayerInteract(final PlayerInteractEvent event) {
    if (!enabled) { return; }
    if (!event.getPlayer().hasPermission("gianttrees.grow")) { return; }

    final ItemStack itemInHand = event.getItem();
    final Block clickedBlock = event.getClickedBlock();
    if ((itemInHand == null) || (clickedBlock == null)
        || (event.getAction() != Action.RIGHT_CLICK_BLOCK)) { return; }

    final ItemCost cost = new ItemCost(Material.INK_SAC, (short) 15,
                                       boneMealConsumed);

    for (PhysicalCraftingRecipe recipe : recipes) {
      if (cost.isAffordable(event.getPlayer()) && stackIsCorrect(itemInHand)
              && recipe.matches(clickedBlock)) {
        final String treeType = identifyTree(clickedBlock);
        final Random seed = new Random(clickedBlock.getWorld().getSeed());
        final File treeFile = new File(plugin.getDataFolder(), "tree."
                + treeType
                + ".xml");
        final File rootFile = new File(plugin.getDataFolder(), "tree."
                + treeType
                + ".root.xml");
        event.setCancelled(true);
        cost.apply(event.getPlayer());
        popup.sendPopup(event.getPlayer(), "Stand back!");

        renderer.renderTree(clickedBlock.getLocation(), treeFile, rootFile,
                seed.nextInt(), true);
      }
    }
  }

  private String identifyTree(final Block block) {
    switch (block.getType()) {
      case OAK_SAPLING: return "OAK";
      case SPRUCE_SAPLING: return "SPRUCE";
      case BIRCH_SAPLING: return "BIRCH";
      case JUNGLE_SAPLING: return "JUNGLE";
      case ACACIA_SAPLING: return "ACACIA";
      case DARK_OAK_SAPLING: return "DARK_OAK";
      default: throw new IllegalArgumentException("Unknown sapling kind");
    }
  }

  private boolean stackIsCorrect(final ItemStack inHand) {
    return (inHand != null) && (inHand.getType() == Material.INK_SAC);
  }
}
