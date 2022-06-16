package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoal;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalUpdatedLocation;
import de.timesnake.library.entities.pathfinder.LocationTargetable;
import de.timesnake.library.entities.pathfinder.PathfinderGoalUpdatedLocation;
import org.bukkit.Location;

public class ExHeightPathfinder extends ExPathfinderGoalUpdatedLocation {

    public ExHeightPathfinder(HeightMap map, int steps, double speed, double trackingDistance, double minDistance,
                              ExPathfinderGoal breakBlock, int breakLevel) {
        super(new CorePathfinder(map, steps, speed, trackingDistance, minDistance, breakBlock, breakLevel));

    }

    public static class CorePathfinder extends PathfinderGoalUpdatedLocation {

        private final HeightMap map;
        private final int steps;
        private final LocationTargetable breakBlock;
        private final int breakLevel;
        private HeightBlock current;

        public CorePathfinder(HeightMap map, int steps, double speed, double trackingDistance, double minDistance,
                              ExPathfinderGoal breakBlock, int breakLevel) {
            super(null, speed, trackingDistance, minDistance);
            this.map = map;
            this.steps = steps;
            if (breakBlock != null) {
                this.breakBlock = ((LocationTargetable) breakBlock.getNMS());
            } else {
                this.breakBlock = null;
            }
            this.breakLevel = breakLevel;
        }

        @Override
        public Location getNextLocation(Location entityLoc) {
            if (map.getCoreLocation().distanceSquared(entityLoc) <= 4) {
                return null;
            }

            if (this.entity.getNMSTarget() != null) {
                return null;
            }

            // slab, stair fix
            Location loc = entityLoc.getY() - entityLoc.getBlockY() > 0 ? entityLoc.add(0, 1, 0) : entityLoc;

            HeightBlock next = this.map.getHeightBlock(ExLocation.fromLocation(loc));
            if (next == null) {
                return this.current != null ? this.current.getLocation() : this.map.getCoreLocation();
            }

            for (int i = 0; i < this.steps; i++) {
                if (!next.hasNext()) {
                    break;
                }
                next = next.getNext();
            }

            if (this.current != null) {
                HeightBlock entityHeightBlock = this.map.getHeightBlock(ExLocation.fromLocation(loc));
                if (entityHeightBlock != null) {
                    if (entityHeightBlock.getLevel() - next.getLevel() >= this.steps - 1 + this.breakLevel) {
                        ExLocation nextLoc = next.getLocation();
                        if (this.breakBlock != null) {
                            this.breakBlock.setTarget(nextLoc.getX(), nextLoc.getY(), nextLoc.getZ());
                        }
                        return null;
                    } else {
                        if (this.breakBlock != null) {
                            this.breakBlock.setTarget(null, null, null);
                        }
                    }
                }
            }

            this.current = next;

            return next.getLocation();
        }
    }
}
