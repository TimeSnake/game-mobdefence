package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExCreeper;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.World;

public class Creeper extends MobDefMob<ExCreeper> {

    public Creeper(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.WALL_FINDER, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {

        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExCreeper(world, false);

        ExPathfinderGoalLocationSwell swell = new ExPathfinderGoalLocationSwell(4, 7);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, swell);

        if (this.currentWave > 10) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.4, swell, 5));
        } else if (this.currentWave > 5) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.3, swell, 5));
        } else {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.2, swell, 5));
        }

        this.entity.addPathfinderGoal(3, new ExPathfinderGoalSwell(3, 5));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalMeleeAttack(1.1D));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));
        this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman, true, true));

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
