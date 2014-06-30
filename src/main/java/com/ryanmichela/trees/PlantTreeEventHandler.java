package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.TreeRenderer;
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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class PlantTreeEventHandler implements Listener{
    private Plugin plugin;
    private PhysicalCraftingRecipe recipe;
    private TreeRenderer renderer;
    private PopupHandler popup;
    private boolean enabled;
    private int boneMealConsumed;

    public PlantTreeEventHandler(Plugin plugin) {
        this.plugin = plugin;
        this.renderer = new TreeRenderer(plugin);
        this.popup = new PopupHandler(plugin);

        try {
            // Read the raw config
            ConfigurationSection patternSection = plugin.getConfig().getConfigurationSection("planting-pattern");
            List<String> rows = patternSection.getStringList("pattern");
            ConfigurationSection materialsSection = patternSection.getConfigurationSection("materials");
            Map<String, Object> configMaterialMap = materialsSection.getValues(false);

            Map<Character, String> materialDataMap = new HashMap<Character, String>();
            for (Map.Entry<String, Object> kvp : configMaterialMap.entrySet()) {
                materialDataMap.put(kvp.getKey().charAt(0), (String) kvp.getValue());
            }

            boneMealConsumed = patternSection.getInt("bone-meal-consumed");

            this.recipe = PhysicalCraftingRecipe.fromStringRepresentation(rows.toArray(new String[]{}), materialDataMap);
            if (!recipe.usedMaterials.contains(Material.SAPLING)) {
                throw new Exception();
            }

            enabled = true;
        } catch (Exception e) {
            plugin.getLogger().severe("The planting-pattern config section is invalid! Disabling survival planting of giant trees.");
            enabled = false;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!enabled) return;
        if (!event.getPlayer().hasPermission("gianttrees.grow")) return;

        ItemStack itemInHand = event.getItem();
        Block clickedBlock = event.getClickedBlock();
        if (itemInHand == null || clickedBlock == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemCost cost = new ItemCost(Material.INK_SACK, (short)15, boneMealConsumed);
        if (cost.isAffordable(event.getPlayer()) && stackIsCorrect(itemInHand)) {
            if (recipe.matches(clickedBlock)) {
                String treeType = identifyTree(clickedBlock);
                Random seed = new Random(clickedBlock.getWorld().getSeed());
                File treeFile = new File(plugin.getDataFolder(), "tree." + treeType + ".xml");
                File rootFile = new File(plugin.getDataFolder(), "tree." + treeType + ".root.xml");
                cost.apply(event.getPlayer());
                popup.sendPopup(event.getPlayer(), "Stand back!");

                renderer.renderTree(clickedBlock.getLocation(), treeFile, rootFile, true, seed.nextInt());
            }
        }
    }

    private boolean stackIsCorrect(ItemStack inHand) {
        return inHand != null &&
               inHand.getType() == Material.INK_SACK &&
               inHand.getData().getData() == 15;
    }

    private String identifyTree(Block block) {
        if (block.getType() != Material.SAPLING) {
            throw new IllegalArgumentException();
        }
        if (block.getData() == 0) return "OAK";
        if (block.getData() == 1) return "SPRUCE";
        if (block.getData() == 2) return "BIRCH";
        if (block.getData() == 3) return "JUNGLE";
        if (block.getData() == 4) return "ACACIA";
        if (block.getData() == 5) return "DARK_OAK";
        return "OAK";
    }
}
