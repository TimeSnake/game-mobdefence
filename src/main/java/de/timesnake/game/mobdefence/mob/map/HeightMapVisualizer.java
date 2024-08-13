/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.server.ColorConverter;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class HeightMapVisualizer {

  private static final int Y_OFFSET = 70;

  private final HeightMap heightMap;

  public HeightMapVisualizer(HeightMap heightMap) {
    this.heightMap = heightMap;
  }

  public void visualize() {
    ExWorld world = heightMap.getCoreLocation().getExWorld();
    Material color = Material.WHITE_WOOL;

    for (Map.Entry<Integer, List<HeightBlock>> entry : heightMap.getBlocksByHeight().entrySet()) {
      List<HeightBlock> heightBlocks = entry.getValue();
      for (HeightBlock heightBlock : heightBlocks) {
        world.getBlockAt(heightBlock.block().getLocation().clone().add(0, Y_OFFSET, 0)).setType(color);
      }
      color = ColorConverter.colorIterator2(color);
    }
  }
}
