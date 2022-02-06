package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HeightMap {

    private static final int MAX_LEVEL = 300;
    private static final int HEIGHT_SIZE = 1;

    private final ExLocation coreLocation;

    private Map<ExLocation, HeightBlock> heightBlocksByLocation = new HashMap<>();
    private Map<Integer, List<HeightBlock>> blocksByHeight = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    private boolean cooldown = false;
    private boolean update = false;
    private final Thread generatorThread;
    private final HeightLevelGenerator heightLevelGenerator;

    private final Random random = new Random();

    public HeightMap(ExLocation coreLocation, List<List<BlockCheck>> checkGroups) {
        this.coreLocation = coreLocation;
        this.heightLevelGenerator = new HeightLevelGenerator(checkGroups);
        this.generatorThread = new Thread(heightLevelGenerator);
    }

    public void reset() {
        this.heightBlocksByLocation = new HashMap<>();
        this.blocksByHeight = new HashMap<>();
    }

    public HeightBlock getHeightBlock(ExLocation current) {
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
                    HeightBlock block = this.getHeightBlock(current.getLocation().clone().add(x, y, z));
                    if (block == null) {
                        continue;
                    }

                    if (block.getLevel() == current.getLevel() || (lowerLevel && block.getLevel() < current.getLevel())) {
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
        if (this.cooldown) {
            this.update = true;
            return;
        }

        this.cooldown = true;
        if (this.generatorThread != null) {
            this.generatorThread.stop();
        }


        new Thread(this.heightLevelGenerator).start();
    }

    private class HeightLevelGenerator implements Runnable {

        private static final int COOLDOWN = 20 * 2;

        private Map<ExLocation, HeightBlock> heightBlocksByLocation = new ConcurrentHashMap<>();
        private Map<Integer, List<HeightBlock>> blocksByHeight = new ConcurrentHashMap<>();

        private final List<List<BlockCheck>> checkGroups;

        private final ExecutorService pool;

        public HeightLevelGenerator(List<List<BlockCheck>> checkGroups) {
            this.checkGroups = checkGroups;
            this.pool = Executors.newFixedThreadPool(MAX_LEVEL);
        }

        @Override
        public void run() {
            long begin = System.currentTimeMillis();

            this.heightBlocksByLocation = new HashMap<>();
            this.blocksByHeight = new HashMap<>();

            // clear height levels
            for (int level = 1; level <= MAX_LEVEL; level++) {
                this.blocksByHeight.put(level, new ArrayList<>());
            }

            // init core height block
            HeightBlock core = new HeightBlock(0, HeightMap.this.coreLocation, null);
            this.blocksByHeight.put(0, List.of(core));
            this.heightBlocksByLocation.put(core.getLocation(), core);

            // calc levels
            for (int level = 1; level <= MAX_LEVEL; level++) {
                List<HeightBlock> heightBlockCenters = this.blocksByHeight.get(level - 1);

                // check each area
                for (BlockArea blockArea : BlockArea.AREAS) {

                    List<Future<List<HeightBlock>>> futureList = new ArrayList<>();

                    // check each center block
                    for (HeightBlock center : heightBlockCenters) {
                        Future<List<HeightBlock>> future = this.pool.submit(new BlockAreaCheck(level, this.checkGroups, center, blockArea));
                        futureList.add(future);
                    }

                    while (!futureList.isEmpty()) {
                        ListIterator<Future<List<HeightBlock>>> iterator = futureList.listIterator();
                        while (iterator.hasNext()) {
                            Future<List<HeightBlock>> future = iterator.next();
                            if (future.isDone()) {
                                try {
                                    for (HeightBlock block : future.get()) {
                                        HeightBlock listBlock = this.heightBlocksByLocation.get(block.getLocation());
                                        if (listBlock != null) {
                                            continue;
                                        }

                                        this.heightBlocksByLocation.put(block.getLocation(), block);
                                        this.blocksByHeight.get(block.getLevel()).add(block);
                                    }
                                    iterator.remove();

                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            HeightMap.this.writeLock.lock();
            try {
                HeightMap.this.blocksByHeight = this.blocksByHeight;
                HeightMap.this.heightBlocksByLocation = this.heightBlocksByLocation;
            } finally {
                HeightMap.this.writeLock.unlock();
            }

            Server.printText(Plugin.MOB_DEFENCE, "Map updated in " + ((System.currentTimeMillis() - begin) / 1000) + "s");

            Server.runTaskLaterAsynchrony(() -> {
                HeightMap.this.cooldown = false;
                if (HeightMap.this.update) {
                    HeightMap.this.update = false;
                    HeightMap.this.update();
                }
            }, COOLDOWN, GameMobDefence.getPlugin());
        }

    }

    private static class BlockAreaCheck implements Callable<List<HeightBlock>> {

        private final int level;
        private final HeightBlock center;
        private final List<List<BlockCheck>> checkGroups;
        private final BlockArea area;

        public BlockAreaCheck(int level, List<List<BlockCheck>> checkGroups, HeightBlock center, BlockArea area) {
            this.level = level;
            this.checkGroups = checkGroups;
            this.center = center;
            this.area = area;
        }

        @Override
        public List<HeightBlock> call() {

            List<HeightBlock> blocks = new ArrayList<>();

            Map<Block, Integer> pathBlocks = this.getReachableByBlocks(center.getLocation().getBlock(), area);


            for (Map.Entry<Block, Integer> entry : pathBlocks.entrySet()) {
                int delta = entry.getValue();
                ExLocation pathBlockLoc = new ExBlock(entry.getKey()).getLocation();

                if (level + delta > MAX_LEVEL) {
                    continue;
                }

                HeightBlock block = new HeightBlock(level + delta, pathBlockLoc, center);
                blocks.add(block);
            }

            return blocks;
        }

        public Map<Block, Integer> getReachableByBlocks(Block start, BlockArea blockArea) {
            Map<Block, Integer> blocksWithLevelDelta = new HashMap<>();

            Location startLoc = start.getLocation();

            for (Vector vector : blockArea.getVectors()) {

                Location loc = startLoc.clone().add(vector);

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

        STRAIGHT(List.of(new Vector(-1, 0, 0), new Vector(1, 0, 0), new Vector(0, 0, -1), new Vector(0, 0, 1))), DIAGONAL(List.of(new Vector(-1, 0, -1), new Vector(-1, 0, 1), new Vector(1, 0, -1), new Vector(1, 0, 1), new Vector(-1, -1, 0), new Vector(-1, 1, 0), new Vector(1, -1, 0), new Vector(1, 1, 0), new Vector(0, -1, -1), new Vector(0, -1, 1), new Vector(0, 1, -1), new Vector(0, 1, 1))), DOUBLE_DIAGONAL(List.of(new Vector(-1, -1, -1), new Vector(-1, -1, 1), new Vector(-1, 1, -1), new Vector(-1, 1, 1), new Vector(1, -1, -1), new Vector(1, -1, 1), new Vector(1, 1, -1), new Vector(1, 1, 1)));

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
