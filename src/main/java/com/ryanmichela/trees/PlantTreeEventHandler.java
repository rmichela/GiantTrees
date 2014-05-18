package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.TreeRenderer;
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

            Map<Character, Material> materialMap = new HashMap<Character, Material>();
            for (Map.Entry<String, Object> kvp : configMaterialMap.entrySet()) {
                materialMap.put(kvp.getKey().charAt(0), Material.matchMaterial((String)kvp.getValue()));
            }

            boneMealConsumed = patternSection.getInt("bone-meal-consumed");

            this.recipe = PhysicalCraftingRecipe.fromStringRepresentation(rows.toArray(new String[]{}), materialMap);

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

        if (stackIsCorrect(itemInHand)) {
            if (recipe.matches(clickedBlock)) {
                Random seed = new Random(clickedBlock.getWorld().getSeed());
                File treeFile = new File(plugin.getDataFolder(), "tree.xml");
                File rootFile = new File(plugin.getDataFolder(), "tree.root.xml");
                event.getPlayer().getInventory().remove(itemInHand);
                popup.sendPopup(event.getPlayer(), "Stand back!");

                renderer.RenderTree(clickedBlock.getLocation(), treeFile, rootFile, true, seed.nextInt());
            }
        }
    }

    private boolean stackIsCorrect(ItemStack inHand) {
        return inHand != null &&
               inHand.getType() == Material.INK_SACK &&
               inHand.getData().getData() == 15 &&
               inHand.getAmount() == 64;
    }
}
