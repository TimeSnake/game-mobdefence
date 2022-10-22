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
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExZombie;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalZombieAttack;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;

public class Zombie extends MeleeMob<ExZombie> {

    private static final double RUNNER_CHANCE = 0.3;

    public Zombie(ExLocation spawn, int currentWave) {
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

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false, BlockCheck.BREAKABLE_MATERIALS);

        double speed;
        if (this.currentWave > 13) speed = 1.3;
        else if (this.currentWave > 10) speed = 1.2;
        else if (this.currentWave > 5) speed = 1.1;
        else speed = 1;

        double random = Math.random();

        if (random < RUNNER_CHANCE) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), speed + 0.2, breakBlock,
                    BREAK_LEVEL));
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalZombieAttack(speed + 0.2, false));
        } else {
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(speed, false));
            this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), speed, breakBlock, BREAK_LEVEL));
        }

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalRandomStrollLand(speed));
        this.entity.addPathfinderGoal(5, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman,
                true,
                true, 1D));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }

        if (this.currentWave > 11) {
            this.entity.setMaxHealth(this.currentWave * 5);
            this.entity.setHealth(this.currentWave * 5);
        } else if (this.currentWave > 6) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

    }
}
