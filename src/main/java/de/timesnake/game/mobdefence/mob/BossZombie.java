/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.ZombieBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import de.timesnake.library.entities.pathfinder.SpawnArmyGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BossZombie extends MobDefMob<Zombie> {

  public BossZombie(ExLocation spawn, int currentWave) {
    super(Type.MELEE, HeightMapManager.MapType.DEFAULT, 5, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    this.entity = new ZombieBuilder()
        .setMaxHealthAndHealth(this.currentWave * 100)
        .applyOnEntity(e -> {
          e.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.GOLDEN_AXE).addExEnchantment(Enchantment.FIRE_ASPECT, 2).getHandle());
          e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.GOLDEN_HELMET).getHandle());
          e.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.GOLDEN_CHESTPLATE).getHandle());
          e.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.GOLDEN_LEGGINGS).getHandle());
          e.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.GOLDEN_BOOTS).getHandle());
        })
        .applyOnEntity(e -> {
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.sqrt(this.currentWave) * 6);
          e.getBukkitLivingEntity().getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.2);
        })
        .applyOnEntity(e -> e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobDefServer.MOB_DAMAGE_MULTIPLIER))
        .addPathfinderGoal(1, e -> new ZombieAttackGoal(e, 1, false))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.8, false,
              MobDefServer.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 1, breakBlock, MobDefServer.BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(2, e -> new SpawnArmyGoal(e, Zombie.class, 3, 10 * 20) {
          @Override
          public List<? extends Mob> getArmy() {
            List<Zombie> zombies = new ArrayList<>();

            if (!MobDefServer.getMobManager().compressGroups()) {
              for (int i = 0; i < 4; i++) {
                MobDefZombie zombie = new MobDefZombie(BossZombie.this.spawn, BossZombie.this.currentWave);

                zombie.init();
                zombie.equipArmor();
                zombie.equipWeapon();
                zombie.getEntity().getBukkitLivingEntity().setNoDamageTicks(1);

                zombies.add(zombie.getEntity());
              }
            } else {
              float health = 40;

              if (BossZombie.this.currentWave > 11) {
                health = BossZombie.this.currentWave * 20;
              } else if (BossZombie.this.currentWave > 6) {
                health = 160;
              }

              double speed;
              if (BossZombie.this.currentWave > 13) {
                speed = 1.3;
              } else if (BossZombie.this.currentWave > 10) {
                speed = 1.2;
              } else if (BossZombie.this.currentWave > 5) {
                speed = 1.1;
              } else {
                speed = 1;
              }

              zombies.add(new ZombieBuilder()
                  .setMaxHealthAndHealth(health)
                  .applyOnEntity(e -> e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                      .setBaseValue(2 + BossZombie.this.currentWave / 5. * MobDefServer.MOB_DAMAGE_MULTIPLIER * 2))
                  .applyOnEntity(e -> {
                    e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.NETHERITE_HELMET).getHandle());
                    e.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.NETHERITE_CHESTPLATE).getHandle());
                    e.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.NETHERITE_LEGGINGS).getHandle());
                    e.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.NETHERITE_BOOTS).getHandle());
                    e.setItemSlot(EquipmentSlot.MAINHAND,
                        new ExItemStack(Material.NETHERITE_SHOVEL).addExEnchantment(Enchantment.SHARPNESS,
                            BossZombie.this.currentWave * 2).getHandle());
                    e.invulnerableDuration = 1;
                  })
                  .addPathfinderGoal(1, e -> new ZombieAttackGoal(e, speed, false))
                  .apply(b -> b.applyOnEntity(e -> {
                    BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.8, false,
                        MobDefServer.BREAKABLE_MATERIALS);

                    b.addPathfinderGoal(4, f -> getCorePathfinder(f, BossZombie.this.getMapType(), speed, breakBlock,
                        MobDefServer.BREAK_LEVEL));
                    b.addPathfinderGoal(4, f -> breakBlock);
                  }))
                  .addPathfinderGoal(3, e -> new RandomStrollGoal(e, speed))
                  .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
                  .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
                  .apply(BossZombie.this::applyDefaultTargetGoals)
                  .build(world.getHandle()));
            }
            return zombies;
          }
        })
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 0.8))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());

    super.spawn();
  }

}
