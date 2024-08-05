/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.CreeperBuilder;
import de.timesnake.library.entities.pathfinder.LocationSwellGoal;
import de.timesnake.library.entities.pathfinder.SwellGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class Creeper extends MobDefMob<net.minecraft.world.entity.monster.Creeper> {

  public Creeper(ExLocation spawn, int currentWave) {
    super(Type.OTHER, HeightMapManager.MapType.WALL_FINDER, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {

    ExWorld world = MobDefServer.getMap().getWorld();

    float health = 30;

    if (this.currentWave > 10) {
      health = this.currentWave * 10;
    } else if (this.currentWave > 5) {
      health = 40;
    }

    this.entity = new CreeperBuilder().setMaxHealthAndHealth(health).apply(b -> b.applyOnEntity(e -> {
          LocationSwellGoal swellGoal = new LocationSwellGoal(e, 4, 7);
          b.addPathfinderGoal(1, f -> swellGoal);
          if (this.currentWave > 10) {
            b.addPathfinderGoal(2, f -> getCorePathfinder(e, HeightMapManager.MapType.WALL_FINDER, 1.4, swellGoal, 5));
          } else if (this.currentWave > 5) {
            b.addPathfinderGoal(2, f -> getCorePathfinder(e, HeightMapManager.MapType.WALL_FINDER, 1.3, swellGoal, 5));
          } else {
            b.addPathfinderGoal(2, f -> getCorePathfinder(e, HeightMapManager.MapType.WALL_FINDER, 1.2, swellGoal, 5));
          }
        }))
        .addPathfinderGoal(3, e -> new SwellGoal(e, 3, 5))
        .addPathfinderGoal(4, e -> new MeleeAttackGoal(e, 1.1,
            false))
        .addPathfinderGoal(6, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(6, e -> new RandomLookAroundGoal(e))
        .addTargetGoal(1, e -> new HurtByTargetGoal(e, Monster.class))
        .addTargetGoal(2, e -> new NearestAttackableTargetGoal<>(e, Player.class, true, true))
        .build(world.getHandle());

    super.spawn();
  }
}
