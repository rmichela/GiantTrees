package com.ryanmichela.trees.rendering;

import net.minecraft.server.v1_7_R1.ChunkSection;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R1.util.CraftMagicNumbers;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2014 Ryan Michela
 */
public class WorldChangeTracker {
    private Map<WorldChangeKey, WorldChange> changes = new HashMap<WorldChangeKey, WorldChange>(1000);


    public void addChange(Vector location, Material material, byte materialData, boolean overwrite) {
        int blockY = location.getBlockY();
        if (blockY > 255 || blockY < 0) {
            return;
        }

        WorldChangeKey key = new WorldChangeKey(location.getBlockX(), blockY, location.getBlockZ());
        if (changes.containsKey(key)) {
            if (overwrite) {
                changes.put(key, new WorldChange(location, material, materialData));
            }
        } else {
            changes.put(key, new WorldChange(location, material, materialData));
        }
    }

    public void applyChanges(Location refPoint) {
        Set<Chunk> touchedChunks = new HashSet<Chunk>();

        for (WorldChange change : changes.values()) {
            Location changeLoc = refPoint.clone().add(change.location);
            Block block = changeLoc.getBlock();
            setBlockFast(block, change.material, change.materialData);
            touchedChunks.add(block.getChunk());
        }

        for (Chunk chunk : touchedChunks) {
            recalculateLight(chunk);
            refPoint.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
        }
    }

    private void setBlockFast(Block b, Material material, byte data) {
        Chunk c = b.getChunk();
        int cx = b.getX() & 0xF;
        int cy = b.getY() & 15;
        int cz = b.getZ() & 0xF;

        net.minecraft.server.v1_7_R1.Chunk nmsChunk = ((CraftChunk) c).getHandle();
        ChunkSection[] chunkSections = nmsChunk.i();
        ChunkSection cs = chunkSections[b.getY() >> 4];
        if (cs == null) {
            cs = chunkSections[b.getY() >> 4] = new ChunkSection(b.getY() >> 4 << 4, !nmsChunk.world.worldProvider.f);
        }
        cs.setTypeId(cx, cy, cz, CraftMagicNumbers.getBlock(material));
        cs.setData(cx, cy, cz, data);
    }

    private void recalculateLight(Chunk c) {
        net.minecraft.server.v1_7_R1.Chunk nmsChunk = ((CraftChunk) c).getHandle();
        nmsChunk.initLighting();
    }
}
