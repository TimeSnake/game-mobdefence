/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.mob.map.HeightPathGoal;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.base.MobBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class MobDefMob<M extends Mob> {

  public static MobDefMob<?> getCompressedMob(int wave, Type type, ExLocation spawn) {
    return switch (type) {
      case COMPRESSED_MELEE -> new CompressedZombie(spawn, wave);
      case COMBRESSED_RANGED -> new CompressedSkeleton(spawn, wave);
      default -> null;
    };
  }

  static Goal getCorePathfinder(Mob mob, HeightMapManager.MapType mapType, double speed,
                                Goal breakPathfinder, int breakLevel) {

    return new HeightPathGoal(mob, MobDefServer.getMap().getHeightMapManager().getMap(mapType),
        3, speed, 32, 0, breakPathfinder, breakLevel);
  }

  static BreakBlockGoal getBreakPathfinder(Mob mob, double speed, boolean ignoreTarget,
                                           Collection<Material> breakable) {
    return new BreakBlockGoal(mob, speed, ignoreTarget,
        (block) -> MobDefServer.getMap().getHeightMapManager().updateMaps(), breakable);
  }

  public static MobDefMob<?> getRandomMob(int wave, MobDefMob.Type type, ExLocation spawn) {
    Random random = new Random();
    int r;
    MobDefMob<?> mob = null;

    switch (type) {
      case MELEE -> {
        r = random.nextInt(10);
        mob = switch (r) {
          case 3, 4 -> new MobDefVindicator(spawn, wave);
          //case 5 -> new FollowerZombie(spawn, wave);
          case 6 -> new CaveSpider(spawn, wave);
          default -> new MobDefZombie(spawn, wave);
        };
      }
      case RANGED -> {
        r = random.nextInt(9);
        mob = switch (r) {
          case 0, 1, 2 -> new MobDefPillager(spawn, wave);
          default -> new MobDefSkeleton(spawn, wave);
        };
      }
      case BREAKER -> {
        r = random.nextInt(3);
        mob = switch (r) {
          case 0, 1 -> new MobDefZombieBreaker(spawn, wave);
          case 2 -> new Creeper(spawn, wave);
          default -> mob;
        };
      }
      case OTHER -> {
        r = random.nextInt(3);
        mob = switch (r) {
          case 0 -> new MobDefBabyZombie(spawn, wave);
          case 1 -> new MobDefWitch(spawn, wave);
          case 2 -> new CaveSpider(spawn, wave);
          default -> mob;
        };
      }
    }

    if (mob != null && mob.getWave() <= wave) {
      return mob;
    }
    return getRandomMob(wave, type, spawn);
  }

  protected final Type type;
  protected final HeightMapManager.MapType mapType;
  protected final int wave;
  protected final int currentWave;
  protected final ExLocation spawn;
  protected final Random random = new Random();
  protected M entity;
  protected List<? extends Mob> subEntities = new ArrayList<>();

  MobDefMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn,
            int currentWave) {
    this.type = type;
    this.mapType = mapType;
    this.wave = wave;
    this.currentWave = currentWave;
    this.spawn = spawn;
  }

  public M getEntity() {
    return entity;
  }

  public Type getType() {
    return type;
  }

  public ExLocation getSpawn() {
    return spawn;
  }

  public HeightMapManager.MapType getMapType() {
    return mapType;
  }

  public int getWave() {
    return wave;
  }

  public void spawn() {
    this.entity.setPersistenceRequired(true);
    this.entity.setPos(this.spawn.getX(), this.spawn.getY() + 1, this.spawn.getZ());
    this.entity.setRot(this.spawn.getYaw(), this.spawn.getPitch());
    de.timesnake.library.entities.EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), this.entity);

    this.entity.invulnerableDuration = 1;

    for (Mob subEntity : this.subEntities) {
      subEntity.setPersistenceRequired(true);
      subEntity.setPos(this.spawn.getX(), this.spawn.getY() + 1, this.spawn.getZ());
      subEntity.setRot(this.spawn.getYaw(), this.spawn.getPitch());
      EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), subEntity);
    }

  }

  public <B extends MobBuilder<? extends Mob, ?>> void applyDefaultTargetGoals(B builder) {
    builder.addTargetGoal(1, e -> new HurtByTargetGoal((PathfinderMob) e, net.minecraft.world.entity.monster.Monster.class))
        .addTargetGoals(2, MobDefServer.FIRST_DEFENDER_CLASSES.stream()
            .map(defClass -> e -> new NearestAttackableTargetGoal<>(e, defClass, true, true)))
        .addTargetGoal(3, e -> new NearestAttackableTargetGoal<>(e, Player.class, true, true))
        .addTargetGoals(3, MobDefServer.SECOND_DEFENDER_CLASSES.stream()
            .map(defClass -> e -> new NearestAttackableTargetGoal<>(e, defClass, true, true)));
  }

  public enum Type {
    COMPRESSED_MELEE,
    COMBRESSED_RANGED,
    MELEE(COMPRESSED_MELEE),
    BREAKER,
    RANGED(COMBRESSED_RANGED),
    OTHER,
    BOSS;

    private final Type compressed;

    Type() {
      this.compressed = null;
    }

    Type(Type compressed) {
      this.compressed = compressed;
    }

    public Type getCompressed() {
      return compressed;
    }

    public boolean isCompressable() {
      return this.compressed != null;
    }
  }
}
