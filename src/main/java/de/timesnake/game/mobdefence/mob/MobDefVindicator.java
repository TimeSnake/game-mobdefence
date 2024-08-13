/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.mob.map.PathCostCalc;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.VindicatorBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import de.timesnake.library.entities.pathfinder.VindicatorMeleeAttackGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;

public class MobDefVindicator extends MobDefMob<Vindicator> {

  public MobDefVindicator(ExLocation spawn, int currentWave) {
    super(Type.MELEE, HeightMapManager.MapType.DEFAULT, 7, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    float health = 20;

    if (this.currentWave > 18) {
      health = this.currentWave * 6;
    } else if (this.currentWave > 13) {
      health = 80;
    } else if (this.currentWave > 8) {
      health = 40;
    }

    this.entity = new VindicatorBuilder()
        .setMaxHealthAndHealth(health)
        .applyOnEntity(e -> e.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_AXE).getHandle()))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.3, false,
              PathCostCalc.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 0.7, breakBlock, BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(0, e -> new FloatGoal(e))
        .addPathfinderGoal(3, e -> new VindicatorMeleeAttackGoal(e, this.currentWave < 13 ? 1 : this.currentWave < 19 ? 1.1 : 1.2))
        .addPathfinderGoal(8, e -> new RandomStrollGoal(e, 0.6))
        .addPathfinderGoal(9, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());

    super.spawn();
  }

}
