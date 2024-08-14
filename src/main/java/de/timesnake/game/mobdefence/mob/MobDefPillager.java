/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.PillagerBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public class MobDefPillager extends MobDefMob<Pillager> {

  MobDefPillager(ExLocation spawn, int currentWave) {
    super(Type.RANGED, HeightMapManager.MapType.DEFAULT, 6, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    float health = 20;

    if (this.currentWave > 20) {
      health = this.currentWave * 5;
    } else if (this.currentWave > 18) {
      health = 60;
    } else if (this.currentWave > 11) {
      health = 40;
    }

    this.entity = new PillagerBuilder()
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + this.currentWave / 5D * MobDefServer.MOB_DAMAGE_MULTIPLIER))
        .setMaxHealthAndHealth(health)
        .applyOnEntity(e -> e.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.CROSSBOW).getHandle()))
        .addPathfinderGoal(0, e -> new FloatGoal(e))
        .addPathfinderGoal(3, e -> new RangedCrossbowAttackGoal<>(e, 1, 15))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.3, false,
              MobDefServer.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 0.7, breakBlock,
              MobDefServer.BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(8, e -> new RandomStrollGoal(e, 0.6))
        .addPathfinderGoal(9, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());

    super.spawn();
  }
}
