/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HeightMap {

  private final Logger logger;

  private final String name;
  private final ExLocation coreLocation;

  private final HeightMapGenerator generator;

  private final ConcurrentHashMap<ExLocation, HeightBlock> heightBlocksByLocation = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, List<HeightBlock>> blocksByHeight = new ConcurrentHashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private BukkitTask updateTask;

  public HeightMap(String name, ExLocation coreLocation, PathCostCalc pathCostCalc) {
    this.name = name;
    this.logger = LogManager.getLogger("mob-def.height-map." + this.name);
    this.coreLocation = coreLocation;
    this.generator = new HeightMapGenerator(name, this.coreLocation, pathCostCalc);
  }

  public void update() {
    this.updateTask = Server.runTaskAsynchrony(() -> {

      Future<Collection<HeightBlock>> future = this.generator.invokeUpdate();
      try {
        Collection<HeightBlock> blocks = future.get(30, TimeUnit.SECONDS);
        try {
          this.lock.writeLock().lock();
          this.heightBlocksByLocation.clear();
          this.blocksByHeight.clear();
          blocks.forEach(b -> {
            this.heightBlocksByLocation.put(b.block().getLocation(), b);
            this.blocksByHeight.computeIfAbsent(b.level(), k -> new ArrayList<>()).add(b);
          });
        } finally {
          this.lock.writeLock().unlock();
        }

      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        this.logger.warn("Exception while updating heightmap", e);
        future.cancel(true);
      }

    }, GameMobDefence.getPlugin());
  }

  public void stopUpdater() {
    if (this.updateTask != null) {
      this.updateTask.cancel();
    }
  }

  public void reset() {
    this.stopUpdater();
    this.heightBlocksByLocation.clear();
    this.blocksByHeight.clear();
  }

  public HeightBlock getHeightBlock(ExLocation current) {
    if (PathCostCalc.ROUNDED_BLOCK_MATERIALS.contains(current.getBlock().getType())) {
      current = current.getExBlock().getLocation().add(0, 1, 0);
    }

    HeightBlock heightBlock;
    try {
      this.lock.readLock().lock();
      heightBlock = this.heightBlocksByLocation.get(current.middleHorizontalBlock());
    } finally {
      this.lock.readLock().unlock();
    }
    return heightBlock;
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
          HeightBlock block = this.getHeightBlock(current.block().getLocation().clone().add(x, y, z));
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

    return blocks.get(Server.getRandom().nextInt(blocks.size()));
  }

  public Map<Integer, List<HeightBlock>> getBlocksByHeight() {
    Map<Integer, List<HeightBlock>> map;

    try {
      this.lock.readLock().lock();
      map = new HashMap<>(this.blocksByHeight);
    } finally {
      this.lock.readLock().unlock();
    }

    return map;
  }

  public ExLocation getCoreLocation() {
    return coreLocation;
  }
}
