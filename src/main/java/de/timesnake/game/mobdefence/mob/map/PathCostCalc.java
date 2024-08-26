/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

import java.util.function.Function;

public abstract class PathCostCalc implements Function<ShortPath, PathCostResult> {

  public static class MaxPositiveDelta extends PathCostCalc {

    private final int max;

    public MaxPositiveDelta(int max) {
      this.max = max;
    }

    @Override
    public PathCostResult apply(ShortPath shortPath) {
      if (shortPath.heightDelta() > max) {
        return PathCostResult.BLOCKED;
      }
      return PathCostResult.EMPTY;
    }
  }

  public static class StartGroundIsSolid extends PathCostCalc {
    @Override
    public PathCostResult apply(ShortPath path) {
      ExBlock groundBlock = path.start().down();

      if (!groundBlock.getType().isSolid()) {
        return PathCostResult.BLOCKED;
      }

      if (this.isEmpty(groundBlock)) {
        return PathCostResult.BLOCKED;
      }

      return PathCostResult.EMPTY;
    }
  }

  public static abstract class PathIsBreakableOrEmptyOnY extends Breakable {
    @Override
    public PathCostResult apply(ShortPath path) {
      ExBlock start = path.start();
      ExBlock end = path.end();
      int heightDelta = path.heightDelta();

      if (heightDelta > 1) {
        return PathCostResult.BLOCKED;
      }

      Integer startCosts = this.getCostsForBlock(start);
      Integer startTopCosts = this.getCostsForBlock(start.up());

      if (startCosts == null || startTopCosts == null) {
        return PathCostResult.BLOCKED;
      }

      PathCostResult result = new PathCostResult();
      result.addBlockToBreakOnStart(startCosts, start);
      result.addBlockToBreakOnStart(startTopCosts, start.up());

      // go down -> all blocks up to start top must be breakable on end
      while (heightDelta < 0) {
        ExBlock block = end.getRelative(0, -heightDelta + 1, 0);
        Integer costs = this.getCostsForBlock(block);
        if (costs == null) {
          return PathCostResult.BLOCKED;
        }
        result.addBlockToBreakToNext(costs, block);
        heightDelta++;
      }

      // go up -> block above start top must be empty
      if (heightDelta > 0) {
        ExBlock block = start.up().up();
        Integer costs = this.getCostsForBlock(block);
        if (costs == null) {
          return PathCostResult.BLOCKED;
        }
        result.addBlockToBreakToNext(costs, block);
      }

      return result;
    }
  }

  public abstract static class PathIsEmptyOrBreakableOnXZDiagonal extends Breakable {
    @Override
    public PathCostResult apply(ShortPath path) {
      ExBlock start = path.start();
      final int heightDelta = path.heightDelta();

      final Location startLoc = path.start().getLocation();
      final Location finishLoc = path.end().getLocation();


      int deltaX = finishLoc.getBlockX() - startLoc.getBlockX();
      int deltaZ = finishLoc.getBlockZ() - startLoc.getBlockZ();

      if (deltaX == 0 || deltaZ == 0) {
        return PathCostResult.EMPTY;
      }

      if (heightDelta > 1) {
        return PathCostResult.BLOCKED;
      }

      ExBlock x = start.getRelative(deltaX, 0, 0);
      ExBlock xTop = x.up();
      ExBlock xTop2 = xTop.up();
      ExBlock xBottom = x.down();

      ExBlock z = start.getRelative(0, 0, deltaZ);
      ExBlock zTop = z.up();
      ExBlock zTop2 = zTop.up();
      ExBlock zBottom = z.down();

      boolean xSolid = this.isSolid(x);
      boolean xBottomSolid = this.isSolid(xBottom);

      boolean zSolid = this.isSolid(z);
      boolean zBottomSolid = this.isSolid(zBottom);

      Integer xCosts = this.getCostsForBlock(x);
      Integer xTopCosts = this.getCostsForBlock(xTop);
      Integer xTop2Costs = this.getCostsForBlock(xTop2);

      Integer zCosts = this.getCostsForBlock(z);
      Integer zTopCosts = this.getCostsForBlock(zTop);
      Integer zTop2Costs = this.getCostsForBlock(zTop2);

      PathCostResult result = new PathCostResult();

      if (heightDelta <= 0
          && xCosts != null && xTopCosts != null
          && zCosts != null && zTopCosts != null) {
        if (xCosts + xTopCosts <= zCosts + zTopCosts) {
          result.addBlockToBreakToNext(xCosts, x);
          result.addBlockToBreakToNext(xTopCosts, xTop);
        } else {
          result.addBlockToBreakToNext(zCosts, z);
          result.addBlockToBreakToNext(zTopCosts, zTop);
        }
        return result;
      }

      if (xTopCosts == null && zTopCosts == null) {
        return PathCostResult.BLOCKED;
      }

      if (heightDelta < 0) {
        if (xCosts != null && xTopCosts != null) {
          ExBlock current = start;
          for (int delta = heightDelta; delta <= 1; delta++) {
            if (this.isSolid(current)) {
              result.addBlockToBreakToNext(xCosts, x);
              result.addBlockToBreakToNext(xTopCosts, xTop);
              return result;
            }
            current = current.down();
          }
        }
        if (xTopCosts != null && xTop2Costs != null) {
          if (xSolid) {
            result.addBlockToBreakToNext(xTopCosts, xTop);
            result.addBlockToBreakToNext(xTop2Costs, xTop2);
            return result;
          }
        }
        if (zCosts != null && zTopCosts != null) {
          ExBlock current = start;
          for (int delta = heightDelta; delta <= 1; delta++) {
            if (this.isSolid(current)) {
              result.addBlockToBreakToNext(zCosts, z);
              result.addBlockToBreakToNext(zTopCosts, zTop);
              return result;
            }
            current = current.down();
          }
        }
        if (zTopCosts != null && zTop2Costs != null) {
          if (zSolid) {
            result.addBlockToBreakToNext(zTopCosts, zTop);
            result.addBlockToBreakToNext(zTop2Costs, zTop2);
            return result;
          }
        }
      } else if (heightDelta == 0) {
        if (xCosts != null && xTopCosts != null) {
          if (xBottomSolid) {
            result.addBlockToBreakToNext(xCosts, x);
            result.addBlockToBreakToNext(xTopCosts, xTop);
            return result;
          }
        }

        if (xTopCosts != null && xTop2Costs != null) {
          if (xSolid) {
            result.addBlockToBreakToNext(xTopCosts, xTop);
            result.addBlockToBreakToNext(xTop2Costs, xTop2);
            return result;
          }
        }

        if (zCosts != null && zTopCosts != null) {
          if (zBottomSolid) {
            result.addBlockToBreakToNext(zCosts, z);
            result.addBlockToBreakToNext(zTopCosts, zTop);
            return result;
          }
        }

        if (zTopCosts != null && zTop2Costs != null) {
          if (zSolid) {
            result.addBlockToBreakToNext(zTopCosts, zTop);
            result.addBlockToBreakToNext(zTop2Costs, zTop2);
            return result;
          }
        }
      } else {
        if (xTopCosts != null && xTop2Costs != null) {
          if (xSolid || xBottomSolid || (zTopCosts != null && zTop2Costs != null)) {
            result.addBlockToBreakToNext(xTopCosts, xTop);
            result.addBlockToBreakToNext(xTop2Costs, xTop2);
            return result;
          }
        }
        if (zTopCosts != null && zTop2Costs != null) {
          if (zSolid || zBottomSolid) {
            result.addBlockToBreakToNext(zTopCosts, zTop);
            result.addBlockToBreakToNext(zTop2Costs, zTop2);
            return result;
          }
        }
      }

      return PathCostResult.BLOCKED;
    }
  }

  public static abstract class PathWithSlabsIsEmptyOrBreakableOnY extends Breakable {
    @Override
    public PathCostResult apply(ShortPath path) {
      ExBlock start = path.start();
      ExBlock startGround = start.down();
      ExBlock end = path.end();
      ExBlock endGround = end.down();
      int heightDelta = path.heightDelta();

      if (heightDelta <= 0) {
        return PathCostResult.EMPTY;
      }

      if (Tag.SLABS.isTagged(startGround.getType())) {
        if (Tag.SLABS.isTagged(endGround.getType())) {
          return PathCostResult.EMPTY;
        }

        Integer endGroundCosts = this.getCostsForBlock(endGround);
        if (endGroundCosts == null) {
          return PathCostResult.BLOCKED;
        }
        PathCostResult result = new PathCostResult();
        result.addBlockToBreakToNext(endGroundCosts, endGround);
        return result;
      }
      return PathCostResult.EMPTY;
    }
  }

  public static abstract class PathWithSlabsIsEmptyOrBreakableOnXZDiagonal extends Breakable {
    @Override
    public PathCostResult apply(ShortPath shortPath) {
      // TODO
      return null;
    }
  }

  public static abstract class PathIsFencedOrWalledDiagonal extends Breakable {
    @Override
    public PathCostResult apply(ShortPath path) {
      // TODO
      return PathCostResult.EMPTY;
    }
  }

  public static abstract class PathIsFencedOrWalledOnY extends Breakable {
    @Override
    public PathCostResult apply(ShortPath path) {
      ExBlock start = path.start();
      ExBlock startTop2 = start.up().up();
      ExBlock startGround = start.down();
      ExBlock end = path.end();
      ExBlock endGround = end.down();
      ExBlock endTop2 = end.up().up();
      int heightDelta = path.heightDelta();

      boolean startFenced = this.isFenceOrWall(startGround);
      boolean endFenced = this.isFenceOrWall(endGround);

      Integer startTop2Costs = this.getCostsForBlock(startTop2);
      Integer startGroundCosts = this.getCostsForBlock(startGround);
      Integer endTop2Costs = this.getCostsForBlock(endTop2);
      Integer endGroundCosts = this.getCostsForBlock(endGround);

      if (!startFenced && !endFenced) {
        return PathCostResult.EMPTY;
      }

      if (startFenced && startGroundCosts == null && startTop2Costs == null) {
        return PathCostResult.BLOCKED;
      }

      PathCostResult result = new PathCostResult();

      if (heightDelta == 0) {
        if (startFenced) {
          if (endFenced) {
            if (startTop2Costs == null) {
              if (endGroundCosts != null) {
                result.addBlockToBreakToNext(startGroundCosts, startGround);
                result.addBlockToBreakToNext(endGroundCosts, endGround);
                return result;
              }
              return PathCostResult.BLOCKED;
            }

            if (startGroundCosts == null) {
              if (endTop2Costs != null) {
                result.addBlockToBreakToNext(startTop2Costs, startTop2);
                result.addBlockToBreakToNext(endTop2Costs, endTop2);
                return result;
              }
              return PathCostResult.BLOCKED;
            }

            if (endTop2Costs == null) {
              if (endGroundCosts != null) {
                result.addBlockToBreakToNext(startGroundCosts, startGround);
                result.addBlockToBreakToNext(endGroundCosts, endGround);
                return result;
              }
              return PathCostResult.BLOCKED;
            }

            if (endGroundCosts == null) {
              result.addBlockToBreakToNext(startTop2Costs, startTop2);
              result.addBlockToBreakToNext(endTop2Costs, endTop2);
              return result;
            }

            if (startTop2Costs + endTop2Costs <= startGroundCosts + endGroundCosts) {
              result.addBlockToBreakToNext(startTop2Costs, startTop2);
              result.addBlockToBreakToNext(endTop2Costs, endTop2);
            } else {
              result.addBlockToBreakToNext(startGroundCosts, startGround);
              result.addBlockToBreakToNext(endGroundCosts, endGround);
            }

            return result;
          }

          if (startTop2Costs == null || endTop2Costs == null) {
            if (startGroundCosts != null) {
              result.addBlockToBreakOnStart(startGroundCosts, startGround);
              return result;
            }
            return PathCostResult.BLOCKED;
          }

          if (startGroundCosts == null) {
            result.addBlockToBreakOnStart(startTop2Costs, startTop2);
            result.addBlockToBreakToNext(endTop2Costs, endTop2);
            return result;
          }

          if (startTop2Costs + endTop2Costs <= startGroundCosts) {
            result.addBlockToBreakOnStart(startTop2Costs, startTop2);
            result.addBlockToBreakToNext(endTop2Costs, endTop2);
          } else {
            result.addBlockToBreakOnStart(startGroundCosts, startGround);
          }
          return result;
        }

        if (startTop2Costs == null) {
          if (endGroundCosts != null) {
            result.addBlockToBreakToNext(endGroundCosts, endGround);
            return result;
          }
          return PathCostResult.BLOCKED;
        }

        if (endTop2Costs == null) {
          if (endGroundCosts != null) {
            result.addBlockToBreakToNext(endGroundCosts, endGround);
            return result;
          }
          return PathCostResult.BLOCKED;
        }

        result.addBlockToBreakToNext(startTop2Costs, startTop2);
        result.addBlockToBreakToNext(endTop2Costs, endTop2);
        return result;
      } else if (heightDelta == 1) {
        if (endFenced) {
          if (startFenced) {
            if (startTop2Costs != null) {
              result.addBlockToBreakOnStart(startTop2Costs, startTop2.up());
            }
            return result;
          }
          return PathCostResult.BLOCKED;
        }
        return PathCostResult.EMPTY; // handled by path on y
      } else if (heightDelta < 0) {
        if (startFenced) {
          if (startTop2Costs == null) {
            result.addBlockToBreakOnStart(startGroundCosts, startGround);
            return result;
          }
          if (startGroundCosts == null) {
            result.addBlockToBreakOnStart(startTop2Costs, startTop2);
            return result;
          }

          if (startTop2Costs <= startGroundCosts) {
            result.addBlockToBreakOnStart(startTop2Costs, startTop2);
          } else {
            result.addBlockToBreakOnStart(startGroundCosts, startGround);
          }
          return result;
        }
      }

      return PathCostResult.EMPTY;
    }

    public boolean isFenceOrWall(ExBlock block) {
      return Tag.FENCES.isTagged(block.getType()) || Tag.WALLS.isTagged(block.getType());
    }

  }

  public static class And extends PathCostCalc {

    private final PathCostCalc[] calcs;

    public And(PathCostCalc... calcs) {
      this.calcs = calcs;
    }

    @Override
    public PathCostResult apply(ShortPath possibleHeightPath) {
      PathCostResult overallResult = new PathCostResult();
      for (PathCostCalc check : calcs) {
        PathCostResult result = check.apply(possibleHeightPath);
        if (result.isBlocked()) {
          return PathCostResult.BLOCKED;
        }
        overallResult = overallResult.merge(result);
      }
      return overallResult;
    }

  }

  public static class Or extends PathCostCalc {

    private final PathCostCalc[] calcs;

    public Or(PathCostCalc... calcs) {
      this.calcs = calcs;
    }

    @Override
    public PathCostResult apply(ShortPath shortPath) {
      PathCostResult bestResult = PathCostResult.BLOCKED;
      for (PathCostCalc check : calcs) {
        PathCostResult result = check.apply(shortPath);

        if (result.isBlocked()) {
          continue;
        }

        if (result.isEmpty()) {
          return result;
        }

        if (result.compareTo(bestResult) < 0) {
          bestResult = result;
        }
      }
      return bestResult;
    }

  }

  private static abstract class Breakable extends PathCostCalc {

    protected Integer getCostsForMaterial(Material material) {
      if (this.isBreakable(material)) {
        return this.getCostsForBreakableMaterial(material);
      }
      if (MobDefServer.EMPTY_MATERIALS.contains(material)) {
        return 0;
      }
      return null;
    }

    protected Integer getCostsForBlock(ExBlock block) {
      return this.getCostsForMaterial(block.getType());
    }

    protected boolean isBreakable(ExBlock block) {
      return this.isBreakable(block.getType());
    }

    public abstract boolean isBreakable(Material material);

    public abstract int getCostsForBreakableMaterial(Material material);

  }

  public boolean isEmpty(Material material) {
    return MobDefServer.EMPTY_MATERIALS.contains(material);
  }

  public boolean isEmpty(Block block) {
    return this.isEmpty(block.getType());
  }

  public boolean isEmpty(ExBlock block) {
    return this.isEmpty(block.getType());
  }

  public boolean isEmpty(Location location) {
    return this.isEmpty(location.getBlock());
  }

  public boolean isFence(ExBlock block) {
    return Tag.FENCES.isTagged(block.getType());
  }

  public boolean isWall(ExBlock block) {
    return Tag.WALLS.isTagged(block.getType());
  }

  public boolean isSolid(Material material) {
    return !MobDefServer.EMPTY_MATERIALS.contains(material);
  }

  public boolean isSolid(ExBlock block) {
    return this.isSolid(block.getType());
  }
}
