/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HeightMap {

  private static final ExecutorService executorService =
      new ThreadPoolExecutor(HeightMapManager.MapType.values().length, 4, 10L,
          TimeUnit.SECONDS, new LinkedBlockingQueue<>());

  private final Logger logger;

  private final String name;
  private final ExLocation coreLocation;

  private final HeightMapGenerator generator;

  private final ConcurrentHashMap<ExBlock, HeightBlock> heightBlocksByBlock = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<Integer, List<HeightBlock>> blocksByHeight = new ConcurrentHashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private boolean updating = false;
  private boolean updateQueued = false;

  public HeightMap(String name, ExLocation coreLocation, PathCostCalc pathCostCalc) {
    this.name = name;
    this.logger = LogManager.getLogger("mob-def.height-map." + this.name);
    this.coreLocation = coreLocation;
    this.generator = new HeightMapGenerator(name, this.coreLocation, pathCostCalc);
  }

  public void update() {
    if (updating) {
      this.updateQueued = true;
      return;
    }
    executorService.execute(this::performUpdate);
  }

  private void performUpdate() {
    HeightMap.this.updating = true;
    do {
      HeightMap.this.updateQueued = false;
      Future<Collection<HeightBlock>> future = HeightMap.this.generator.invokeUpdate();
      try {
        Collection<HeightBlock> blocks = future.get(30, TimeUnit.SECONDS);
        try {
          HeightMap.this.lock.writeLock().lock();
          HeightMap.this.heightBlocksByBlock.clear();
          HeightMap.this.blocksByHeight.clear();
          blocks.forEach(b -> {
            HeightMap.this.heightBlocksByBlock.put(b.block(), b);
            HeightMap.this.blocksByHeight.computeIfAbsent(b.level(), k -> new ArrayList<>()).add(b);
          });
        } finally {
          HeightMap.this.lock.writeLock().unlock();
        }

      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        HeightMap.this.logger.warn("Exception while updating heightmap", e);
        future.cancel(true);
      }
    } while (HeightMap.this.updateQueued);
    HeightMap.this.updating = false;
  }

  public static void stopUpdater() {
    Server.runTaskAsynchrony(() -> {
      executorService.shutdownNow();
    }, GameMobDefence.getPlugin());
  }

  public void reset() {
    this.heightBlocksByBlock.clear();
    this.blocksByHeight.clear();
  }

  public HeightBlock getHeightBlock(ExLocation current) {
    if (MobDefServer.ROUNDED_BLOCK_MATERIALS.contains(current.getBlock().getType())) {
      current = current.getExBlock().getLocation().add(0, 1, 0);
    }

    HeightBlock heightBlock;
    try {
      this.lock.readLock().lock();
      heightBlock = this.heightBlocksByBlock.get(current.getExBlock());
    } finally {
      this.lock.readLock().unlock();
    }
    return heightBlock;
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
