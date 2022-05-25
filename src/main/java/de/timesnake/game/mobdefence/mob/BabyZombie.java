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

public class BabyZombie extends ArmorMob<ExZombie> {

    public BabyZombie(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.NORMAL, 1, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false);
        entity.setBaby(true);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.3, false, BlockCheck.BREAKABLE_MATERIALS);

        double speed = this.currentWave < 15 ? 1.2 : 1.3;

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(speed));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1.2, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(1.2D));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman, true,
                true, 16D));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }

        if (this.currentWave > 13) {
            this.entity.setMaxHealth(30);
            this.entity.setHealth(30);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);


        super.spawn();
    }
}
