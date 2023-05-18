/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExStray;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.bukkit.Stray;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalAvoidTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalBowShoot;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalSpawnArmy;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BossSkeletonStray extends MobDefMob<ExStray> {

  public BossSkeletonStray(ExLocation spawn, int currentWave) {
    super(Type.BOSS, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

    this.entity = new ExStray(world, false, false);

    ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false,
        BlockCheck.BREAKABLE_MATERIALS);

    this.entity.addPathfinderGoal(3,
        getCorePathfinder(this.getMapType(), 1.3, breakBlock, BREAK_LEVEL));
    this.entity.addPathfinderGoal(3, breakBlock);
    this.entity.addPathfinderGoal(3,
        new ExPathfinderGoalAvoidTarget(HumanEntity.class, 5, 1.1, 1.3));
    this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomStrollLand(1.3D));
    this.entity.addPathfinderGoal(5, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
    this.entity.addPathfinderGoal(5, new ExPathfinderGoalRandomLookaround());

    this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

    for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
      this.entity.addPathfinderGoal(1,
          new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
    }
    this.entity.addPathfinderGoal(2,
        new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

    for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
      this.entity.addPathfinderGoal(2,
          new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
    }

    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 2, 30.0F));

    this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalSpawnArmy(Stray.class, 3,
        10 * 20) {
      @Override
      public List<? extends Mob> getArmee(Mob entity) {
        List<ExStray> skeletons = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
          World world = MobDefServer.getMap().getWorld().getBukkitWorld();

          ExStray stray = new ExStray(world, false, false);

          ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.5, false,
              BlockCheck.BREAKABLE_MATERIALS);

          stray.addPathfinderGoal(2,
              getCorePathfinder(HeightMapManager.MapType.NORMAL, 1, breakBlock,
                  BREAK_LEVEL));
          stray.addPathfinderGoal(2, breakBlock);
          stray.addPathfinderGoal(3,
              new ExPathfinderGoalAvoidTarget(HumanEntity.class, 5, 1.1, 1.1));
          stray.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.9D));
          stray.addPathfinderGoal(4,
              new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
          stray.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

          stray.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

          for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            stray.addPathfinderGoal(2,
                new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
          }
          stray.addPathfinderGoal(3,
              new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

          for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            stray.addPathfinderGoal(3,
                new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
          }

          stray.setMaxNoDamageTicks(1);

          if (BossSkeletonStray.this.currentWave > 16) {
            stray.setMaxHealth(BossSkeletonStray.this.currentWave * 5);
          } else if (BossSkeletonStray.this.currentWave > 12) {
            stray.setMaxHealth(60);
            stray.setHealth(60);
          } else if (BossSkeletonStray.this.currentWave > 6) {
            stray.setMaxHealth(40);
            stray.setHealth(40);
          }

          stray.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2
              + BossSkeletonStray.this.currentWave / 5.
              * MobManager.MOB_DAMAGE_MULTIPLIER);

          stray.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.BOW));

          skeletons.add(stray);
        }

        return skeletons;
      }
    });

    this.entity.setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.GOLDEN_HELMET));
    this.entity.setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.GOLDEN_CHESTPLATE));
    this.entity.setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.GOLDEN_LEGGINGS));
    this.entity.setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.GOLDEN_BOOTS));

    this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
        new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_FIRE, 2)
            .addExEnchantment(Enchantment.ARROW_DAMAGE, 12)
            .addExEnchantment(Enchantment.ARROW_KNOCKBACK, 6));

    this.entity.getBukkitAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
    this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(10);
    this.entity.getBukkitAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);

    this.entity.setMaxHealth(this.currentWave * 70);
    this.entity.setHealth(this.currentWave * 70);

    super.spawn();
  }
}
