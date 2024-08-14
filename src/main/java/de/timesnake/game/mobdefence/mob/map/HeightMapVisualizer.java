/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.server.ColorConverter;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeightMapVisualizer {

  private static final int Y_OFFSET = 70;

  private final Set<Block> lastBlocks = new HashSet<>();

  public HeightMapVisualizer() {
  }

  public void clear() {
    for (Block block : lastBlocks) {
      block.setType(Material.AIR);
    }
  }

  public void visualize(HeightMap heightMap) {
    ExWorld world = heightMap.getCoreLocation().getExWorld();
    Material color = Material.WHITE_WOOL;

    this.lastBlocks.clear();

    for (int level = 0; level <= HeightMapGenerator.MAX_LEVEL; level++) {
      List<HeightBlock> heightBlocks = heightMap.getBlocksByHeight().getOrDefault(level, List.of());
      for (HeightBlock heightBlock : heightBlocks) {
        final Block block = world.getBlockAt(heightBlock.block().getLocation().clone().add(0, Y_OFFSET, 0));
        block.setType(color);
        this.lastBlocks.add(block);
      }
      color = ColorConverter.colorIterator2(color);
    }
  }
}
