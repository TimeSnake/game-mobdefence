/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.CaveSpiderBuilder;
import de.timesnake.library.entities.pathfinder.SpiderAttackGoal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import org.bukkit.attribute.Attribute;

public class CaveSpider extends MobDefMob<net.minecraft.world.entity.monster.CaveSpider> {

  public CaveSpider(ExLocation spawn, int currentWave) {
    super(Type.OTHER, HeightMapManager.MapType.NORMAL, 8, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    this.entity = new CaveSpiderBuilder(world.getHandle(), false, false)
        .setMaxHealthAndHealth(this.currentWave > 13 ? 40 : 20)
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER))
        .addPathfinderGoal(0, e -> new FloatGoal(e))
        .addPathfinderGoal(1, e -> new LeapAtTargetGoal(e, 0.4F))
        .addPathfinderGoal(2, e -> new SpiderAttackGoal(e, 1))
        .addPathfinderGoal(3, e -> getCorePathfinder(e, this.getMapType(), 1, null, BREAK_LEVEL))
        .addPathfinderGoal(4, e -> new RandomStrollGoal(e, 0.8))
        .addPathfinderGoal(5, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(5, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build();

    super.spawn();
  }
}
