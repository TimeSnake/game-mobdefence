/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.StrayBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import de.timesnake.library.entities.pathfinder.SpawnArmyGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BossSkeletonStray extends MobDefMob<Stray> {

  public BossSkeletonStray(ExLocation spawn, int currentWave) {
    super(Type.BOSS, HeightMapManager.MapType.DEFAULT, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    this.entity = new StrayBuilder()
        .applyOnEntity(e -> e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + this.currentWave / 5D * MobDefServer.MOB_DAMAGE_MULTIPLIER))
        .setMaxHealthAndHealth(this.currentWave * 70)
        .applyOnEntity(e -> {
          e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.GOLDEN_HELMET).getHandle());
          e.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.GOLDEN_CHESTPLATE).getHandle());
          e.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.GOLDEN_LEGGINGS).getHandle());
          e.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.GOLDEN_BOOTS).getHandle());
          e.setItemSlot(EquipmentSlot.MAINHAND,
              new ExItemStack(Material.BOW).addExEnchantment(Enchantment.FLAME, 2)
                  .addExEnchantment(Enchantment.POWER, 5)
                  .addExEnchantment(Enchantment.PUNCH, 4).getHandle());
        })
        .applyOnEntity(e -> {
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(10);
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);
        })
        .addPathfinderGoal(1, e -> new RangedBowAttackGoal<>(e, 1.2, 2, 30.0F))
        .addPathfinderGoal(2, e -> new SpawnArmyGoal(e, Stray.class, 3, 10 * 20) {
          @Override
          public List<? extends Mob> getArmy() {
            List<Stray> skeletons = new ArrayList<>();

            float health = 20;

            if (BossSkeletonStray.this.currentWave > 16) {
              health = BossSkeletonStray.this.currentWave * 5;
            } else if (BossSkeletonStray.this.currentWave > 12) {
              health = 60;
            } else if (BossSkeletonStray.this.currentWave > 6) {
              health = 40;
            }

            if (!MobDefServer.getMobManager().compressGroups()) {
              for (int i = 0; i < 4; i++) {
                ExWorld world = MobDefServer.getMap().getWorld();

                Stray stray = new StrayBuilder()
                    .applyOnEntity(e -> e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                        .setBaseValue(2 + BossSkeletonStray.this.currentWave / 5D * MobDefServer.MOB_DAMAGE_MULTIPLIER))
                    .applyOnEntity(e -> e.getBukkitLivingEntity().setNoDamageTicks(1))
                    .applyOnEntity(e -> {
                      e.setItemSlot(EquipmentSlot.MAINHAND,
                          new ExItemStack(Material.BOW).getHandle());
                      e.invulnerableDuration = 1;
                    })
                    .setMaxHealthAndHealth(health)
                    .apply(b -> b.applyOnEntity(e -> {
                      BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.5, false,
                          MobDefServer.BREAKABLE_MATERIALS);

                      b.addPathfinderGoal(4, f -> getCorePathfinder(f, HeightMapManager.MapType.DEFAULT, 1, breakBlock,
                          MobDefServer.BREAK_LEVEL));
                      b.addPathfinderGoal(4, f -> breakBlock);
                    }))
                    .addPathfinderGoal(2, e -> new AvoidEntityGoal<>(e, Player.class, 5, 1.1, 1.1))
                    .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 0.9D))
                    .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
                    .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
                    .apply(BossSkeletonStray.this::applyDefaultTargetGoals)
                    .build(world.getHandle());

                skeletons.add(stray);
              }
            } else {
              skeletons.add(new StrayBuilder()
                  .applyOnEntity(e -> e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                      .setBaseValue(2 + BossSkeletonStray.this.currentWave / 5D * MobDefServer.MOB_DAMAGE_MULTIPLIER))
                  .applyOnEntity(e -> e.setItemSlot(EquipmentSlot.MAINHAND,
                      new ExItemStack(Material.BOW).addExEnchantment(Enchantment.POWER,
                          BossSkeletonStray.this.currentWave / 2).getHandle()))
                  .applyOnEntity(e -> {
                    e.setItemSlot(EquipmentSlot.HEAD,
                        ExItemStack.getLeatherArmor(Material.LEATHER_HELMET, Color.GREEN)
                            .addExEnchantment(Enchantment.PROTECTION,
                                BossSkeletonStray.this.currentWave / 4).getHandle());
                    e.setItemSlot(EquipmentSlot.CHEST,
                        ExItemStack.getLeatherArmor(Material.LEATHER_CHESTPLATE, Color.GREEN)
                            .addExEnchantment(Enchantment.PROTECTION,
                                BossSkeletonStray.this.currentWave / 4).getHandle());
                    e.setItemSlot(EquipmentSlot.LEGS,
                        ExItemStack.getLeatherArmor(Material.LEATHER_LEGGINGS, Color.GREEN)
                            .addExEnchantment(Enchantment.PROTECTION,
                                BossSkeletonStray.this.currentWave / 4).getHandle());
                    e.setItemSlot(EquipmentSlot.FEET,
                        ExItemStack.getLeatherArmor(Material.LEATHER_BOOTS, Color.GREEN)
                            .addExEnchantment(Enchantment.PROTECTION,
                                BossSkeletonStray.this.currentWave / 4).getHandle());
                    e.invulnerableDuration = 1;
                  })
                  .setMaxHealthAndHealth(BossSkeletonStray.this.currentWave * 20)
                  .apply(b -> b.applyOnEntity(e -> {
                    BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.8, false, MobDefServer.BREAKABLE_MATERIALS);

                    b.addPathfinderGoal(4, f -> getCorePathfinder(f, BossSkeletonStray.this.getMapType(), 1.2,
                        breakBlock,
                        MobDefServer.BREAK_LEVEL));
                    b.addPathfinderGoal(4, f -> breakBlock);
                  }))
                  .apply(b -> {
                    if (BossSkeletonStray.this.currentWave > 20) {
                      b.addPathfinderGoal(1, e -> new RangedBowAttackGoal<>(e, 1.2, 5, 30.0F));
                    } else {
                      b.addPathfinderGoal(1, e -> new RangedBowAttackGoal<>(e, 1.2, 10, 30.0F));

                    }
                  })
                  .addPathfinderGoal(2, e -> new AvoidEntityGoal<>(e, Player.class, 5, 1, 1))
                  .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 0.9D))
                  .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
                  .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
                  .apply(BossSkeletonStray.this::applyDefaultTargetGoals)
                  .build(world.getHandle()));
            }

            return skeletons;
          }
        })
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.4, false,
              MobDefServer.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 1.3, breakBlock,
              MobDefServer.BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(3, e -> new AvoidEntityGoal<>(e, Player.class, 5, 1, 1))
        .addPathfinderGoal(4, e -> new RandomStrollGoal(e, 0.9D))
        .addPathfinderGoal(5, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(5, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());

    super.spawn();
  }
}
