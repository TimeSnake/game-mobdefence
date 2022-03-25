package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExZombie;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
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

        this.entity = new ExZombie(world, false);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false, BlockCheck.BREAKABLE_MATERIALS);

        double speed;
        if (this.currentWave > 13) speed = 1.3;
        else if (this.currentWave > 10) speed = 1.2;
        else if (this.currentWave > 5) speed = 1.1;
        else speed = 1;

        double random = Math.random();

        if (random < RUNNER_CHANCE) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), speed + 0.2, breakBlock, BREAK_LEVEL));
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalZombieAttack(speed + 0.2));
        } else {
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(speed));
            this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), speed, breakBlock, BREAK_LEVEL));
        }

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(speed));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman, true, true, 1D));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
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
