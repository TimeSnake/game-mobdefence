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
import de.timesnake.library.entities.entity.SkeletonBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class MobDefSkeleton extends ArmorMob<Skeleton> {

  public MobDefSkeleton(ExLocation spawn, int currentWave) {
    super(Type.RANGED, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    this.init();
    super.spawn();
  }

  public void init() {
    ExWorld world = MobDefServer.getMap().getWorld();

    float health = 20;

    if (this.currentWave > 16) {
      health = this.currentWave * 5;
    } else if (this.currentWave > 12) {
      health = 60;
    } else if (this.currentWave > 6) {
      health = 40;
    }

    this.entity = new SkeletonBuilder()
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER))
        .setMaxHealthAndHealth(health)
        .apply(b -> b.applyOnEntity(e -> {
          if (this.currentWave < 3) {
            switch (this.random.nextInt(8)) {
              case 0, 1 -> {
                e.setItemSlot(EquipmentSlot.MAINHAND,
                    new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE, 2).getHandle());
                e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.2, 20, 30.0F));
              }
              default -> {
                e.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.BOW).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.1, 20, 15.0F));
              }
            }
          } else if (this.currentWave < 11) {
            switch (this.random.nextInt(this.currentWave < 7 ? 15 : 10)) {
              case 0, 1, 2, 5 -> {
                e.setItemSlot(EquipmentSlot.MAINHAND,
                    new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE, 4).getHandle());
                e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.2, 10, 30.0F));
              }
              case 3 -> {
                e.setItemSlot(EquipmentSlot.MAINHAND,
                    new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_FIRE, 1).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.1, 10, 15.0F));
              }
              default -> {
                e.setItemSlot(EquipmentSlot.MAINHAND,
                    new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE, 2).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.1, 10, 15.0F));
              }
            }
          } else {
            switch (this.random.nextInt(8)) {
              case 0, 1 -> {
                e.setItemSlot(EquipmentSlot.MAINHAND,
                    new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_FIRE, 1).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.1, 10, 15.0F));
              }
              default -> {
                e.setItemSlot(EquipmentSlot.MAINHAND,
                    new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE, this.currentWave / 4).getHandle());
                e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET).getHandle());
                b.addPathfinderGoal(1, f -> new RangedBowAttackGoal<>(e, 1.2, 10, 30.0F));
              }
            }
          }
        }))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.4, false,
              PathCostCalc.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 1, breakBlock, BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(2, e -> new AvoidEntityGoal<>(e, Player.class, 5, 1, 1))
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 0.9D))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());
  }
}
