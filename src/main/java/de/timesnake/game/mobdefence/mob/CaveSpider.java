package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExCaveSpider;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;

public class CaveSpider extends MobDefMob<ExCaveSpider> {

    public CaveSpider(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.NORMAL, 8, spawn, currentWave);
    }

    @Override
    public void spawn() {

        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExCaveSpider(world, false);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, null, BREAK_LEVEL));
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalLeapAtTarget(0.4F));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalSpiderMeleeAttack(1));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalRandomStrollLand(0.8D));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        if (this.currentWave > 13) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        } else {
            this.entity.setMaxHealth(20);
            this.entity.setHealth(20);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

        super.spawn();
    }
}
