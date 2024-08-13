/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;

import java.util.List;

public record HeightBlock(int level, ExLocation location, HeightBlock next,
                          List<BreakableBlock> blocksToBreak,
                          List<BreakableBlock> blocksToBreakForNext) {

  public HeightBlock(int level, ExLocation location, HeightBlock next, List<BreakableBlock> blocksToBreak,
                     List<BreakableBlock> blocksToBreakForNext) {

    this.level = level;
    this.location = location.getExBlock().getLocation();
    this.next = next;
    this.blocksToBreak = blocksToBreak;
    this.blocksToBreakForNext = blocksToBreakForNext;
  }

  public boolean hasNext() {
    return this.next != null;
  }
}
