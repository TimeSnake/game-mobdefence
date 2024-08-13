/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;

import java.util.List;
import java.util.Objects;

public record HeightBlock(int level, ExBlock block, HeightBlock next,
                          List<BreakableBlock> blocksToBreak,
                          List<BreakableBlock> blocksToBreakForNext) {

  public HeightBlock(int level, ExBlock block, HeightBlock next, List<BreakableBlock> blocksToBreak,
                     List<BreakableBlock> blocksToBreakForNext) {
    this.level = level;
    this.block = block;
    this.next = next;
    this.blocksToBreak = blocksToBreak;
    this.blocksToBreakForNext = blocksToBreakForNext;
  }

  public boolean hasNext() {
    return this.next != null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeightBlock that = (HeightBlock) o;
    return Objects.equals(block, that.block);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(block);
  }
}
