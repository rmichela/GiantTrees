package com.ryanmichela.trees.rendering;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import net.sourceforge.arbaro.params.AbstractParam;
import net.sourceforge.arbaro.tree.Segment;
import net.sourceforge.arbaro.tree.Stem;
import net.sourceforge.arbaro.tree.Tree;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreeRenderer {

  private final Plugin plugin;

  public TreeRenderer(final Plugin plugin) {
    this.plugin = plugin;
  }

  public void renderTree(final Location refPoint, final File treeFile,
                         final File rootFile, final int seed,
                         final boolean withDelay) {
    this.renderTree(refPoint, treeFile, rootFile, seed, false, null, withDelay);
  }

  public void renderTreeWithHistory(final Location refPoint,
                                    final File treeFile, final File rootFile,
                                    final int seed, final Player forPlayer,
                                    final boolean withDelay) {
    this.renderTree(refPoint, treeFile, rootFile, seed, true, forPlayer,
                    withDelay);
  }

  private Tree loadTree(final File treeFile, final int seed) throws Exception {
    if (treeFile == null) { return null; }

    final Tree tree = new Tree();
    tree.setOutputType(Tree.CONES);
    tree.readFromXML(new FileInputStream(treeFile));
    tree.params.Seed = seed;
    tree.params.stopLevel = -1; // -1 for everything
    tree.params.verbose = false;
    return tree;
  }

  private void logVerbose(final String message) {
    if (this.plugin.getConfig().getBoolean("verbose-logging", false)) {
      this.plugin.getLogger().info(message);
    }
  }

  private void renderTree(final Location refPoint, final File treeFile,
                          final File rootFile, final int seed,
                          final boolean recordHistory, final Player forPlayer,
                          final boolean withDelay) {
    AbstractParam.loading = true;

    final CraftMassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(
                                                                          this.plugin,
                                                                          refPoint.getWorld());
    final MassBlockUpdate.RelightingStrategy relightingStrategy = this.plugin.getConfig()
                                                                             .getBoolean("SIMPLE_LIGHTING",
                                                                                         false) ? MassBlockUpdate.RelightingStrategy.NEVER
                                                                                               : MassBlockUpdate.RelightingStrategy.HYBRID;
    massBlockUpdate.setRelightingStrategy(relightingStrategy);
    final WorldChangeTracker changeTracker = new WorldChangeTracker(
                                                                    this.plugin,
                                                                    massBlockUpdate,
                                                                    relightingStrategy,
                                                                    recordHistory);

    this.plugin.getServer().getScheduler()
               .runTaskAsynchronously(this.plugin, new Runnable() {

                 @Override
                 public void run() {
                   try {
                     TreeRenderer.this.logVerbose("Rendering tree "
                                                  + treeFile.getName());
                     final Tree tree = TreeRenderer.this.loadTree(treeFile,
                                                                  seed);
                     tree.make();
                     final TreeType treeType = new TreeType(
                                                            tree.params.WoodType);
                     final Draw3d d3d = new Draw3d(
                                                   refPoint,
                                                   tree.params.Smooth,
                                                   treeType,
                                                   changeTracker,
                                                   Draw3d.RenderOrientation.NORMAL);

                     final MinecraftExporter treeExporter = new MinecraftExporter(
                                                                                  tree,
                                                                                  d3d);
                     treeExporter.write();

                     if (tree.params.WoodType.equals("Jungle")) {
                       JungleVinePopulator.populate(changeTracker,
                                                    new Random(seed));
                     }

                     d3d.drawRootJunction(d3d.toMcVector(((Segment) ((Stem) tree.trunks.get(0)).stemSegments()
                                                                                               .nextElement()).posFrom()),
                                          ((Stem) tree.trunks.get(0)).baseRadius);

                     if ((rootFile != null) && rootFile.exists()) {
                       TreeRenderer.this.logVerbose("Rendering root "
                                                    + rootFile.getName());
                       final Tree root = TreeRenderer.this.loadTree(rootFile,
                                                                    seed);
                       // Turn off leaves for roots and scale the roots the same
                       // as the tree
                       root.params.Leaves = -1;
                       root.params.scale_tree = tree.params.scale_tree;
                       root.make();
                       final TreeType rootType = new TreeType(
                                                              root.params.WoodType);
                       final Draw3d d3dInverted = new Draw3d(
                                                             refPoint,
                                                             root.params.Smooth,
                                                             rootType,
                                                             changeTracker,
                                                             Draw3d.RenderOrientation.INVERTED);

                       final MinecraftExporter treeExporterInverted = new MinecraftExporter(
                                                                                            root,
                                                                                            d3dInverted);
                       treeExporterInverted.write();
                     }
                     AbstractParam.loading = false;

                     final long generationDelay = withDelay ? TreeRenderer.this.plugin.getConfig()
                                                                                      .getInt("generation-delay",
                                                                                              0) * 20
                                                           : 0;

                     TreeRenderer.this.plugin.getServer()
                                             .getScheduler()
                                             .runTaskLater(TreeRenderer.this.plugin,
                                                           new Runnable() {

                                                             @Override
                                                             public void run() {
                                                               try {
                                                                 d3d.applyChanges(forPlayer);
                                                               } catch (final Exception e) {
                                                                 TreeRenderer.this.plugin.getLogger()
                                                                                         .severe("Error rendering tree: "
                                                                                                     + e.getMessage());
                                                               }
                                                             }
                                                           }, generationDelay);

                   } catch (final Exception e) {
                     TreeRenderer.this.plugin.getLogger()
                                             .severe("Error rendering tree: "
                                                         + e.getMessage());
                   }
                 }
               });
  }
}
