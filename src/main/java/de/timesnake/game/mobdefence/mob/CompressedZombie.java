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
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExZombie;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalZombieAttack;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class CompressedZombie extends MobDefMob<ExZombie> {

    public CompressedZombie(ExLocation spawn, int currentWave) {
        super(Type.COMPRESSED_MELEE, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {
        this.init();
        super.spawn();
    }

    public void init() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false, false);

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        double speed;
        if (this.currentWave > 13) speed = 1.3;
        else if (this.currentWave > 10) speed = 1.2;
        else if (this.currentWave > 5) speed = 1.1;
        else speed = 1;

        double random = Math.random();

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(speed, false));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), speed, breakBlock, BREAK_LEVEL));

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalRandomStrollLand(speed));
        this.entity.addPathfinderGoal(5, new ExCustomPathfinderGoalLookAtPlayer(HumanEntity.class));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class,
                true,
                true, 1D));

        for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }

        if (this.currentWave > 11) {
            this.entity.setMaxHealth(this.currentWave * 20);
            this.entity.setHealth(this.currentWave * 20);
        } else if (this.currentWave > 6) {
            this.entity.setMaxHealth(160);
            this.entity.setHealth(160);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5. * MobManager.MOB_DAMAGE_MULTIPLIER * 2);

        this.entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.NETHERITE_HELMET));
        this.entity.setSlot(ExEnumItemSlot.CHEST, new ExItemStack(Material.NETHERITE_CHESTPLATE));
        this.entity.setSlot(ExEnumItemSlot.LEGS, new ExItemStack(Material.NETHERITE_LEGGINGS));
        this.entity.setSlot(ExEnumItemSlot.FEET, new ExItemStack(Material.NETHERITE_BOOTS));

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                new ExItemStack(Material.NETHERITE_SHOVEL).addExEnchantment(Enchantment.DAMAGE_ALL, this.currentWave * 2));

    }
}
