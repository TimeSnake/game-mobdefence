/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.*;

public class HeightMapGenerator {

  public static final int MAX_LEVEL = 400;

  private final Logger logger;

  private final ExecutorService executorService = new ThreadPoolExecutor(8, 16,
      10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
  private final CompletionService<Collection<HeightBlock>> completionService =
      new ExecutorCompletionService<>(this.executorService);

  private final String name;

  private final ExLocation startLocation;
  private final PathCostCalc pathCostCalc;

  private boolean running = false;

  private Future<Collection<HeightBlock>> currentFuture;
  private final Set<ExBlock> seenBlocks = ConcurrentHashMap.newKeySet();

  public HeightMapGenerator(String name, ExLocation startLocation, PathCostCalc pathCostCalc) {
    this.name = name;
    this.logger = LogManager.getLogger("mob-def.height-map.generator." + this.name);
    this.startLocation = startLocation;
    this.pathCostCalc = pathCostCalc;
  }

  public Future<Collection<HeightBlock>> invokeUpdate() {
    if (currentFuture == null || currentFuture.isDone()) {
      this.currentFuture = this.executorService.submit(this::update);
    }
    return this.currentFuture;
  }

  public Collection<HeightBlock> update() throws ExecutionException, InterruptedException {
    if (this.running) {
      return currentFuture.get();
    }
    this.running = true;

    this.logger.info("Updating heightmap...");

    this.seenBlocks.clear();

    Set<HeightBlock> heightBlocks = ConcurrentHashMap.newKeySet();
    Map<Integer, List<HeightBlock>> blocksByHeight = new ConcurrentHashMap<>();

    HeightBlock core = new HeightBlock(0, this.startLocation.getExBlock(), null, List.of(), List.of());
    blocksByHeight.put(0, List.of(core));
    heightBlocks.add(core);

    for (int level = 1; !blocksByHeight.isEmpty() && level < MAX_LEVEL; level++) {
      List<HeightBlock> heightBlockCenters = blocksByHeight.remove(level - 1);
      if (heightBlockCenters == null) {
        continue;
      }

      List<Future<Collection<HeightBlock>>> futures = new ArrayList<>();

      for (BlockArea blockArea : BlockArea.AREAS) {
        for (HeightBlock center : heightBlockCenters) {
          int finalLevel = level;
          futures.add(this.completionService.submit(() -> this.getReachableByBlocks(finalLevel, center, blockArea)));
        }
      }
      while (!futures.isEmpty()) {
        Future<Collection<HeightBlock>> future = this.completionService.take();
        futures.remove(future);
        Collection<HeightBlock> blocks = future.get();

        heightBlocks.addAll(blocks);
        blocks.forEach(b -> blocksByHeight.computeIfAbsent(b.level(), k -> new ArrayList<>()).add(b));
      }
    }

    this.logger.info("Updated heightmap: {} blocks", heightBlocks.size());
    this.running = false;

    return heightBlocks;
  }

  private List<HeightBlock> getReachableByBlocks(int level, HeightBlock end, BlockArea blockArea) {
    List<HeightBlock> blocks = new ArrayList<>();

    for (Vector vector : blockArea.getVectors()) {
      ExBlock startBlock = end.block().getRelative(vector);

      if (this.seenBlocks.contains(startBlock)) {
        continue;
      }

      if (startBlock.getLocation().distanceSquared(end.block().getLocation()) > 2) {
        continue;
      }

      PathCostResult result = this.pathCostCalc.apply(new ShortPath(startBlock, end.block(),
          end.block().getY() - startBlock.getY()));

      if (result.isBlocked() || level + result.costs() > MAX_LEVEL) {
        continue;
      }

      this.seenBlocks.add(startBlock);
      blocks.add(new HeightBlock(level + result.costs(), startBlock,
          end, result.getBlocksToBreakOnStart(), result.getBlocksToBreakToNext()));
    }
    return blocks;
  }

  private enum BlockArea {

    STRAIGHT(List.of(new Vector(-1, 0, 0), new Vector(1, 0, 0), new Vector(0, 0, -1),
        new Vector(0, 0, 1))),
    DIAGONAL(List.of(new Vector(-1, 0, -1), new Vector(-1, 0, 1), new Vector(1, 0, -1),
        new Vector(1, 0, 1),
        new Vector(-1, -1, 0), new Vector(-1, 1, 0), new Vector(1, -1, 0),
        new Vector(1, 1, 0),
        new Vector(0, -1, -1), new Vector(0, -1, 1), new Vector(0, 1, -1),
        new Vector(0, 1, 1))),
    DOUBLE_DIAGONAL(
        List.of(new Vector(-1, -1, -1), new Vector(-1, -1, 1), new Vector(-1, 1, -1),
            new Vector(-1,
                1, 1),
            new Vector(1, -1, -1), new Vector(1, -1, 1), new Vector(1, 1, -1),
            new Vector(1, 1, 1)));

    private final List<Vector> vectors;

    BlockArea(List<Vector> vectors) {
      this.vectors = vectors;
    }

    public List<Vector> getVectors() {
      return vectors;
    }

    static final BlockArea[] AREAS = {STRAIGHT, DIAGONAL};
  }


}
