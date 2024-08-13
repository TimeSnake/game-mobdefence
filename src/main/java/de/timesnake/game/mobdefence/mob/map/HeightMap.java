/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HeightMap {

  public static final int MAX_LEVEL = 400;

  private final Logger logger = LogManager.getLogger("mob-def.height-map");

  private final ExLocation coreLocation;

  private Map<ExLocation, HeightBlock> heightBlocksByLocation = new HashMap<>();
  private Map<Integer, List<HeightBlock>> blocksByHeight = new HashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Lock writeLock = lock.writeLock();
  private final Lock readLock = lock.readLock();

  private boolean running = false;
  private boolean update = false;
  private final Object updater = new Object();
  private final Thread heightLevelGeneratorThread;

  private final Random random = new Random();

  public HeightMap(ExLocation coreLocation, PathCostCalc pathCostCalc) {
    this.coreLocation = coreLocation;
    this.heightLevelGeneratorThread = new Thread(new HeightLevelGenerator(pathCostCalc));
  }

  public void startUpdater() {
    this.heightLevelGeneratorThread.start();
  }

  public void stopUpdater() {
    this.heightLevelGeneratorThread.interrupt();
  }

  public void reset() {
    this.heightBlocksByLocation = new HashMap<>();
    this.blocksByHeight = new HashMap<>();
  }

  public HeightBlock getHeightBlock(ExLocation current) {
    if (PathCostCalc.ROUNDED_BLOCK_MATERIALS.contains(current.getBlock().getType())) {
      current = current.getExBlock().getLocation().add(0, 1, 0);
    }

    HeightBlock block;
    this.readLock.lock();
    try {
      block = this.heightBlocksByLocation.get(current.getExBlock().getLocation());
    } finally {
      this.readLock.unlock();
    }
    return block;
  }

  public Map<Integer, List<HeightBlock>> getBlocksByHeight() {
    return blocksByHeight;
  }

  public HeightBlock getHeightBlock(ExLocation current, int radius, boolean lowerLevel) {
    return this.getHeightBlock(this.getHeightBlock(current), radius, lowerLevel);
  }

  public HeightBlock getHeightBlock(HeightBlock current, int radius, boolean lowerLevel) {
    if (current == null) {
      return null;
    }

    List<HeightBlock> blocks = new ArrayList<>();

    for (int x = 0; x <= radius; x++) {
      for (int y = 0; y <= radius; y++) {
        for (int z = 0; z <= radius; z++) {
          HeightBlock block = this.getHeightBlock(
              current.location().clone().add(x, y, z));
          if (block == null) {
            continue;
          }

          if (block.level() == current.level() || (lowerLevel && block.level() < current.level())) {
            blocks.add(block);
          }
        }
      }
    }

    if (blocks.isEmpty()) {
      return current;
    }

    return blocks.get(this.random.nextInt(blocks.size()));
  }

  public ExLocation getCoreLocation() {
    return coreLocation;
  }

  public void update() {
    this.update = true;
    synchronized (this.updater) {
      this.updater.notifyAll();
    }
  }

  private class HeightLevelGenerator implements Runnable {

    private Map<ExLocation, HeightBlock> heightBlocksByLocation = new ConcurrentHashMap<>();

    private final PathCostCalc pathCostCalc;

    public HeightLevelGenerator(PathCostCalc pathCostCalc) {
      this.pathCostCalc = pathCostCalc;
    }

    @Override
    public void run() {
      this.waitForUpdate();
    }

    public void update() {
      if (HeightMap.this.running) {
        return;
      }

      HeightMap.this.logger.info("Updating heightmap...");

      HeightMap.this.update = false;
      HeightMap.this.running = true;

      this.heightBlocksByLocation = new HashMap<>();
      Map<Integer, List<HeightBlock>> blocksByHeight = new HashMap<>();

      // clear height levels
      for (int level = 1; level <= MAX_LEVEL; level++) {
        blocksByHeight.put(level, new ArrayList<>());
      }

      // init core height block
      HeightBlock core = new HeightBlock(0, HeightMap.this.coreLocation, null, null, null);
      blocksByHeight.put(0, List.of(core));
      this.heightBlocksByLocation.put(core.location(), core);

      // calc levels
      for (int level = 1; level <= MAX_LEVEL; level++) {
        List<HeightBlock> heightBlockCenters = blocksByHeight.get(level - 1);

        // check each area
        for (BlockArea blockArea : BlockArea.AREAS) {
          // check each center block
          for (HeightBlock center : heightBlockCenters) {

            // get blocks, which have a path to the center
            Map<Block, PathCostResult> startBlocks = this.getReachableByBlocks(center.location().getBlock(), blockArea);

            for (Map.Entry<Block, PathCostResult> entry : startBlocks.entrySet()) {
              PathCostResult result = entry.getValue();
              ExLocation pathBlockLoc = new ExBlock(entry.getKey()).getLocation();

              // block already seen
              if (this.heightBlocksByLocation.containsKey(pathBlockLoc)) {
                continue;
              }

              if (level + result.costs() > MAX_LEVEL) {
                continue;
              }

              HeightBlock block = new HeightBlock(level + result.costs() + 1, pathBlockLoc, center,
                  result.getBlocksToBreakOnStart(), result.getBlocksToBreakToNext());

              // add as new center
              blocksByHeight.get(level + result.costs()).add(block);

              // add to location map
              this.heightBlocksByLocation.put(pathBlockLoc, block);
            }
          }
        }
        HeightMap.this.logger.info("Blocks on height {}: {}", level, blocksByHeight.get(level).size());
      }

      HeightMap.this.writeLock.lock();
      try {
        HeightMap.this.blocksByHeight = blocksByHeight;
        HeightMap.this.heightBlocksByLocation = this.heightBlocksByLocation;
      } finally {
        HeightMap.this.writeLock.unlock();
      }
      HeightMap.this.logger.info("Updated heightmap");
      HeightMap.this.running = false;
      this.waitForUpdate();
    }

    public void waitForUpdate() {
      while (!HeightMap.this.update && !HeightMap.this.running) {
        try {
          synchronized (HeightMap.this.updater) {
            HeightMap.this.updater.wait();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      this.update();
    }

    public Map<Block, PathCostResult> getReachableByBlocks(Block end, BlockArea blockArea) {
      Map<Block, PathCostResult> blockByCostResult = new HashMap<>();

      Location endLocation = end.getLocation();

      for (Vector vector : blockArea.getVectors()) {

        Location startLocation = endLocation.clone().add(vector);

        if (this.heightBlocksByLocation.containsKey(ExLocation.fromLocation(startLocation))) {
          continue;
        }

        Block startBlock = startLocation.getBlock();

        PathCostResult result = this.isBlockReachable(startBlock, end);
        if (!result.isBlocked()) {
          blockByCostResult.put(startBlock, result);
        }
      }

      return blockByCostResult;
    }

    private PathCostResult isBlockReachable(Block start, Block end) {
      if (start.getLocation().distanceSquared(end.getLocation()) > 2) {
        return PathCostResult.BLOCKED;
      }

      return this.pathCostCalc.apply(new ShortPath(new ExBlock(start), new ExBlock(end),
          end.getY() - start.getY()));
    }

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
