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

public abstract class ArmorMob<M extends Mob> extends MobDefMob<M> {

  public ArmorMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn,
                  int currentWave) {
    super(type, mapType, wave, spawn, currentWave);
  }

  @Override
  public void spawn() {
    this.equipArmor();
    super.spawn();
  }

  public void equipArmor() {

    if (this.currentWave <= 5) {
      int random = this.random.nextInt(10);

      switch (random) {
        case 0:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.CHAINMAIL_CHESTPLATE).getHandle());
        case 1:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.CHAINMAIL_LEGGINGS).getHandle());
        case 2:
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.CHAINMAIL_BOOTS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.CHAINMAIL_HELMET).getHandle());
          break;
        case 3:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.IRON_CHESTPLATE).getHandle());
        case 4:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.IRON_LEGGINGS).getHandle());
          break;
        case 5:
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.CHAINMAIL_HELMET).getHandle());
      }
    } else if (this.currentWave <= 7) {
      int random = this.random.nextInt(10);

      switch (random) {
        case 0:
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.IRON_BOOTS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.IRON_HELMET).getHandle());
        case 1:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.IRON_LEGGINGS).getHandle());
        case 2:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.IRON_CHESTPLATE).getHandle());
          break;

        case 3:
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.IRON_BOOTS).getHandle());
        case 4:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.IRON_LEGGINGS).getHandle());
        case 5:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.DIAMOND_CHESTPLATE).getHandle());
          break;

        case 6:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.CHAINMAIL_CHESTPLATE).getHandle());
        case 7:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.CHAINMAIL_LEGGINGS).getHandle());
        case 8:
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.CHAINMAIL_BOOTS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.LEATHER_HELMET).getHandle());
          break;

        case 9:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.IRON_CHESTPLATE).getHandle());
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.LEATHER_LEGGINGS).getHandle());
      }
    } else if (this.currentWave <= 10) {
      int random = this.random.nextInt(6);

      switch (random) {
        case 0:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.IRON_CHESTPLATE).getHandle());
        case 1:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.IRON_LEGGINGS).getHandle());
        case 2:
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.IRON_BOOTS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.IRON_HELMET).getHandle());
          break;
        case 3:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.DIAMOND_CHESTPLATE).getHandle());
        case 4:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.IRON_LEGGINGS).getHandle());
        case 5:
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.DIAMOND_HELMET).getHandle());
      }
    } else if (this.currentWave <= 16) {
      int random = this.random.nextInt(6);

      switch (random) {
        case 0:
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.IRON_HELMET).getHandle());
        case 1:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.DIAMOND_CHESTPLATE).getHandle());
        case 2:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.IRON_LEGGINGS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.IRON_BOOTS).getHandle());
          break;
        case 3:
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.DIAMOND_HELMET).getHandle());
        case 4:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.DIAMOND_CHESTPLATE).getHandle());
        case 5:
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.DIAMOND_LEGGINGS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.DIAMOND_BOOTS).getHandle());
      }
    } else {
      int random = this.random.nextInt(6);

      switch (random) {
        case 0:
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.DIAMOND_HELMET).getHandle());
        case 1:
        case 2:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.DIAMOND_CHESTPLATE).getHandle());
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.DIAMOND_LEGGINGS).getHandle());
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.DIAMOND_BOOTS).getHandle());
          break;
        case 3:
          this.entity.setItemSlot(EquipmentSlot.HEAD, new ExItemStack(Material.DIAMOND_HELMET).getHandle());
          this.entity.setItemSlot(EquipmentSlot.FEET, new ExItemStack(Material.DIAMOND_BOOTS).getHandle());
        case 4:
        case 5:
          this.entity.setItemSlot(EquipmentSlot.CHEST, new ExItemStack(Material.DIAMOND_CHESTPLATE).getHandle());
          this.entity.setItemSlot(EquipmentSlot.LEGS, new ExItemStack(Material.DIAMOND_LEGGINGS).getHandle());
      }
    }

  }


}
