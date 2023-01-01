/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class HeightMap {

    public static final int MAX_LEVEL = 400;

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

    public HeightMap(ExLocation coreLocation, List<List<BlockCheck>> checkGroups) {
        this.coreLocation = coreLocation;
        this.heightLevelGeneratorThread = new Thread(new HeightLevelGenerator(checkGroups));
        this.heightLevelGeneratorThread.start();
    }

    public void reset() {
        this.heightBlocksByLocation = new HashMap<>();
        this.blocksByHeight = new HashMap<>();
    }

    public HeightBlock getHeightBlock(ExLocation current) {
        if (BlockCheck.ROUNDED_BLOCKS.isTagged(current.getBlock().getType())) {
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
                            current.getLocation().clone().add(x, y, z));
                    if (block == null) {
                        continue;
                    }

                    if (block.getLevel() == current.getLevel() || (lowerLevel
                            && block.getLevel() < current.getLevel())) {
                        blocks.add(block);
                    }
                }
            }
        }

        if (blocks.size() == 0) {
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

        private final List<List<BlockCheck>> checkGroups;

        public HeightLevelGenerator(List<List<BlockCheck>> checkGroups) {
            this.checkGroups = checkGroups;
        }

        @Override
        public void run() {
            this.waitForUpdate();
        }

        public void update() {

            HeightMap.this.update = false;
            HeightMap.this.running = true;

            long begin = System.currentTimeMillis();

            this.heightBlocksByLocation = new HashMap<>();
            Map<Integer, List<HeightBlock>> blocksByHeight = new HashMap<>();

            // clear height levels
            for (int level = 1; level <= MAX_LEVEL; level++) {
                blocksByHeight.put(level, new ArrayList<>());
            }

            // init core height block
            HeightBlock core = new HeightBlock(0, HeightMap.this.coreLocation, null);
            blocksByHeight.put(0, List.of(core));
            this.heightBlocksByLocation.put(core.getLocation(), core);

            // calc levels
            for (int level = 1; level <= MAX_LEVEL; level++) {
                List<HeightBlock> heightBlockCenters = blocksByHeight.get(level - 1);

                // check each area
                for (BlockArea blockArea : BlockArea.AREAS) {
                    // check each center block
                    for (HeightBlock center : heightBlockCenters) {

                        // get blocks, which have a path to the center
                        Map<Block, Integer> pathBlocks = this.getReachableByBlocks(
                                center.getLocation().getBlock(),
                                blockArea);

                        for (Map.Entry<Block, Integer> entry : pathBlocks.entrySet()) {
                            int delta = entry.getValue();
                            ExLocation pathBlockLoc = new ExBlock(entry.getKey()).getLocation();

                            // block already seen
                            if (this.heightBlocksByLocation.containsKey(pathBlockLoc)) {
                                continue;
                            }

                            if (level + delta > MAX_LEVEL) {
                                continue;
                            }

                            HeightBlock block = new HeightBlock(level + delta, pathBlockLoc,
                                    center);

                            // add as new center
                            blocksByHeight.get(level + delta).add(block);

                            // add to location map
                            this.heightBlocksByLocation.put(pathBlockLoc, block);
                        }
                    }
                }
            }

            HeightMap.this.writeLock.lock();
            try {
                HeightMap.this.blocksByHeight = blocksByHeight;
                HeightMap.this.heightBlocksByLocation = this.heightBlocksByLocation;
            } finally {
                HeightMap.this.writeLock.unlock();
            }

            long timeDelta = (System.currentTimeMillis() - begin);
            // Server.printText(Plugin.MOB_DEFENCE, "Map updated in " + (timeDelta / 1000) + "." + timeDelta % 1000 +
            // "s");

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

        public Map<Block, Integer> getReachableByBlocks(Block start, BlockArea blockArea) {
            Map<Block, Integer> blocksWithLevelDelta = new HashMap<>();

            Location startLoc = start.getLocation();

            for (Vector vector : blockArea.getVectors()) {

                Location loc = startLoc.clone().add(vector);

                if (this.heightBlocksByLocation.containsKey(ExLocation.fromLocation(loc))) {
                    continue;
                }

                Block block = loc.getBlock();

                int delta = this.isBlockReachable(block, start);
                if (delta >= 0) {
                    blocksWithLevelDelta.put(block, delta);
                }
            }

            return blocksWithLevelDelta;
        }

        private int isBlockReachable(Block start, Block finish) {
            if (start.getLocation().distanceSquared(finish.getLocation()) > 2) {
                return -1;
            }

            int heightDelta = start.getY() - finish.getY();

            int costSum = 0;

            for (List<BlockCheck> checks : this.checkGroups) {
                int extraCheckDelta = -1;

                for (BlockCheck check : checks) {
                    int delta = check.getLevelDelta(start, finish, heightDelta);

                    if (delta <= 0) {
                        continue;
                    }

                    if (delta == 1) {
                        extraCheckDelta = 0;
                        break;
                    }

                    if (extraCheckDelta > delta || extraCheckDelta < 0) {
                        extraCheckDelta = delta - 1;
                    }
                }

                if (extraCheckDelta < 0) {
                    return -1;
                }

                costSum += extraCheckDelta;
            }

            return costSum;
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
