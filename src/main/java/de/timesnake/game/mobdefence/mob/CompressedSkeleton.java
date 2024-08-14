/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.SkeletonBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class CompressedSkeleton extends MobDefMob<Skeleton> {

  public CompressedSkeleton(ExLocation spawn, int currentWave) {
    super(Type.COMBRESSED_RANGED, HeightMapManager.MapType.DEFAULT, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    this.init();
    super.spawn();
  }

  public void init() {
    ExWorld world = MobDefServer.getMap().getWorld();

    this.entity = new SkeletonBuilder()
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + this.currentWave / 5D * MobDefServer.MOB_DAMAGE_MULTIPLIER))
        .applyOnEntity(e -> e.setItemSlot(EquipmentSlot.MAINHAND,
            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE, this.currentWave / 2).getHandle()))
        .applyOnEntity(e -> {
          e.setItemSlot(EquipmentSlot.HEAD,
              ExItemStack.getLeatherArmor(Material.LEATHER_HELMET, Color.GREEN)
                  .addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4).getHandle());
          e.setItemSlot(EquipmentSlot.CHEST,
              ExItemStack.getLeatherArmor(Material.LEATHER_CHESTPLATE, Color.GREEN)
                  .addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4).getHandle());
          e.setItemSlot(EquipmentSlot.LEGS,
              ExItemStack.getLeatherArmor(Material.LEATHER_LEGGINGS, Color.GREEN)
                  .addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4).getHandle());
          e.setItemSlot(EquipmentSlot.FEET,
              ExItemStack.getLeatherArmor(Material.LEATHER_BOOTS, Color.GREEN)
                  .addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4).getHandle());
        })
        .setMaxHealthAndHealth(this.currentWave * 20)
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.8, false,
              MobDefServer.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), 1.2, breakBlock,
              MobDefServer.BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .apply(b -> {
          if (this.currentWave > 20) {
            b.addPathfinderGoal(1, e -> new RangedBowAttackGoal<>(e, 1.2, 5, 30.0F));
          } else {
            b.addPathfinderGoal(1, e -> new RangedBowAttackGoal<>(e, 1.2, 10, 30.0F));

          }
        })
        .addPathfinderGoal(2, e -> new AvoidEntityGoal<>(e, Player.class, 5, 1, 1))
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 0.9D))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());
  }
}
