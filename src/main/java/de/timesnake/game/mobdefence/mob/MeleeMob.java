/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public abstract class MeleeMob<M extends Mob> extends ArmorMob<M> {

  public MeleeMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn,
                  int currentWave) {
    super(type, mapType, wave, spawn, currentWave);
  }

  @Override
  public void spawn() {
    this.equipWeapon();
    super.spawn();
  }

  public void equipWeapon() {
    if (this.currentWave <= 5) {
      int random = this.random.nextInt(8);

      switch (random) {
        case 0 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.STONE_SWORD).getHandle());
        case 1 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_SWORD).getHandle());
        case 2 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.GOLDEN_SWORD).getHandle());
      }
    } else if (this.currentWave <= 7) {
      int random = this.random.nextInt(6);

      switch (random) {
        case 0 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.STONE_SWORD).getHandle());
        case 1 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_SWORD).getHandle());
        case 2 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.DIAMOND_SWORD).getHandle());
      }

    } else if (this.currentWave <= 10) {
      int random = this.random.nextInt(5);

      switch (random) {
        case 0 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.STONE_SWORD).getHandle());
        case 1 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_SWORD).getHandle());
        case 2 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.DIAMOND_SWORD).getHandle());
        case 3 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_AXE).getHandle());
      }
    } else if (this.currentWave <= 14) {
      int random = this.random.nextInt(5);

      switch (random) {
        case 0 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.STONE_SWORD).getHandle());
        case 1 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_SWORD).getHandle());
        case 2 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.DIAMOND_SWORD).getHandle());
        case 3 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_AXE).getHandle());
        case 4 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.DIAMOND_AXE).getHandle());
      }
    } else {
      int random = this.random.nextInt(5);

      switch (random) {
        case 0 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND,
            new ExItemStack(Material.STONE_SWORD).addExEnchantment(Enchantment.DAMAGE_ALL, 2).getHandle());
        case 1 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND,
            new ExItemStack(Material.IRON_SWORD).addExEnchantment(Enchantment.DAMAGE_ALL, 2).getHandle());
        case 2 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND,
            new ExItemStack(Material.DIAMOND_SWORD).addExEnchantment(Enchantment.DAMAGE_ALL, 2).getHandle());
        case 3 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND,
            new ExItemStack(Material.IRON_AXE).addExEnchantment(Enchantment.DAMAGE_ALL, 1).getHandle());
        case 4 -> this.entity.setItemSlot(EquipmentSlot.MAINHAND,
            new ExItemStack(Material.DIAMOND_AXE).addExEnchantment(Enchantment.DAMAGE_ALL, 1).getHandle());
      }
    }
  }
}
