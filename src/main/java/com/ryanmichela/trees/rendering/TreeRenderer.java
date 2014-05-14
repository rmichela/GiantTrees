package com.ryanmichela.trees.rendering;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import net.sourceforge.arbaro.params.AbstractParam;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.Tree;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreeRenderer {
    private Plugin plugin;

    public TreeRenderer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void RenderTree(Location refPoint, File treeFile, File rootFile, boolean applyLight, int seed) {
        try {

            AbstractParam.loading = true;


            CraftMassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(plugin, refPoint.getWorld());
            massBlockUpdate.setRelightingStrategy(applyLight ? MassBlockUpdate.RelightingStrategy.HYBRID : MassBlockUpdate.RelightingStrategy.NEVER);
            massBlockUpdate.setMaxRelightTimePerTick(100, TimeUnit.MILLISECONDS);
            WorldChangeTracker changeTracker = new WorldChangeTracker(massBlockUpdate);

            plugin.getLogger().info("Rendering tree " + treeFile.getName());
            Tree tree = loadTree(treeFile, seed, true);
            TreeType treeType = new TreeType(tree.params.WoodType);
            Draw3d d3d = new Draw3d(refPoint, tree.params.Smooth, treeType, changeTracker, Draw3d.RenderOrientation.NORMAL);

            MinecraftExporter treeExporter = new MinecraftExporter(tree, d3d);
            treeExporter.write();
            d3d.drawRootJunction(d3d.toMcVector(((Segment)((Stem) tree.trunks.get(0)).stemSegments().nextElement()).posFrom()), ((Stem)tree.trunks.get(0)).baseRadius);

            if (rootFile != null) {
                plugin.getLogger().info("Rendering root " + rootFile.getName());
                Tree root = loadTree(rootFile, seed, false);
                TreeType rootType = new TreeType(root.params.WoodType);
                Draw3d d3dInverted = new Draw3d(refPoint, root.params.Smooth, rootType, changeTracker, Draw3d.RenderOrientation.INVERTED);

                MinecraftExporter treeExporterInverted = new MinecraftExporter(root, d3dInverted);
                treeExporterInverted.write();
            }

            d3d.applyChanges();
            AbstractParam.loading = false;
        } catch (Exception e) {
            plugin.getLogger().severe("Error rendering tree: " + e.getMessage());
        }
    }

    private Tree loadTree(File treeFile, int seed, boolean withLeaves) throws Exception {
        if (treeFile == null) return null;

        Tree tree = new Tree();
        tree.setOutputType(Tree.CONES);
        tree.readFromXML(new FileInputStream(treeFile));
        tree.params.Seed = seed;
        tree.params.stopLevel = -1; // -1 for everything
        tree.params.verbose = false;
        tree.params.Leaves = withLeaves ? 0 : -1;
        tree.make();
        return tree;
    }
}
