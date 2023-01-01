/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.game.mobdefence.main.GameMobDefence;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.jetbrains.annotations.NotNull;

public abstract class BlockCheck {

    public static final Set<Material> ROUNDED_BLOCK_MATERIALS = new HashSet<>();
    public static final Tag<Material> ROUNDED_BLOCKS = new Tag<>() {
        @Override
        public boolean isTagged(@NotNull Material material) {
            return ROUNDED_BLOCK_MATERIALS.contains(material);
        }

        @Override
        public @NotNull Set<Material> getValues() {
            return ROUNDED_BLOCK_MATERIALS;
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return new NamespacedKey(GameMobDefence.getPlugin(), "walkable");
        }
    };
    public static final Set<Material> WALKABLE_IN_MATERIALS = new HashSet<>();
    public static final Tag<Material> WALKABLE_IN = new Tag<>() {
        @Override
        public boolean isTagged(@NotNull Material material) {
            return WALKABLE_IN_MATERIALS.contains(material);
        }

        @Override
        public @NotNull Set<Material> getValues() {
            return WALKABLE_IN_MATERIALS;
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return new NamespacedKey(GameMobDefence.getPlugin(), "walkable");
        }
    };
    public static final BlockCheck WALK_IN = new BlockCheck() {
        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Location startLoc = start.getLocation();
            Location finishLoc = finish.getLocation();

            if (!WALKABLE_IN.isTagged(startLoc.getBlock().getType()) || !WALKABLE_IN.isTagged(
                    startLoc.clone().add(0,
                            1, 0).getBlock().getType())) {
                return -1;
            }

            while (heightDelta > 0) {
                if (!WALKABLE_IN.isTagged(
                        finishLoc.add(0, heightDelta + 1, 0).getBlock().getType())) {
                    return -1;
                }
                heightDelta--;
            }

            while (heightDelta < 0) {
                if (!WALKABLE_IN.isTagged(
                        startLoc.add(0, -heightDelta + 1, 0).getBlock().getType())) {
                    return -1;
                }
                heightDelta++;
            }

            return 1;
        }
    };
    public static final BlockCheck WALK_IN_SMALL = new BlockCheck() {
        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Location startLoc = start.getLocation();
            Location finishLoc = finish.getLocation();

            if (!WALKABLE_IN.isTagged(startLoc.getBlock().getType())) {
                return -1;
            }

            while (heightDelta > 0) {
                if (!WALKABLE_IN.isTagged(finishLoc.add(0, heightDelta, 0).getBlock().getType())) {
                    return -1;
                }
                heightDelta--;
            }

            while (heightDelta < 0) {
                if (!WALKABLE_IN.isTagged(startLoc.add(0, -heightDelta, 0).getBlock().getType())) {
                    return -1;
                }
                heightDelta++;
            }

            return 1;
        }
    };
    public static final BlockCheck WALL = new BlockCheck() {
        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            if (heightDelta < -1 || heightDelta > 1) {
                return -1;
            }

            if (WALKABLE_IN.isTagged(finish.getType()) && WALKABLE_IN.isTagged(
                    finish.getLocation().add(0, 1, 0).getBlock().getType())) {
                return 5;
            }

            if (WALKABLE_IN.isTagged(start.getType()) && WALKABLE_IN.isTagged(
                    start.getLocation().add(0, 1, 0).getBlock().getType())) {
                return 5;
            }

            return -1;
        }
    };
    public static final Set<Material> NORMAL_BREAKABLE_MATERIALS = Set.of(Material.OAK_PLANKS,
            Material.OAK_SLAB,
            Material.IRON_BARS);
    public static final Tag<Material> NORMAL_BREAKABLE = new Tag<>() {
        @Override
        public boolean isTagged(@NotNull Material material) {
            return NORMAL_BREAKABLE_MATERIALS.contains(material);
        }

        @Override
        public @NotNull Set<Material> getValues() {
            return NORMAL_BREAKABLE_MATERIALS;
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return new NamespacedKey(GameMobDefence.getPlugin(), "normal_breakable");
        }
    };
    public static final Set<Material> HIGH_BREAKABLE_MATERIALS = Set.of(Material.OAK_FENCE,
            Material.OAK_FENCE_GATE,
            Material.COBBLESTONE_WALL);
    public static final Tag<Material> HIGH_BREAKABLE = new Tag<>() {
        @Override
        public boolean isTagged(@NotNull Material material) {
            return HIGH_BREAKABLE_MATERIALS.contains(material);
        }

        @Override
        public @NotNull Set<Material> getValues() {
            return HIGH_BREAKABLE_MATERIALS;
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return new NamespacedKey(GameMobDefence.getPlugin(), "high_breakable");
        }
    };
    public static final BlockCheck ON_SOLID_1H = new BlockCheck() {
        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Block underBlock = start.getLocation().add(0, -1, 0).getBlock();

            if (!underBlock.getType().isSolid()) {
                return -1;
            }

            if (WALKABLE_IN.isTagged(underBlock.getType())) {
                return -1;
            }

            if (HIGH_BREAKABLE.isTagged(underBlock.getType())) {
                return -1;
            }

            return 1;
        }
    };
    public static final Set<Material> BREAKABLE_MATERIALS = new HashSet<>();
    public static final BlockCheck OPEN_DOOR = new BlockCheck() {
        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            if (!Tag.DOORS.isTagged(start.getType())) {
                return -1;
            }
            if (start.getBlockData() instanceof Door) {
                return ((Door) start.getBlockData()).isOpen() ? 1 : -1;
            }
            return -1;
        }
    };

    static {
        ROUNDED_BLOCK_MATERIALS.addAll(Tag.SLABS.getValues());
        ROUNDED_BLOCK_MATERIALS.addAll(Tag.STAIRS.getValues());
    }

    ;

    static {
        WALKABLE_IN_MATERIALS.add(Material.AIR);
        WALKABLE_IN_MATERIALS.addAll(Tag.CLIMBABLE.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.CROPS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.WOOL_CARPETS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.SIGNS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.BANNERS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.BUTTONS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.PRESSURE_PLATES.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.RAILS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.FLOWERS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.SAPLINGS.getValues());
        WALKABLE_IN_MATERIALS.addAll(Tag.RAILS.getValues());
        WALKABLE_IN_MATERIALS.add(Material.SNOW);
        WALKABLE_IN_MATERIALS.add(Material.TRIPWIRE);
        WALKABLE_IN_MATERIALS.addAll(
                List.of(Material.GRASS, Material.TALL_GRASS, Material.ARMOR_STAND));
    }

    ;

    static {
        BREAKABLE_MATERIALS.addAll(NORMAL_BREAKABLE_MATERIALS);
        BREAKABLE_MATERIALS.addAll(HIGH_BREAKABLE_MATERIALS);
    }

    public abstract int getLevelDelta(Block start, Block finish, int heightDelta);

    public static class HighBlockBreak extends Breakable {

        private final int cost;

        public HighBlockBreak(int cost, Tag<Material>... breakable) {
            super(breakable);
            this.cost = cost;
        }

        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Block upperBlock = start.getLocation().add(0, 1, 0).getBlock();

            if (this.breakable.isTagged(start.getType())) {
                if (WALKABLE_IN.isTagged(upperBlock.getType())) {
                    return this.cost;
                }

                if (this.breakable.isTagged(upperBlock.getType())) {
                    return 2 * this.cost;
                }

                return -1;
            }

            if (WALKABLE_IN.isTagged(start.getType())) {
                if (this.breakable.isTagged(upperBlock.getType())) {
                    return this.cost;
                }
            }

            return -1;
        }
    }

    public static class FloorFenceWallBlocked extends Breakable {

        public FloorFenceWallBlocked(Tag<Material>... breakable) {
            super(breakable);
        }

        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Material startDownType = start.getLocation().add(0, -1, 0).getBlock().getType();
            Material finishDownType = finish.getLocation().add(0, -1, 0).getBlock().getType();
            Material startUpType = start.getLocation().add(0, 2, 0).getBlock().getType();
            Material finishUpType = finish.getLocation().add(0, 2, 0).getBlock().getType();

            boolean startFenceWall =
                    !breakable.isTagged(startDownType) && (Tag.FENCES.isTagged(startDownType)
                            || Tag.WALLS.isTagged(startDownType));
            boolean finishFenceWall =
                    !breakable.isTagged(finishDownType) && (Tag.FENCES.isTagged(finishDownType)
                            || Tag.WALLS.isTagged(finishDownType));

            if (heightDelta < 0 && finishFenceWall) {
                return -1;
            }

            if (startFenceWall || finishFenceWall) {
                if (!WALKABLE_IN.isTagged(startUpType) || !WALKABLE_IN.isTagged(finishUpType)) {
                    return -1;
                }
            }

            return 1;
        }
    }

    ;

    public static class HardBreakable extends BlockCheck {

        private final double multiplier;

        public HardBreakable(double multiplier) {
            this.multiplier = multiplier;
        }

        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Location startLoc = start.getLocation();
            Location finishLoc = finish.getLocation();

            Block startBlock = startLoc.getBlock();
            Block upperBlock = startLoc.clone().add(0, 1, 0).getBlock();

            if (WALK_IN.getLevelDelta(start, finish, heightDelta) > 0) {
                return -1;
            }

            int costs = 0;

            if (heightDelta == 0) {
                costs += (int) (startBlock.getType().getHardness() * this.multiplier);
                costs += ((int) (upperBlock.getType().getHardness() * this.multiplier));
                return costs;
            }

            if (heightDelta == 1) {
                costs += (int) (startBlock.getType().getHardness() * this.multiplier);
                costs += ((int) (upperBlock.getType().getHardness() * this.multiplier));
                costs += ((int) (startLoc.clone().add(0, 2, 0).getBlock().getType().getHardness()
                        * this.multiplier));
                return costs;
            }

            if (heightDelta == -1) {
                costs += (int) (startBlock.getType().getHardness() * this.multiplier);
                costs += ((int) (upperBlock.getType().getHardness() * this.multiplier));
                costs += ((int) (finishLoc.clone().add(0, 2, 0).getBlock().getType().getHardness()
                        * this.multiplier));
                return costs;
            }

            return 1;
        }
    }

    ;

    public static class DiagonalBlocked extends Breakable {

        private final int cost;

        public DiagonalBlocked(int cost, Tag<Material>... breakable) {
            super(breakable);
            this.cost = cost;
        }

        @Override
        public int getLevelDelta(Block start, Block finish, int heightDelta) {
            Location startLoc = start.getLocation();
            Location finishLoc = finish.getLocation();

            int deltaX = finish.getX() - start.getX();
            int deltaZ = finish.getZ() - start.getZ();

            if (deltaX != 0 && deltaZ != 0) {

                Block xBlock = startLoc.clone().add(deltaX, 0, 0).getBlock();
                Material xBlockType = xBlock.getType();
                Material xBlockUpType = xBlock.getLocation().add(0, 1, 0).getBlock().getType();
                Material xBlockDownType = xBlock.getLocation().add(0, -1, 0).getBlock().getType();

                Block zBlock = startLoc.clone().add(0, 0, deltaZ).getBlock();
                Material zBlockType = zBlock.getType();
                Material zBlockUpType = zBlock.getLocation().add(0, 1, 0).getBlock().getType();
                Material zBlockDownType = zBlock.getLocation().add(0, -1, 0).getBlock().getType();

                int xBreakableEmpty =
                        (this.breakable.isTagged(xBlockType) ? this.cost : 0) + (
                                WALKABLE_IN.isTagged(xBlockType) ?
                                        1 : 0);

                int upperXBreakableEmpty =
                        (this.breakable.isTagged(xBlockUpType) ? this.cost : 0) + (
                                WALKABLE_IN.isTagged(xBlockUpType) ? 1 : 0);

                int zBreakableEmpty =
                        (this.breakable.isTagged(zBlockType) ? this.cost : 0) + (
                                WALKABLE_IN.isTagged(zBlock.getType()) ? 1 : 0);

                int upperZBreakableEmpty =
                        (this.breakable.isTagged(zBlockUpType) ? this.cost : 0) + (
                                WALKABLE_IN.isTagged(zBlockUpType) ? 1 : 0);

                boolean startFinishEmptyTripleUp =
                        !WALKABLE_IN.isTagged(start.getLocation().add(0, 2, 0).getBlock().getType())
                                && !WALKABLE_IN.isTagged(
                                finish.getLocation().add(0, 2, 0).getBlock().getType());

                if (upperXBreakableEmpty > 0 && xBreakableEmpty > 0) {
                    if (Tag.FENCES.isTagged(xBlockDownType) || Tag.WALLS.isTagged(xBlockDownType)) {
                        if (WALKABLE_IN.isTagged(
                                xBlock.getLocation().add(0, 2, 0).getBlock().getType())
                                && startFinishEmptyTripleUp) {
                            return upperXBreakableEmpty + xBreakableEmpty;
                        }
                    } else {
                        return upperXBreakableEmpty + xBreakableEmpty;
                    }
                }

                if (upperZBreakableEmpty > 0 && zBreakableEmpty > 0) {
                    if (Tag.FENCES.isTagged(zBlockDownType) || Tag.WALLS.isTagged(zBlockDownType)) {
                        if (WALKABLE_IN.isTagged(
                                zBlock.getLocation().add(0, 2, 0).getBlock().getType())
                                && startFinishEmptyTripleUp) {
                            return upperZBreakableEmpty + zBreakableEmpty;
                        }
                    } else {
                        return upperZBreakableEmpty + zBreakableEmpty;
                    }
                }
                return -1;
            }
            return 1;
        }

    }

    private static abstract class Breakable extends BlockCheck {

        protected final Tag<Material> breakable;
        private final Set<Material> materials = new HashSet<>();

        public Breakable(Tag<Material>... breakable) {
            for (Tag<Material> tag : breakable) {
                this.materials.addAll(tag.getValues());
            }

            this.breakable = new Tag<>() {
                @Override
                public boolean isTagged(@NotNull Material material) {
                    return materials.contains(material);
                }

                @Override
                public @NotNull Set<Material> getValues() {
                    return materials;
                }

                @Override
                public @NotNull NamespacedKey getKey() {
                    return new NamespacedKey(GameMobDefence.getPlugin(), "custom");
                }
            };
        }

    }
}
