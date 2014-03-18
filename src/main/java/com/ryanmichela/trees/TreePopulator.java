package com.ryanmichela.trees;

import com.ryanmichela.trees.rendering.Draw3d;
import com.ryanmichela.trees.rendering.MinecraftExporter;
import net.sourceforge.arbaro.tree.Tree;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;

/**
 * Copyright 2014 Ryan Michela
 */
public class TreePopulator extends BlockPopulator {
    private Plugin plugin;

    public TreePopulator(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        try {
            File treeFile = new File(plugin.getDataFolder(), "tree.xml");
            InputStream treeStream = new FileInputStream(treeFile);

            Tree tree = new Tree();
            tree.setOutputType(Tree.CONES);
            tree.readFromXML(treeStream);
            tree.params.Seed = random.nextInt();
            tree.params.stopLevel = -1;
            tree.params.verbose = true;
            tree.make();

            Location refPoint = new Location(world, chunk.getX() * 16 + 8, 64, chunk.getZ() * 16 + 8);
            refPoint.setY(world.getHighestBlockYAt(refPoint));

            MinecraftExporter treeExporter = new MinecraftExporter(tree, new Draw3d(refPoint));
            treeExporter.write();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
