/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.ZombieBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.attribute.Attribute;

public class MobDefZombie extends MeleeMob<Zombie> {

  private static final double RUNNER_CHANCE = 0.3;

  public MobDefZombie(ExLocation spawn, int currentWave) {
    super(Type.COMPRESSED_MELEE, HeightMapManager.MapType.DEFAULT, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    this.init();
    super.spawn();
  }

  public void init() {
    ExWorld world = MobDefServer.getMap().getWorld();

    double speed;
    if (this.currentWave > 13) {
      speed = 1.3;
    } else if (this.currentWave > 10) {
      speed = 1.2;
    } else if (this.currentWave > 5) {
      speed = 1.1;
    } else {
      speed = 1;
    }

    boolean isRunner = Math.random() < RUNNER_CHANCE;

    float health = 20;

    if (this.currentWave > 11) {
      health = this.currentWave * 5;
    } else if (this.currentWave > 6) {
      health = 40;
    }

    this.entity = new ZombieBuilder()
        .setMaxHealthAndHealth(health)
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobDefServer.MOB_DAMAGE_MULTIPLIER))
        .addPathfinderGoal(1, e -> new ZombieAttackGoal(e, speed + (isRunner ? 0.2 : 0), false))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.4, false,
              MobDefServer.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), speed + (isRunner ? 0.2 : 0),
              breakBlock, MobDefServer.BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 1.2))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());

  }
}
