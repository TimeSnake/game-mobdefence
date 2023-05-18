/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExVindicator;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStroll;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalMeleeAttackVindicator;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Vindicator extends MobDefMob<ExVindicator> {

  public Vindicator(ExLocation spawn, int currentWave) {
    super(Type.MELEE, HeightMapManager.MapType.NORMAL, 7, spawn, currentWave);
  }

  @Override
  public void spawn() {
    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

    ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.3, false,
        BlockCheck.BREAKABLE_MATERIALS);

    this.entity = new ExVindicator(world, false, false);
    this.entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.IRON_AXE));

    this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
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

    if (this.currentWave < 13) {
      this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalMeleeAttackVindicator(1));
    } else if (this.currentWave < 19) {
      this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalMeleeAttackVindicator(1.1));
    } else {
      this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalMeleeAttackVindicator(1.2));
    }

    super.spawn();

    if (this.currentWave > 18) {
      this.entity.setMaxHealth(this.currentWave * 6);
      this.entity.setHealth(this.currentWave * 6);
    } else if (this.currentWave > 13) {
      this.entity.setMaxHealth(80);
      this.entity.setHealth(80);
    } else if (this.currentWave > 8) {
      this.entity.setMaxHealth(40);
      this.entity.setHealth(40);
    }
  }

}
