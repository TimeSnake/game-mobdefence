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

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.library.entities.entity.ExtendedCraftEntity;
import de.timesnake.library.entities.entity.extension.ExEntityInsentient;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

public abstract class MeleeMob<M extends Mob & ExtendedCraftEntity<? extends ExEntityInsentient>> extends ArmorMob<M> {

    public MeleeMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn, int currentWave) {
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
                case 0 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.STONE_SWORD));
                case 1 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.IRON_SWORD));
                case 2 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.GOLDEN_SWORD));
            }
        } else if (this.currentWave <= 7) {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.STONE_SWORD));
                case 1 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.IRON_SWORD));
                case 2 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.DIAMOND_SWORD));
            }

        } else if (this.currentWave <= 10) {
            int random = this.random.nextInt(5);

            switch (random) {
                case 0 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.STONE_SWORD));
                case 1 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.IRON_SWORD));
                case 2 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.DIAMOND_SWORD));
                case 3 ->
                        this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.IRON_AXE));
            }
        } else if (this.currentWave <= 14) {
            int random = this.random.nextInt(5);

            switch (random) {
                case 0 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.STONE_SWORD));
                case 1 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.IRON_SWORD));
                case 2 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.DIAMOND_SWORD));
                case 3 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.IRON_AXE));
                case 4 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ItemStack(Material.DIAMOND_AXE));
            }
        } else {
            int random = this.random.nextInt(5);

            switch (random) {
                case 0 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ExItemStack(Material.STONE_SWORD).addExEnchantment(Enchantment.DAMAGE_ALL, 2));
                case 1 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ExItemStack(Material.IRON_SWORD).addExEnchantment(Enchantment.DAMAGE_ALL, 2));
                case 2 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ExItemStack(Material.DIAMOND_SWORD).addExEnchantment(Enchantment.DAMAGE_ALL, 2));
                case 3 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ExItemStack(Material.IRON_AXE).addExEnchantment(Enchantment.DAMAGE_ALL, 1));
                case 4 -> this.entity.getExtension().setSlot(ExEnumItemSlot.MAIN_HAND,
                        new ExItemStack(Material.DIAMOND_AXE).addExEnchantment(Enchantment.DAMAGE_ALL, 1));
            }
        }
    }
}
