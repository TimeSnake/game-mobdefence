package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExCreeper;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMeleeAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import org.bukkit.World;

public class Creeper extends MobDefMob<ExCreeper> {

    public Creeper(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.WALL_FINDER, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {

        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExCreeper(world, false);

        ExCustomPathfinderGoalLocationSwell swell = new ExCustomPathfinderGoalLocationSwell(4, 7);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, swell);

        if (this.currentWave > 10) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.4, swell, 5));
        } else if (this.currentWave > 5) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.3, swell, 5));
        } else {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.2, swell, 5));
        }

        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalSwell(3, 5));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalMeleeAttack(1.1D));
        this.entity.addPathfinderGoal(6, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));
        this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman,
                true,
                true));

        if (this.currentWave > 10) {
            this.entity.setMaxHealth(60);
            this.entity.setHealth(60);
        } else if (this.currentWave > 5) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        } else {
            this.entity.setMaxHealth(30);
            this.entity.setHealth(30);
        }

        super.spawn();


    }
}
