package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.TreeRenderer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class PlantTreeEventHandler implements Listener{
    private Plugin plugin;
    private PhysicalCraftingRecipe recipe;
    private TreeRenderer renderer;

    public PlantTreeEventHandler(Plugin plugin) {
        this.plugin = plugin;
        this.renderer = new TreeRenderer(plugin);

        String row1 = "SSSSS";
        String row2 = "SSESS";
        String row3 = "SSSSS";
        String[] rows = new String[]{row1, row2, row3};
        Map<Character, Material> materialMap = new HashMap<Character, Material>();
        materialMap.put('S', Material.SAPLING);
        materialMap.put('E', Material.EMERALD_BLOCK);
        this.recipe = PhysicalCraftingRecipe.fromStringRepresentation(rows, materialMap);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
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
