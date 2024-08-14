/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record HeightBlock(int level, @NotNull ExBlock block, @Nullable HeightBlock next,
                          @NotNull List<BreakableBlock> blocksToBreak,
                          @NotNull List<BreakableBlock> blocksToBreakForNext) {

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
