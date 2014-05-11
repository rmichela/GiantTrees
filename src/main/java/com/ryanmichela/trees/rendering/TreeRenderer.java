package com.ryanmichela.trees.rendering;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import net.sourceforge.arbaro.params.AbstractParam;
import net.sourceforge.arbaro.tree.Tree;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreeRenderer {
    private Plugin plugin;

    public TreeRenderer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void RenderTree(Location refPoint, File treeFile, boolean applyLight, int seed) {
        try {
            InputStream treeStream = new FileInputStream(treeFile);

            AbstractParam.loading = true;
            Tree tree = new Tree();
            tree.setOutputType(Tree.CONES);
            tree.readFromXML(treeStream);
            tree.params.Seed = seed;
            tree.params.stopLevel = -1; // -1 for everything
            tree.params.verbose = false;
            tree.make();

            CraftMassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(plugin, refPoint.getWorld());
            massBlockUpdate.setRelightingStrategy(applyLight ? MassBlockUpdate.RelightingStrategy.HYBRID : MassBlockUpdate.RelightingStrategy.NEVER);
            massBlockUpdate.setMaxRelightTimePerTick(100, TimeUnit.MILLISECONDS);
            WorldChangeTracker changeTracker = new WorldChangeTracker(massBlockUpdate);
            TreeType treeType = new TreeType(tree.params.WoodType);
            Draw3d d3d = new Draw3d(refPoint, tree.params.Smooth, treeType, changeTracker);
            MinecraftExporter treeExporter = new MinecraftExporter(tree, d3d);
            treeExporter.write();
            d3d.applyChanges();
            AbstractParam.loading = false;
        } catch (Exception e) {
            plugin.getLogger().severe("Error rendering tree: " + e.getMessage());
        }
    }
}
