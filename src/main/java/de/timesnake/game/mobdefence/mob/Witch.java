/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExWitch;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
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
        this.entity.addPathfinderGoal(2, new ExPathfinderGoalRandomStrollLand(1.0D));
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTargetWitch(entityClass, 10,
                    true, false, null));

        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

        for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTargetWitch(entityClass, 10,
                    true, false));

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
