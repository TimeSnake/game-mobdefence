/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.server;

import de.timesnake.basic.bukkit.util.user.scoreboard.Sideboard;
import de.timesnake.basic.loungebridge.util.server.LoungeBridgeServer;
import de.timesnake.game.mobdefence.map.MobDefMap;
import de.timesnake.game.mobdefence.mob.MobManager;
import de.timesnake.game.mobdefence.shop.BaseShops;
import de.timesnake.game.mobdefence.special.weapon.WeaponManager;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.UserManager;
import de.timesnake.library.basic.util.statistics.IntegerStat;
import de.timesnake.library.basic.util.statistics.StatType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

import java.util.*;
import java.util.stream.Stream;

public class MobDefServer extends LoungeBridgeServer {

  public static final double EMERALD_CHANCE = 0.08;
  public static final double GOLD_CHANCE = 0.15;
  public static final double SILVER_CHANCE = 0.4;
  public static final double BRONZE_CHANCE = 0.6;

  public static final double SPAWN_AMOUNT_MULTIPLIER = 5;
  public static final double SPAWN_AMOUNT_INCREASE = 1.6;

  public static final double SPAWN_TIME_MULTIPLIER = 14;
  public static final double SPAWN_TIME_RANDOM_RANGE = 0.4;

  public static final int MIN_GROUP_SIZE = 5;
  public static final int MAX_GROUP_SIZE = 7;
  public static final double GROUP_SIZE_INCREASE = 1.4;
  public static final double GROUP_SIZE_PLAYER_MULTIPLIER = 1.7;

  public static final int MOB_LIMIT = 50;

  public static final double MOB_DAMAGE_MULTIPLIER = 1;

  public static final int BREAK_LEVEL = 16;
  public static final int BREAKER_HARDNESS_MULTIPLIER = 2;

  public static final List<Class<? extends LivingEntity>> ATTACKER_ENTITY_CLASSES =
      List.of(net.minecraft.world.entity.monster.Zombie.class,
          net.minecraft.world.entity.monster.Skeleton.class,
          net.minecraft.world.entity.raid.Raider.class,
          net.minecraft.world.entity.monster.Vex.class,
          net.minecraft.world.entity.monster.Silverfish.class,
          net.minecraft.world.entity.monster.Endermite.class,
          net.minecraft.world.entity.monster.Creeper.class,
          net.minecraft.world.entity.monster.CaveSpider.class,
          net.minecraft.world.entity.monster.Stray.class,
          net.minecraft.world.entity.monster.Witch.class);

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

  public static final List<Class<? extends Mob>> FIRST_DEFENDER_CLASSES =
      List.of(Sheep.class);

  public static final List<Class<? extends Mob>> SECOND_DEFENDER_CLASSES =
      List.of(net.minecraft.world.entity.animal.IronGolem.class,
          net.minecraft.world.entity.npc.Villager.class,
          net.minecraft.world.entity.animal.Wolf.class,
          net.minecraft.world.entity.animal.SnowGolem.class,
          net.minecraft.world.entity.monster.Blaze.class);

  public static final List<Class<? extends Mob>> DEFENDER_CLASSES =
      Stream.concat(FIRST_DEFENDER_CLASSES.stream(), SECOND_DEFENDER_CLASSES.stream()).toList();

  public static final Set<Material> ROUNDED_BLOCK_MATERIALS = new HashSet<>();
  public static final Set<Material> EMPTY_MATERIALS = new HashSet<>();

  public static final Set<Material> BREAKABLE_MATERIALS = Set.of(
      Material.OAK_PLANKS,
      Material.OAK_SLAB,
      Material.IRON_BARS,
      Material.OAK_FENCE,
      Material.OAK_FENCE_GATE,
      Material.COBBLESTONE_WALL);

  public static final Set<Material> BREAKABLE_MATERIALS_2 = new HashSet<>();

  public static final List<Material> EXPLODEABLE = new ArrayList<>();

  static {
    ROUNDED_BLOCK_MATERIALS.addAll(Tag.SLABS.getValues());
    ROUNDED_BLOCK_MATERIALS.addAll(Tag.STAIRS.getValues());

    EMPTY_MATERIALS.add(Material.AIR);
    EMPTY_MATERIALS.addAll(Tag.CLIMBABLE.getValues());
    EMPTY_MATERIALS.addAll(Tag.CROPS.getValues());
    EMPTY_MATERIALS.addAll(Tag.WOOL_CARPETS.getValues());
    EMPTY_MATERIALS.addAll(Tag.SIGNS.getValues());
    EMPTY_MATERIALS.addAll(Tag.BANNERS.getValues());
    EMPTY_MATERIALS.addAll(Tag.BUTTONS.getValues());
    EMPTY_MATERIALS.addAll(Tag.PRESSURE_PLATES.getValues());
    EMPTY_MATERIALS.addAll(Tag.RAILS.getValues());
    EMPTY_MATERIALS.addAll(Tag.FLOWERS.getValues());
    EMPTY_MATERIALS.addAll(Tag.SAPLINGS.getValues());
    EMPTY_MATERIALS.addAll(Tag.RAILS.getValues());
    EMPTY_MATERIALS.add(Material.SNOW);
    EMPTY_MATERIALS.add(Material.WATER);
    EMPTY_MATERIALS.add(Material.LAVA);
    EMPTY_MATERIALS.add(Material.TRIPWIRE);
    EMPTY_MATERIALS.addAll(List.of(Material.GRASS, Material.TALL_GRASS, Material.ARMOR_STAND));

    BREAKABLE_MATERIALS_2.addAll(BREAKABLE_MATERIALS);
    BREAKABLE_MATERIALS_2.addAll(Tag.STONE_BRICKS.getValues());
    BREAKABLE_MATERIALS_2.addAll(Tag.FENCES.getValues());
    BREAKABLE_MATERIALS_2.addAll(Tag.WOODEN_TRAPDOORS.getValues());
    BREAKABLE_MATERIALS_2.addAll(Tag.WOODEN_DOORS.getValues());
    BREAKABLE_MATERIALS_2.addAll(Tag.WOODEN_SLABS.getValues());
    BREAKABLE_MATERIALS_2.addAll(Tag.PLANKS.getValues());
    BREAKABLE_MATERIALS_2.addAll(Tag.WALLS.getValues());

    EXPLODEABLE.addAll(MobDefServer.BREAKABLE_MATERIALS);
    EXPLODEABLE.addAll(Tag.STONE_BRICKS.getValues());
    EXPLODEABLE.addAll(Tag.FENCES.getValues());
    EXPLODEABLE.addAll(Tag.WOODEN_TRAPDOORS.getValues());
    EXPLODEABLE.addAll(Tag.WOODEN_DOORS.getValues());
    EXPLODEABLE.addAll(Tag.WOODEN_SLABS.getValues());
    EXPLODEABLE.addAll(Tag.LOGS.getValues());
    EXPLODEABLE.addAll(Tag.PLANKS.getValues());
    EXPLODEABLE.addAll(Tag.WALLS.getValues());
  }


  public static final StatType<Integer> MOB_KILLS = new IntegerStat("mob_kill", "Mob Kills",
      0, 10, 2, true, 0, 2);

  public static MobDefMap getMap() {
    return LoungeBridgeServer.getMap();
  }

  public static Integer getPlayerAmount() {
    return server.getPlayerAmount();
  }

  public static void setCoreHealth(double health) {
    server.setCoreHealth(health);
  }

  public static void removeCoreHealth(double health) {
    server.removeCoreHealth(health);
  }

  public static BossBar getCoreHealthBar() {
    return server.getCoreHealthBar();
  }

  public static LivingEntity getCoreEntity() {
    return server.getCoreEntity();
  }

  public static Integer getWaveNumber() {
    return server.getWaveNumber();
  }

  public static Sideboard getSideboard() {
    return server.getSideboard();
  }

  public static void updateSideboardPlayers() {
    server.updateSideboardPlayers();
  }

  public static void initNextWave() {
    server.initNextWave();
  }

  public static Collection<MobDefUser> getAliveUsers() {
    return server.getAliveUsers();
  }

  public static void stopGame() {
    server.stopGame();
  }

  public static MobManager getMobManager() {
    return server.getMobManager();
  }

  public static UserManager getMobDefUserManager() {
    return server.getMobDefUserManager();
  }

  public static WeaponManager getWeaponManager() {
    return server.getWeaponManager();
  }

  public static BaseShops getBaseShops() {
    return server.getBaseShops();
  }

  public static boolean isDelayRunning() {
    return server.isDelayRunning();
  }

  private static final MobDefServerManager server = MobDefServerManager.getInstance();
}
