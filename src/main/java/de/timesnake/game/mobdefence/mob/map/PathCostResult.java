/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PathCostResult implements Comparable<PathCostResult> {

  public static final PathCostResult BLOCKED = new PathCostResult(-1, List.of(), List.of());
  public static final PathCostResult EMPTY = new PathCostResult(0, List.of(), List.of());

  private int costs;
  private final List<BreakableBlock> blocksToBreakOnStart;
  private final List<BreakableBlock> blocksToBreakToNext;

  public PathCostResult() {
    this(0, new ArrayList<>(), new ArrayList<>());
  }

  public PathCostResult(int costs, List<BreakableBlock> blocksToBreakOnStart,
                        List<BreakableBlock> blocksToBreakToNext) {
    this.costs = costs;
    this.blocksToBreakOnStart = blocksToBreakOnStart.stream().filter(b -> b.costs() > 0).collect(Collectors.toList());
    this.blocksToBreakToNext = blocksToBreakToNext.stream().filter(b -> b.costs() > 0).collect(Collectors.toList());
  }

  public void addCosts(int costs) {
    this.costs += costs;
  }

  public void addBlockToBreakOnStart(Integer costs, ExBlock block) {
    if (costs == null) {
      throw new BlockedPathException();
    }
    if (costs <= 0) {
      return;
    }
    this.addBlockToBreakOnStart(new BreakableBlock(costs, block));
  }

  public void addBlockToBreakOnStart(BreakableBlock block) {
    if (this.blocksToBreakOnStart.contains(block) || block.costs() <= 0) {
      return;
    }
    this.costs += block.costs();
    this.blocksToBreakOnStart.add(block);
  }

  public void addBlockToBreakToNext(Integer costs, ExBlock block) {
    if (costs == null) {
      throw new BlockedPathException();
    }
    if (costs <= 0) {
      return;
    }
    this.addBlockToBreakToNext(new BreakableBlock(costs, block));
  }

  public void addBlockToBreakToNext(BreakableBlock block) {
    if (this.blocksToBreakToNext.contains(block) || block.costs() <= 0) {
      return;
    }
    this.costs += block.costs();
    this.blocksToBreakToNext.add(block);
  }

  public List<BreakableBlock> getBlocksToBreakOnStart() {
    return blocksToBreakOnStart;
  }

  public List<BreakableBlock> getBlocksToBreakToNext() {
    return blocksToBreakToNext;
  }

  public PathCostResult merge(PathCostResult other) {
    return new PathCostResult(costs + other.costs, // TODO
        Stream.concat(blocksToBreakOnStart.stream(), other.blocksToBreakOnStart.stream()).distinct().toList(),
        Stream.concat(blocksToBreakToNext.stream(), other.blocksToBreakToNext.stream()).distinct().toList());
  }

  public boolean isBlocked() {
    return this.costs == -1;
  }

  public boolean isEmpty() {
    return this.costs == 0;
  }

  @Override
  public int compareTo(@NotNull PathCostResult o) {
    if (this.costs == -1) {
      return 1;
    }
    if (o.costs == -1) {
      return -1;
    }
    return Integer.compare(this.costs, o.costs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PathCostResult that = (PathCostResult) o;
    return costs == that.costs
           && Objects.equals(blocksToBreakOnStart, that.blocksToBreakOnStart)
           && Objects.equals(blocksToBreakToNext, that.blocksToBreakToNext);
  }

  @Override
  public int hashCode() {
    return Objects.hash(costs, blocksToBreakOnStart, blocksToBreakToNext);
  }

  public int costs() {
    return costs;
  }
}
