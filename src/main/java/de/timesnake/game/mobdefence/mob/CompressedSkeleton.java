/*
 * workspace.game-mobdefence.main
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
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExSkeleton;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import de.timesnake.library.entities.wrapper.ExMobEffects;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class CompressedSkeleton extends MobDefMob<ExSkeleton> {

    public CompressedSkeleton(ExLocation spawn, int currentWave) {
        super(Type.COMBRESSED_RANGED, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {
        this.init();
        super.spawn();
    }

    public void init() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        if (this.currentWave > 20) {
            if (this.random.nextBoolean()) {
                this.entity = new ExSkeleton(world, false, ExMobEffects.INSTANT_DAMAGE, 0, 2);
            } else {
                this.entity = new ExSkeleton(world, false, ExMobEffects.POISON, (int) (this.currentWave * 0.5 * 20), 3);
            }
        } else {
            this.entity = new ExSkeleton(world, false, ExMobEffects.POISON, (int) (this.currentWave * 0.5 * 20), 3);
        }

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1.2, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.9D));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

        for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }


        entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                this.currentWave / 2));

        if (this.currentWave > 20) {
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 5, 30.0F));
        } else {
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));

        }

        this.entity.setMaxHealth(this.currentWave * 20);
        this.entity.setHealth(this.currentWave * 20);

        this.entity.setSlot(ExEnumItemSlot.HEAD,
                ExItemStack.getLeatherArmor(Material.LEATHER_HELMET, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4));
        this.entity.setSlot(ExEnumItemSlot.CHEST,
                ExItemStack.getLeatherArmor(Material.LEATHER_CHESTPLATE, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4));
        this.entity.setSlot(ExEnumItemSlot.LEGS,
                ExItemStack.getLeatherArmor(Material.LEATHER_LEGGINGS, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4));
        this.entity.setSlot(ExEnumItemSlot.FEET,
                ExItemStack.getLeatherArmor(Material.LEATHER_BOOTS, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, this.currentWave / 4));

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER);
    }
}
