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
  private final int maxBlocksToNextLocation;
  private final Break breakBlock;
  private final int maxPathCostsBeforeBreak;

  private HeightBlock lastBlock;

  public HeightPathfinder(Mob mob, HeightMap map, int maxBlocksToNextLocation, double walkSpeed,
                          double trackingDistance,
                          double minDistance, Break breakBlock, int maxPathCostsBeforeBreak) {
    super(mob, walkSpeed, trackingDistance, minDistance);
    this.map = map;
    this.maxBlocksToNextLocation = maxBlocksToNextLocation;
    this.breakBlock = breakBlock;
    this.maxPathCostsBeforeBreak = maxPathCostsBeforeBreak;
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
    Location loc = entityLoc.getY() - entityLoc.getBlockY() > 0 ? entityLoc.add(0, 1, 0) : entityLoc;

    HeightBlock currentBlock = this.map.getHeightBlock(ExLocation.fromLocation(loc));
    if (currentBlock == null) {
      return this.lastBlock != null ? this.lastBlock.block().getLocation() : this.map.getCoreLocation();
    }

    HeightBlock nextBlock = currentBlock;
    for (int i = 0; i < this.maxBlocksToNextLocation; i++) {
      if (!nextBlock.hasNext()) {
        break;
      }
      nextBlock = nextBlock.next();
    }

    if (nextBlock != null) {
      if (nextBlock.level() - currentBlock.level() - this.maxBlocksToNextLocation + 1 >= this.maxPathCostsBeforeBreak) {
        ExLocation firstNextLoc = currentBlock.next().block().getLocation();
        if (this.breakBlock != null) {
          this.breakBlock.setTarget(firstNextLoc.getX(), firstNextLoc.getY(), firstNextLoc.getZ());
        }
        return null;
      } else {
        if (this.breakBlock != null) {
          this.breakBlock.setTarget(null, null, null);
        }
      }
    }

    this.lastBlock = nextBlock;

    return nextBlock.block().getLocation();
  }
}
