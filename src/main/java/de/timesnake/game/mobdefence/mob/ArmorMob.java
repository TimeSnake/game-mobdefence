/*
 * game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.library.entities.entity.ExtendedCraftEntity;
import de.timesnake.library.entities.entity.extension.ExEntityInsentient;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorMob<M extends Mob & ExtendedCraftEntity<? extends ExEntityInsentient>> extends MobDefMob<M> {

    public ArmorMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn, int currentWave) {
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
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                    break;
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
            }
        } else if (this.currentWave <= 7) {
            int random = this.random.nextInt(10);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.IRON_HELMET));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                    break;

                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                    break;

                case 6:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                case 7:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
                case 8:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.LEATHER_HELMET));
                    break;

                case 9:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.LEATHER_LEGGINGS));
            }
        } else if (this.currentWave <= 10) {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.IRON_HELMET));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
            }
        } else if (this.currentWave <= 16) {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.IRON_HELMET));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
            }
        } else {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
                case 1:
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
                case 4:
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
            }
        }

    }


}
