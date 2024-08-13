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
import de.timesnake.library.entities.entity.ZombieBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import de.timesnake.library.entities.pathfinder.SpawnArmyGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
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
          e.getBukkitCreature().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
          e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
          e.getBukkitCreature().getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);
          e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.sqrt(this.currentWave) * 6);
          e.getBukkitCreature().getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.2);
        })
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.8, false,
              PathCostCalc.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 1, breakBlock, BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(2, e -> new SpawnArmyGoal(e, Zombie.class, 3, 10 * 20) {
          @Override
          public List<? extends Mob> getArmy() {
            List<Zombie> zombies = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
              MobDefZombie zombie = new MobDefZombie(BossZombie.this.spawn, BossZombie.this.currentWave);

              zombie.init();
              zombie.equipArmor();
              zombie.equipWeapon();
              zombie.getEntity().getBukkitCreature().setNoDamageTicks(1);

              zombies.add(zombie.getEntity());
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
