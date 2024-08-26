/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.IntStream;

public class MobGroup {

  private BukkitTask task;
  private boolean spawned;

  private final int wave;
  private final ExLocation spawn;
  private final Map<MobDefMob.Type, List<MobDefMob<?>>> mobsByType = new HashMap<>();

  private final int maxDelay; // in seconds

  public MobGroup(int wave, ExLocation spawn, int amount, int maxDelay) {
    this.maxDelay = maxDelay;
    this.spawn = spawn;
    this.wave = wave;

    Random random = new Random();

    List<MobTypeGroup> mobTypeGroups;

    if (random.nextInt(16) == 0) {
      mobTypeGroups = List.of(new MobTypeGroup(MobDefMob.Type.RANGED, amount));
    } else if (random.nextInt(16) == 0) {
      mobTypeGroups = List.of(new MobTypeGroup(MobDefMob.Type.MELEE, amount));
    } else {
      mobTypeGroups = List.of(new MobTypeGroup(MobDefMob.Type.MELEE, amount / 3 + 2),
          new MobTypeGroup(MobDefMob.Type.RANGED, amount / 4 + 1),
          new MobTypeGroup(MobDefMob.Type.BREAKER, 0),
          new MobTypeGroup(MobDefMob.Type.OTHER, 1));

      while (mobTypeGroups.stream().mapToInt(MobTypeGroup::getAmount).reduce(0, Integer::sum)
          < amount) {
        mobTypeGroups.get(random.nextInt(3)).increaseAmount();
      }
    }

    for (MobTypeGroup typeGroup : mobTypeGroups) {
      List<MobDefMob<?>> mobs = this.mobsByType.computeIfAbsent(typeGroup.getType(),
          v -> new ArrayList<>());

      for (int j = 0; j < typeGroup.getAmount(); j++) {
        MobDefMob<?> mob = MobDefMob.getRandomMob(wave, typeGroup.getType(), spawn);
        mobs.add(mob);
      }
    }
  }

  public MobGroup(List<MobDefMob<?>> mobs, int maxDelay) {
    this.maxDelay = maxDelay;
    this.spawn = null;
    this.wave = 0;

    for (MobDefMob<?> mob : mobs) {
      List<MobDefMob<?>> mobTypeList = this.mobsByType.computeIfAbsent(mob.getType(),
          v -> new ArrayList<>());
      mobTypeList.add(mob);
    }
  }

  public int size() {
    return this.mobsByType.values().stream().flatMapToInt((mobs -> IntStream.of(mobs.size())))
        .sum();
  }

  public void run() {
    this.task = Server.runTaskLaterSynchrony(this::spawn, this.maxDelay * 20,
        GameMobDefence.getPlugin());
  }

  public void spawn() {
    if (this.spawned) {
      return;
    }

    this.cancel();

    this.spawned = true;

    if (!MobDefServer.getMobManager().compressGroups()) {
      for (MobDefMob.Type type : MobDefMob.Type.values()) {
        if (this.mobsByType.containsKey(type)) {
          for (MobDefMob<?> mob : this.mobsByType.get(type)) {
            mob.spawn();
          }
        }
      }
    } else {
      for (MobDefMob.Type type : MobDefMob.Type.values()) {
        if (this.mobsByType.containsKey(type)) {
          if (type.isCompressable()) {
            int size = this.mobsByType.get(type).size();
            for (int i = 0; i < size; i += 5) {
              MobDefMob<?> compressed = MobDefMob.getCompressedMob(this.wave, type.getCompressed(), this.spawn);
              compressed.spawn();
            }
          } else {
            for (MobDefMob<?> mob : this.mobsByType.get(type)) {
              mob.spawn();
            }
          }
        }
      }
    }
  }

  public void cancel() {
    if (this.task != null) {
      this.task.cancel();
    }
  }

  public boolean isSpawned() {
    return spawned;
  }

  private static class MobTypeGroup {

    private final MobDefMob.Type type;
    private int amount;

    public MobTypeGroup(MobDefMob.Type type, int amount) {
      this.type = type;
      this.amount = amount;
    }

    public MobDefMob.Type getType() {
      return type;
    }

    public int getAmount() {
      return amount;
    }

    public void increaseAmount() {
      this.amount++;
    }
  }
}
