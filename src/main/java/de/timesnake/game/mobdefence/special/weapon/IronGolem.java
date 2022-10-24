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

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExIronGolem;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.LivingEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMeleeAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMoveTowardsTarget;
import de.timesnake.library.entities.pathfinder.custom.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

public class IronGolem extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.IRON_BLOCK, "§6Iron Golem", "§7Place the block to" +
            " spawn the golem", "§7The golem tries to hold his position");

    public IronGolem() {
        super(EntityType.IRON_GOLEM, ITEM, 3);
    }

    @Override
    public int getAmountFromString(String s) {
        return Integer.parseInt(s.replace("§c", "").replace(" Iron-Golems", ""));
    }

    @Override
    public String parseAmountToString(int amount) {
        return "§c" + amount + " Iron-Golems";
    }

    @Override
    public void spawnEntities(Location location) {
        ExIronGolem golem = new ExIronGolem(location.getWorld(), false, false);
        golem.setPosition(location.getX(), location.getY(), location.getZ());

        golem.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(1.0D));
        golem.addPathfinderGoal(2, new ExPathfinderGoalMoveTowardsTarget(0.9D, 32.0F));
        golem.addPathfinderGoal(3, new ExCustomPathfinderGoalLocation(location.getX(), location.getY(),
                location.getZ(), 1,
                32, 2));
        golem.addPathfinderGoal(7, new ExCustomPathfinderGoalLookAtPlayer(HumanEntity.class));
        golem.addPathfinderGoal(8, new ExCustomPathfinderGoalRandomLookaround());

        golem.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (Class<? extends LivingEntity> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            golem.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, 5, true, true));
        }

        golem.setPersistent(true);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), golem);
    }
}
