/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExEvoker;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.pathfinder.custom.*;
import org.bukkit.World;

public class Evoker extends MobDefMob<ExEvoker> {

    public Evoker(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.NORMAL, 8, spawn, currentWave);
    }

    @Override
    public void spawn() {

        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExEvoker(world, false, false);

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.3, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, new ExPathfinderGoalAvoidTarget(HumanEntity.class, 8.0F, 0.6, 12));
        this.entity.addPathfinderGoal(4, new ExCustomPathfinderGoalEvokerCastSpellVex());
        this.entity.addPathfinderGoal(5, new ExCustomPathfinderGoalEvokerCastSpellFangs());
        this.entity.addPathfinderGoal(6, new ExCustomPathfinderGoalEvokerCastSpellWololo());
        this.entity.addPathfinderGoal(8, new ExPathfinderGoalRandomStroll(0.6D));
        this.entity.addPathfinderGoal(9, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 3.0F, 1.0F));
        this.entity.addPathfinderGoal(10, new ExPathfinderGoalLookAtPlayer(Mob.class, 8.0F));

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

        for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }

        if (this.currentWave > 21) {
            this.entity.setMaxHealth(this.currentWave * 5);
        } else if (this.currentWave > 17) {
            this.entity.setMaxHealth(60);
            this.entity.setHealth(60);
        } else if (this.currentWave > 12) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        }

        super.spawn();
    }
}
