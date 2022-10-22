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
import de.timesnake.library.entities.entity.bukkit.ExWitch;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalArrowAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.World;

public class Witch extends MobDefMob<ExWitch> {

    public Witch(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.NORMAL, 3, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExWitch(world, true, false);

        this.entity.clearPathfinderGoals();

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.2, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1.5, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, new ExPathfinderGoalArrowAttack(1.0D, 60, 10.0F));
        this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalRandomStrollLand(1.0D));
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTargetWitch(entityClass, 10,
                    true,
                    false));

        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTargetWitch(entityClass, 10,
                    true,
                    false));

        }

        super.spawn();

        if (this.currentWave > 7) {
            this.entity.setMaxHealth(80);
            this.entity.setHealth(80);
        } else {
            this.entity.setMaxHealth(60);
            this.entity.setHealth(60);
        }


    }
}
