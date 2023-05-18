/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExPillager;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalCrossbowAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStroll;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

public class Pillager extends MobDefMob<ExPillager> {

  Pillager(ExLocation spawn, int currentWave) {
    super(Type.RANGED, HeightMapManager.MapType.NORMAL, 6, spawn, currentWave);
  }

  @Override
  public void spawn() {
    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

    ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.3, false,
        BlockCheck.BREAKABLE_MATERIALS);

    this.entity = new ExPillager(world, false, false);
    this.entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.CROSSBOW));

    this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
    this.entity.addPathfinderGoal(3, new ExPathfinderGoalCrossbowAttack(1.0, 15.0F));
    this.entity.addPathfinderGoal(4,
        getCorePathfinder(this.getMapType(), 0.7, breakBlock, BREAK_LEVEL));
    this.entity.addPathfinderGoal(4, breakBlock);
    this.entity.addPathfinderGoal(8, new ExPathfinderGoalRandomStroll(0.6));
    this.entity.addPathfinderGoal(9,
        new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 3.0F, 1.0F));
    this.entity.addPathfinderGoal(10, new ExPathfinderGoalLookAtPlayer(Mob.class, 8.0F));

    this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

    for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
      this.entity.addPathfinderGoal(2,
          new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
    }
    this.entity.addPathfinderGoal(3,
        new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

    for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
      this.entity.addPathfinderGoal(3,
          new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
    }

    if (this.currentWave > 20) {
      this.entity.setMaxHealth(this.currentWave * 5);
    } else if (this.currentWave > 18) {
      this.entity.setMaxHealth(60);
      this.entity.setHealth(60);
    } else if (this.currentWave > 11) {
      this.entity.setMaxHealth(40);
      this.entity.setHealth(40);
    }

    this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
        2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

    super.spawn();
  }
}
