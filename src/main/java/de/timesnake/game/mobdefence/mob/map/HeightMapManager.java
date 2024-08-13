/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class HeightMapManager implements Listener {

  private final HashMap<MapType, HeightMap> mapsByType = new HashMap<>();

  public HeightMapManager(ExLocation core) {
    for (MapType type : MapType.values()) {
      HeightMap heightMap = new HeightMap(type.name().toLowerCase(), core, type.getCostCalc());
      this.mapsByType.put(type, heightMap);
    }
  }

  public HashMap<MapType, HeightMap> getMapsByType() {
    return mapsByType;
  }

  public HeightMap getDefaultMap() {
    return this.mapsByType.get(MapType.DEFAULT);
  }

  public HeightMap getMap(MapType type) {
    return this.mapsByType.get(type);
  }

  public void stopUpdater() {
    this.mapsByType.values().forEach(HeightMap::stopUpdater);
  }

  public void resetMaps() {
    this.mapsByType.values().forEach(HeightMap::reset);
  }

  public void updateMaps() {
    this.mapsByType.values().forEach(HeightMap::update);
  }

  public enum MapType {
    DEFAULT(new PathCostCalc.And(
        new PathCostCalc.StartGroundIsSolid(),
        new PathCostCalc.PathIsBreakableOrEmptyOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return BREAKABLE_MATERIALS.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return MobDefMob.BREAK_LEVEL;
          }
        },
        new PathCostCalc.PathIsEmptyOrBreakableOnXZDiagonal() {
          @Override
          public boolean isBreakable(Material material) {
            return BREAKABLE_MATERIALS.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return MobDefMob.BREAK_LEVEL;
          }
        },
        new PathCostCalc.PathIsFencedOrWalledOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return BREAKABLE_MATERIALS.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return MobDefMob.BREAK_LEVEL;
          }
        }
    )),

    /*SMALL(List.of(BlockCheck.WALK_IN_SMALL),
            List.of(BlockCheck.ON_SOLID_1H)),

    BREAKER(List.of(BlockCheck.WALK_IN, new BlockCheck.HardBreakable(2), new BlockCheck.HighBlockBreak(Mob
    .BREAK_LEVEL, BlockCheck.HIGH_BREAKABLE, BlockCheck.BREAKABLE)),
            List.of(BlockCheck.ON_SOLID_1H),
            List.of(new BlockCheck.DiagonalBlocked(BlockCheck.HIGH_BREAKABLE, BlockCheck.BREAKABLE),
                    new BlockCheck.FloorFenceWallBlocked(BlockCheck.HIGH_BREAKABLE))),


     */
    WALL_FINDER(new PathCostCalc.And(
        new PathCostCalc.StartGroundIsSolid(),
        new PathCostCalc.PathIsBreakableOrEmptyOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return PathCostCalc.BREAKABLE_MATERIALS_2.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return (int) (material.getHardness() * MobDefMob.BREAKER_HARDNESS_MULTIPLIER);
          }
        },
        new PathCostCalc.PathIsEmptyOrBreakableOnXZDiagonal() {
          @Override
          public boolean isBreakable(Material material) {
            return PathCostCalc.BREAKABLE_MATERIALS_2.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return (int) (material.getHardness() * MobDefMob.BREAKER_HARDNESS_MULTIPLIER);
          }
        },
        new PathCostCalc.PathIsFencedOrWalledOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return PathCostCalc.BREAKABLE_MATERIALS_2.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return (int) (material.getHardness() * MobDefMob.BREAKER_HARDNESS_MULTIPLIER);
          }
        }
    ));

    private final PathCostCalc costCalc;

    MapType(PathCostCalc costCalc) {
      this.costCalc = costCalc;
    }

    public PathCostCalc getCostCalc() {
      return costCalc;
    }
  }
}
