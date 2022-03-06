package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExBlaze;
import de.timesnake.basic.entities.entity.bukkit.ExSkeleton;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;

public class FollowerSkeleton extends ArmorMob<ExSkeleton> {

    public FollowerSkeleton(ExLocation spawn, int currentWave) {
        super(Type.RANGED, HeightMapManager.MapType.NORMAL, 1, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExSkeleton(world, false);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.5, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(15.0F));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.9D));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW));

        if (this.currentWave > 10) {
            this.subEntities.add(this.getBlaze());
        }
        this.subEntities.add(this.getBlaze());

        if (this.currentWave > 16) {
            this.entity.setMaxHealth(this.currentWave * 5);
        } else if (this.currentWave > 12) {
            this.entity.setMaxHealth(60);
            this.entity.setHealth(60);
        } else if (this.currentWave > 6) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

        super.spawn();
    }

    private ExBlaze getBlaze() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        ExBlaze blaze = new ExBlaze(world, false);

        blaze.addPathfinderGoal(1, new ExPathfinderGoalBlazeFireball());
        blaze.addPathfinderGoal(2, new ExPathfinderGoalFollowEntity(EntityClass.EntitySkeleton, 1.2f, 10, 30));
        blaze.addPathfinderGoal(3, new ExPathfinderGoalFollowEntity(EntityClass.EntitySkeleton, 1.2f, 10, 30));
        blaze.addPathfinderGoal(3, new ExPathfinderGoalFollowEntity(EntityClass.EntityBlaze, 1.2f, 10, 30));
        blaze.addPathfinderGoal(7, new ExPathfinderGoalRandomStrollLand(1.0D));
        blaze.addPathfinderGoal(8, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        blaze.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        blaze.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            blaze.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }
        blaze.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            blaze.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        blaze.setMaxNoDamageTicks(1);

        return blaze;
    }
}
