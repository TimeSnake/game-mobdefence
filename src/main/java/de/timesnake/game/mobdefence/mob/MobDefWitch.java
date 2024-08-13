/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.mob.map.PathCostCalc;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.WitchBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class MobDefWitch extends MobDefMob<net.minecraft.world.entity.monster.Witch> {

  public MobDefWitch(ExLocation spawn, int currentWave) {
    super(Type.OTHER, HeightMapManager.MapType.DEFAULT, 3, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    float health = 20;

    if (this.currentWave > 7) {
      health = 80;
    } else {
      health = 60;
    }

    this.entity = new WitchBuilder()
        .setMaxHealthAndHealth(health)
        .addPathfinderGoal(1, e -> new FloatGoal(e))
        .addPathfinderGoal(2, e -> new RangedAttackGoal(e, 1.0D, 60, 10.0F))
        .addPathfinderGoal(2, e -> new RandomStrollGoal(e, 1))
        .addPathfinderGoal(3, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.2, false,
              PathCostCalc.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 1.5, breakBlock, BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addTargetGoal(1, e -> new HurtByTargetGoal(e, Monster.class))
        .addTargetGoals(2, MobDefMob.FIRST_DEFENDER_CLASSES.stream()
            .map(defClass -> e -> new NearestAttackableTargetGoal<>(e, defClass, 10, true, false, null)))
        .addTargetGoal(3, e -> new NearestAttackableTargetGoal<>(e, Player.class, 10, true, false, null))
        .addTargetGoals(3, MobDefMob.SECOND_DEFENDER_CLASSES.stream()
            .map(defClass -> e -> new NearestAttackableTargetGoal<>(e, defClass, 10, true, false, null)))
        .build(world.getHandle());

    super.spawn();
  }
}
