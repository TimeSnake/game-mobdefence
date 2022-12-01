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
import de.timesnake.library.entities.entity.bukkit.ExZombie;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalSpawnArmy;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BossZombie extends MobDefMob<ExZombie> {

    public BossZombie(ExLocation spawn, int currentWave) {
        super(Type.MELEE, HeightMapManager.MapType.NORMAL, 5, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false, false);

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(1.1, false));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.8));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

        for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }

        this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalSpawnArmy(de.timesnake.library.entities.entity.bukkit.Zombie.class, 4, 10 * 20) {
            @Override
            public List<? extends Mob> getArmee(Mob entity) {
                List<ExZombie> zombies = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    Zombie zombie = new Zombie(BossZombie.this.spawn, BossZombie.this.currentWave);

                    zombie.init();
                    zombie.equipArmor();
                    zombie.equipWeapon();
                    zombie.getEntity().setMaxNoDamageTicks(1);

                    zombies.add(zombie.getEntity());
                }

                return zombies;
            }
        });

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                new ExItemStack(Material.GOLDEN_AXE).addExEnchantment(Enchantment.FIRE_ASPECT, 2));

        this.entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.GOLDEN_HELMET));
        this.entity.setSlot(ExEnumItemSlot.CHEST, new ExItemStack(Material.GOLDEN_CHESTPLATE));
        this.entity.setSlot(ExEnumItemSlot.LEGS, new ExItemStack(Material.GOLDEN_LEGGINGS));
        this.entity.setSlot(ExEnumItemSlot.FEET, new ExItemStack(Material.GOLDEN_BOOTS));

        this.entity.getBukkitAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
        this.entity.getBukkitAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);
        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.sqrt(this.currentWave) * 6);
        this.entity.getBukkitAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.2);

        this.entity.setMaxHealth(this.currentWave * 100);
        this.entity.setHealth(this.currentWave * 100);

        super.spawn();
    }

}
