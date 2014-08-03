package com.ryanmichela.trees.rendering;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import net.sourceforge.arbaro.params.AbstractParam;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.Tree;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreeRenderer {
    private Plugin plugin;

    public TreeRenderer(Plugin plugin) {
        this.plugin = plugin;
    }

    public void renderTreeWithHistory(final Location refPoint, final File treeFile, final File rootFile, final int seed, Player forPlayer, boolean withDelay) {
        renderTree(refPoint, treeFile, rootFile, seed, true, forPlayer, withDelay);
    }

    public void renderTree(final Location refPoint, final File treeFile, final File rootFile, final int seed, boolean withDelay) {
        renderTree(refPoint, treeFile, rootFile, seed, false, null, withDelay);
    }

    private void renderTree(final Location refPoint, final File treeFile, final File rootFile, final int seed, final boolean recordHistory, final Player forPlayer, final boolean withDelay) {
        AbstractParam.loading = true;

        CraftMassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(plugin, refPoint.getWorld());
        MassBlockUpdate.RelightingStrategy relightingStrategy = MassBlockUpdate.RelightingStrategy.HYBRID;
        massBlockUpdate.setRelightingStrategy(relightingStrategy);
        final WorldChangeTracker changeTracker = new WorldChangeTracker(massBlockUpdate, relightingStrategy, recordHistory);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    logVerbose("Rendering tree " + treeFile.getName());
                    Tree tree = loadTree(treeFile, seed);
                    tree.make();
                    TreeType treeType = new TreeType(tree.params.WoodType);
                    final Draw3d d3d = new Draw3d(refPoint, tree.params.Smooth, treeType, changeTracker, Draw3d.RenderOrientation.NORMAL);

                    MinecraftExporter treeExporter = new MinecraftExporter(tree, d3d);
                    treeExporter.write();

                    if (tree.params.WoodType.equals("Jungle")) {
                        JungleVinePopulator.populate(changeTracker, new Random(seed));
                    }

                    d3d.drawRootJunction(d3d.toMcVector(((Segment)((Stem) tree.trunks.get(0)).stemSegments().nextElement()).posFrom()), ((Stem)tree.trunks.get(0)).baseRadius);

                    if (rootFile != null && rootFile.exists()) {
                        logVerbose("Rendering root " + rootFile.getName());
                        Tree root = loadTree(rootFile, seed);
                        // Turn off leaves for roots and scale the roots the same as the tree
                        root.params.Leaves = -1;
                        root.params.scale_tree = tree.params.scale_tree;
                        root.make();
                        TreeType rootType = new TreeType(root.params.WoodType);
                        Draw3d d3dInverted = new Draw3d(refPoint, root.params.Smooth, rootType, changeTracker, Draw3d.RenderOrientation.INVERTED);

                        MinecraftExporter treeExporterInverted = new MinecraftExporter(root, d3dInverted);
                        treeExporterInverted.write();
                    }
                    AbstractParam.loading = false;

                    long generationDelay = withDelay ? plugin.getConfig().getInt("generation-delay", 0) * 20 : 0;

                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int changeCount = d3d.applyChanges(forPlayer);
                                logVerbose("Affected blocks: " + changeCount);
                            } catch (Exception e) {
                                plugin.getLogger().severe("Error rendering tree: " + e.getMessage());
                            }
                        }
                    }, generationDelay);

                } catch (Exception e) {
                    plugin.getLogger().severe("Error rendering tree: " + e.getMessage());
                }
            }
        });
    }

    private Tree loadTree(File treeFile, int seed) throws Exception {
        if (treeFile == null) return null;

        Tree tree = new Tree();
        tree.setOutputType(Tree.CONES);
        tree.readFromXML(new FileInputStream(treeFile));
        tree.params.Seed = seed;
        tree.params.stopLevel = -1; // -1 for everything
        tree.params.verbose = false;
        return tree;
    }

    private void logVerbose(String message) {
        if (plugin.getConfig().getBoolean("verbose-logging", false)) {
            plugin.getLogger().info(message);
        }
    }
}
