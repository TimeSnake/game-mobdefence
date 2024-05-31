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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class CompressedZombie extends MobDefMob<Zombie> {

  public CompressedZombie(ExLocation spawn, int currentWave) {
    super(Type.COMPRESSED_MELEE, HeightMapManager.MapType.DEFAULT, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    this.init();
    super.spawn();
  }

  public void init() {
    ExWorld world = MobDefServer.getMap().getWorld();

    double speed;
    if (this.currentWave > 13) {
      speed = 1.3;
    } else if (this.currentWave > 10) {
      speed = 1.2;
    } else if (this.currentWave > 5) {
      speed = 1.1;
    } else {
      speed = 1;
    }

    float health = 40;

    if (this.currentWave > 11) {
      health = this.currentWave * 20;
    } else if (this.currentWave > 6) {
      health = 160;
    }


    this.entity = new ZombieBuilder()
        .setMaxHealthAndHealth(health)
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + this.currentWave / 5. * MobDefServer.MOB_DAMAGE_MULTIPLIER * 2))
        .applyOnEntity(e -> {
          e.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.NETHERITE_HELMET).getHandle());
          e.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.NETHERITE_CHESTPLATE).getHandle());
          e.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.NETHERITE_LEGGINGS).getHandle());
          e.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.NETHERITE_BOOTS).getHandle());
          e.setItemSlot(EquipmentSlot.MAINHAND,
              new ExItemStack(Material.NETHERITE_SHOVEL).addExEnchantment(Enchantment.SHARPNESS,
                  this.currentWave * 2).getHandle());
        })
        .addPathfinderGoal(1, e -> new ZombieAttackGoal(e, speed, false))
        .apply(b -> b.applyOnEntity(e -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(e, 0.8, false,
              MobDefServer.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(4, f -> getCorePathfinder(f, this.getMapType(), speed, breakBlock,
              MobDefServer.BREAK_LEVEL));
          b.addPathfinderGoal(4, f -> breakBlock);
        }))
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, speed))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build(world.getHandle());
  }
}
