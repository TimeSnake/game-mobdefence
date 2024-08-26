/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.VillagerBuilder;
import de.timesnake.library.entities.pathfinder.LocationGoal;
import net.minecraft.world.entity.npc.Villager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public class MobManager implements Listener {

  private final Logger logger = LogManager.getLogger("mob-def.mob.manager");

  private final LinkedList<MobGroup> mobGroups = new LinkedList<>();

  private final Random random = new Random();

  private final MobDropManager dropManager;

  public MobManager() {
    Server.registerListener(this, GameMobDefence.getPlugin());
    this.dropManager = new MobDropManager();
  }

  @EventHandler
  public void onMobDeath(EntityDeathEvent e) {
    this.checkRespawn();
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
    if (!(e.getEntity() instanceof Creeper)) {
      return;
    }

    Server.runTaskLaterSynchrony(this::checkRespawn, 1, GameMobDefence.getPlugin());
  }

  private void checkRespawn() {
    int alive = MobDefServer.getMap().getWorld().getEntitiesByClasses(
        MobDefServer.ATTACKER_ENTITY_COUNT_CLASSES.toArray(new Class[0])).size();

    if (alive <= 3 * MobDefServer.getPlayerAmount() * Math.sqrt(MobDefServer.getWaveNumber())) {
      boolean allSpawned = true;
      for (MobGroup mobGroup : this.mobGroups) {
        if (!mobGroup.isSpawned()) {
          mobGroup.spawn();
          allSpawned = false;
          break;
        }
      }

      if (allSpawned && alive == 0 && MobDefServer.isGameRunning() && !MobDefServer.isDelayRunning()) {
        Server.broadcastSound(Sound.ITEM_GOAT_HORN_SOUND_1, 2);
        MobDefServer.initNextWave();
      }

    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
      return;
    }

    if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getDamager().getType())) {
      return;
    }

    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
      e.setDamage(e.getDamage() / 6);
    }

    if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
      e.setDamage(e.getDamage() / 2);
    }
  }

  public boolean compressGroups() {
    return MobDefServer.getMap().getWorld()
               .getEntitiesByClasses(MobDefServer.ATTACKER_ENTITY_COUNT_CLASSES.toArray(new Class[0]))
               .size() > MobDefServer.MOB_LIMIT;
  }

  public Collection<Entity> getAliveMobs() {
    return MobDefServer.getMap().getWorld().getEntitiesByClasses(
        MobDefServer.ATTACKER_ENTITY_COUNT_CLASSES.toArray(new Class[0]));
  }

  public void cancelSpawning() {
    for (MobGroup mobGroup : this.mobGroups) {
      mobGroup.cancel();
    }
  }

  public Villager createCoreEntity() {
    ExLocation loc = MobDefServer.getMap().getCoreLocation();
    return new VillagerBuilder()
        .addPathfinderGoal(1, e -> new LocationGoal(e, loc.getX(), loc.getY(), loc.getZ(), 1.4, 32, 1))
        .applyOnEntity(e -> {
          e.setPos(loc.getX(), loc.getY(), loc.getZ());
          e.setRot(loc.getYaw(), loc.getPitch());
          e.setPersistenceRequired(true);
        })
        .setMaxHealthAndHealth(2048)
        .build(loc.getExWorld().getHandle());
  }

  public void spawnWave() {
    int wave = MobDefServer.getWaveNumber();

    double waveSqrt = Math.sqrt(wave);
    double players = MobDefServer.getPlayerAmount();
    double playerSqrt = Math.sqrt(players);

    this.logger.info("Spawning wave {}...", wave);
    this.mobGroups.clear();

    int totalMobAmount =
        (int) (MobDefServer.SPAWN_AMOUNT_MULTIPLIER * Math.sqrt(wave) * MobDefServer.SPAWN_AMOUNT_INCREASE * players
            * this.nextLimitedGaussian(0.2));
    int mobAmount = totalMobAmount;

    int totalTime = (int) (Math.log(totalMobAmount) * MobDefServer.SPAWN_TIME_MULTIPLIER);

    int minGroupSize = (int) (MobDefServer.MIN_GROUP_SIZE + waveSqrt * MobDefServer.GROUP_SIZE_INCREASE
                              + playerSqrt * MobDefServer.GROUP_SIZE_PLAYER_MULTIPLIER);
    int maxGroupSize = ((int) (MobDefServer.MAX_GROUP_SIZE + waveSqrt * MobDefServer.GROUP_SIZE_INCREASE
                               + playerSqrt * MobDefServer.GROUP_SIZE_PLAYER_MULTIPLIER));

    List<Integer> groupSizes = new ArrayList<>();

    while (totalMobAmount > 0) {
      int groupSize = this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize;
      groupSizes.add(groupSize);
      totalMobAmount -= groupSize;
    }

    int groupAmount = groupSizes.size();
    int delay = totalTime / groupAmount;

    this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(),
            groupSizes.get(0), 1));

    for (int i = 1; i < groupSizes.size(); i++) {
      int groupDelay = (int) (this.nextLimitedGaussian(MobDefServer.SPAWN_TIME_RANDOM_RANGE) * delay) + delay * (i - 1);
      this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(),
              groupSizes.get(i), groupDelay));
    }

    if (wave % 5 == 0) {
      List<MobDefMob<?>> bossMobs = new ArrayList<>();
      for (int i = 0; i < playerSqrt; i++) {
        bossMobs.add(new BossZombie(MobDefServer.getMap().getRandomMobPath().getLocation(), wave));
      }

      delay =
          (int) (this.nextLimitedGaussian(MobDefServer.SPAWN_TIME_RANDOM_RANGE) * delay) + delay * (groupSizes.size() - 1);

      MobGroup bossGroup = new MobGroup(bossMobs, delay);
      this.mobGroups.addLast(bossGroup);

      this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(),
              this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));
      this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(),
              this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));

      this.logger.info("Bosses: {}", bossGroup.size());
    }

    if (wave % 5 == 1 && wave > 5) {
      List<MobDefMob<?>> bossMobs = new ArrayList<>();
      for (int i = 0; i < playerSqrt; i++) {
        bossMobs.add(new BossSkeletonStray(MobDefServer.getMap().getRandomMobPath().getLocation(), wave));
      }

      delay =
          (int) (this.nextLimitedGaussian(MobDefServer.SPAWN_TIME_RANDOM_RANGE) * delay) + delay * (groupSizes.size() - 1);

      MobGroup bossGroup = new MobGroup(bossMobs, delay);
      this.mobGroups.addLast(bossGroup);

      this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(),
              this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));
      this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(),
              this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));

      this.logger.info("Bosses: {}", bossGroup.size());
    }

    for (MobGroup mobGroup : mobGroups) {
      mobGroup.run();
    }

    this.logger.info("Mobs: {} in {} groups and bosses", mobAmount, groupAmount);

  }

  private double nextLimitedGaussian() {
    double d = this.random.nextGaussian();
    return d >= -1 && d < 1 ? d + 1 : this.nextLimitedGaussian();
  }

  private double nextLimitedGaussian(double range) {
    return range * this.nextLimitedGaussian() + 1 - range / 2;
  }

}
