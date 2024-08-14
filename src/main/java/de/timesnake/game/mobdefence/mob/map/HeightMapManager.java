/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.server.MobDefServer;
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

  public HeightMap getMap(MapType type) {
    return this.mapsByType.get(type);
  }

  public void stopUpdater() {
    HeightMap.stopUpdater();
  }

  public void resetMaps() {
    this.mapsByType.values().forEach(HeightMap::reset);
  }

  public void updateMaps() {
    this.mapsByType.values().forEach(HeightMap::update);
  }

  public enum MapType {
    DEFAULT(new PathCostCalc.And(
        new PathCostCalc.MaxPositiveDelta(1),
        new PathCostCalc.StartGroundIsSolid(),
        new PathCostCalc.PathIsBreakableOrEmptyOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return MobDefServer.BREAKABLE_MATERIALS.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return MobDefServer.BREAK_LEVEL;
          }
        },
        new PathCostCalc.PathIsEmptyOrBreakableOnXZDiagonal() {
          @Override
          public boolean isBreakable(Material material) {
            return MobDefServer.BREAKABLE_MATERIALS.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return MobDefServer.BREAK_LEVEL;
          }
        },
        new PathCostCalc.PathIsFencedOrWalledOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return MobDefServer.BREAKABLE_MATERIALS.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return MobDefServer.BREAK_LEVEL;
          }
        }
    )),
    BREAKER(new PathCostCalc.And(
        new PathCostCalc.MaxPositiveDelta(1),
        new PathCostCalc.StartGroundIsSolid(),
        new PathCostCalc.PathIsBreakableOrEmptyOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return MobDefServer.BREAKABLE_MATERIALS_2.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return (int) (material.getHardness() * MobDefServer.BREAKER_HARDNESS_MULTIPLIER);
          }
        },
        new PathCostCalc.PathIsEmptyOrBreakableOnXZDiagonal() {
          @Override
          public boolean isBreakable(Material material) {
            return MobDefServer.BREAKABLE_MATERIALS_2.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return (int) (material.getHardness() * MobDefServer.BREAKER_HARDNESS_MULTIPLIER);
          }
        },
        new PathCostCalc.PathIsFencedOrWalledOnY() {
          @Override
          public boolean isBreakable(Material material) {
            return MobDefServer.BREAKABLE_MATERIALS_2.contains(material);
          }

          @Override
          public int getCostsForBreakableMaterial(Material material) {
            return (int) (material.getHardness() * MobDefServer.BREAKER_HARDNESS_MULTIPLIER);
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
