/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.ExHeightPathfinder;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.Blaze;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.bukkit.IronGolem;
import de.timesnake.library.entities.entity.bukkit.Sheep;
import de.timesnake.library.entities.entity.bukkit.Snowman;
import de.timesnake.library.entities.entity.bukkit.Villager;
import de.timesnake.library.entities.entity.bukkit.Wolf;
import de.timesnake.library.entities.entity.extension.LivingEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoal;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

public abstract class MobDefMob<M extends de.timesnake.library.entities.entity.extension.Mob> {

  public static final int BREAK_LEVEL = 16;

  public static final List<Class<? extends LivingEntity>> ATTACKER_ENTITY_CLASSES =
      List.of(de.timesnake.library.entities.entity.bukkit.Zombie.class,
          de.timesnake.library.entities.entity.bukkit.Skeleton.class,
          de.timesnake.library.entities.entity.extension.Illager.class,
          de.timesnake.library.entities.entity.bukkit.Vex.class,
          de.timesnake.library.entities.entity.bukkit.Silverfish.class,
          de.timesnake.library.entities.entity.bukkit.Endermite.class,
          de.timesnake.library.entities.entity.bukkit.Creeper.class,
          de.timesnake.library.entities.entity.bukkit.CaveSpider.class,
          de.timesnake.library.entities.entity.bukkit.Stray.class,
          de.timesnake.library.entities.entity.bukkit.Witch.class);

  public static final List<Class<? extends Monster>> ATTACKER_ENTITY_COUNT_CLASSES =
      List.of(org.bukkit.entity.Zombie.class, org.bukkit.entity.Skeleton.class,
          org.bukkit.entity.Illusioner.class, org.bukkit.entity.Witch.class,
          org.bukkit.entity.Pillager.class, org.bukkit.entity.Evoker.class,
          org.bukkit.entity.Vindicator.class, org.bukkit.entity.Creeper.class,
          org.bukkit.entity.CaveSpider.class, org.bukkit.entity.Stray.class);

  public static final List<EntityType> ATTACKER_ENTITY_TYPES = List.of(EntityType.ZOMBIE,
      EntityType.SKELETON,
      EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.ILLUSIONER, EntityType.WITCH,
      EntityType.PILLAGER,
      EntityType.EVOKER, EntityType.VINDICATOR, EntityType.VEX, EntityType.SILVERFISH,
      EntityType.ENDERMITE,
      EntityType.CREEPER, EntityType.CAVE_SPIDER, EntityType.STRAY);

  public static final List<Class<? extends de.timesnake.library.entities.entity.extension.Mob>> FIRST_DEFENDER_CLASSES =
      List.of(de.timesnake.library.entities.entity.bukkit.Sheep.class);

  public static final List<Class<? extends de.timesnake.library.entities.entity.extension.Mob>> SECOND_DEFENDER_CLASSES =
      List.of(IronGolem.class, Villager.class, Wolf.class, Snowman.class, Blaze.class);

  public static final List<Class<? extends LivingEntity>> DEFENDER_CLASSES =
      List.of(Sheep.class, HumanEntity.class, IronGolem.class, Villager.class, Wolf.class,
          Snowman.class, Blaze.class);

  public static final List<EntityType> DEFENDER_TYPES = List.of(EntityType.SHEEP,
      EntityType.PLAYER, EntityType.IRON_GOLEM, EntityType.VILLAGER, EntityType.WOLF,
      EntityType.SNOWMAN, EntityType.BLAZE);

  public static MobDefMob<?> getCompressedMob(int wave, Type type, ExLocation spawn) {
    return switch (type) {
      case COMPRESSED_MELEE -> new CompressedZombie(spawn, wave);
      case COMBRESSED_RANGED -> new CompressedSkeleton(spawn, wave);
      default -> null;
    };
  }

  static ExPathfinderGoal getCorePathfinder(HeightMapManager.MapType mapType, double speed,
      ExPathfinderGoal breakPathfinder, int breakLevel) {

    return new ExHeightPathfinder(MobDefServer.getMap().getHeightMapManager().getMap(mapType),
        3, speed, 32, 0,
        breakPathfinder, breakLevel);
  }

  static ExCustomPathfinderGoalBreakBlock getBreakPathfinder(double speed, boolean ignoreTarget,
      Collection<Material> breakable) {
    return new ExCustomPathfinderGoalBreakBlock(speed, ignoreTarget,
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
          case 3, 4 -> new Vindicator(spawn, wave);
          //case 5 -> new FollowerZombie(spawn, wave);
          case 6 -> new CaveSpider(spawn, wave);
          default -> new Zombie(spawn, wave);
        };
      }
      case RANGED -> {
        r = random.nextInt(9);
        mob = switch (r) {
          case 0, 1, 2 -> new Pillager(spawn, wave);
          default -> new Skeleton(spawn, wave);
        };
      }
      case BREAKER -> {
        r = random.nextInt(3);
        mob = switch (r) {
          case 0, 1 -> new ZombieBreaker(spawn, wave);
          case 2 -> new Creeper(spawn, wave);
          default -> mob;
        };
      }
      case OTHER -> {
        r = random.nextInt(4);
        mob = switch (r) {
          case 0 -> new BabyZombie(spawn, wave);
          case 1 -> new Witch(spawn, wave);
          case 2 -> new Evoker(spawn, wave);
          case 3 -> new CaveSpider(spawn, wave);
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
  protected List<? extends de.timesnake.library.entities.entity.extension.Mob> subEntities = new ArrayList<>();

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
    this.entity.setPersistent(true);
    this.entity.setRemoveWhenFarAway(false);
    this.entity.setPositionRotation(this.spawn.getX(), this.spawn.getY() + 1, this.spawn.getZ(),
        this.spawn.getYaw(), this.spawn.getPitch());
    EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), this.entity,
        false);

    this.entity.setMaxNoDamageTicks(1);

    for (de.timesnake.library.entities.entity.extension.Mob subEntity : this.subEntities) {
      subEntity.setPersistent(true);
      subEntity.setRemoveWhenFarAway(false);
      subEntity.setPositionRotation(this.spawn.getX(), this.spawn.getY() + 1,
          this.spawn.getZ(),
          this.spawn.getYaw(), this.spawn.getPitch());
      EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), subEntity);
    }

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
