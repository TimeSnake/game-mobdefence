/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExIronGolem;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.LivingEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMeleeAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMoveTowardsTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalLocation;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

public class IronGolem extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.IRON_BLOCK, "§6Iron Golem",
            "§7Place a block to spawn a golem", "§7The golem tries to hold his position")
            .immutable();

    public IronGolem() {
        super(EntityType.IRON_GOLEM, ITEM);
    }

    @Override
    public void spawnEntities(Location location) {
        ExIronGolem golem = new ExIronGolem(location.getWorld(), false, false);
        golem.setPosition(location.getX(), location.getY(), location.getZ());

        golem.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(1.0D));
        golem.addPathfinderGoal(2, new ExPathfinderGoalMoveTowardsTarget(0.9D, 32.0F));
        golem.addPathfinderGoal(3,
                new ExCustomPathfinderGoalLocation(location.getX(), location.getY(),
                        location.getZ(), 1, 32, 2));
        golem.addPathfinderGoal(7, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 6.0F));
        golem.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        golem.addPathfinderGoal(1,
                new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)));

        for (Class<? extends LivingEntity> entityClass : MobDefMob.ATTACKER_ENTITY_CLASSES) {
            golem.addPathfinderGoal(2,
                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, 5, true, true));
        }

        golem.setPersistent(true);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), golem);
    }
}
