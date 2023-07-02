/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.library.entities.pathfinder.LocationTargetable;
import de.timesnake.library.entities.pathfinder.UpdatedLocationGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;

public class HeightPathfinder<Break extends Goal & LocationTargetable> extends UpdatedLocationGoal {

  private final HeightMap map;
  private final int steps;
  private final Break breakBlock;
  private final int breakLevel;
  private HeightBlock current;

  public HeightPathfinder(Mob mob, HeightMap map, int steps, double speed, double trackingDistance,
                          double minDistance, Break breakBlock, int breakLevel) {
    super(mob, speed, trackingDistance, minDistance);
    this.map = map;
    this.steps = steps;
    if (breakBlock != null) {
      this.breakBlock = breakBlock;
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

    if (this.entity.getTarget() != null) {
      return null;
    }

    // slab, stair fix
    Location loc =
        entityLoc.getY() - entityLoc.getBlockY() > 0 ? entityLoc.add(0, 1, 0) : entityLoc;

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
