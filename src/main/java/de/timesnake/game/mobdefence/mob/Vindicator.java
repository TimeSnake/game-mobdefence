package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExVindicator;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class Vindicator extends MobDefMob<ExVindicator> {

    public Vindicator(ExLocation spawn, int currentWave) {
        super(Type.MELEE, HeightMapManager.MapType.NORMAL, 7, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.3, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity = new ExVindicator(world, false);
        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.IRON_AXE));

        this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(4, getCorePathfinder(this.getMapType(), 0.7, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(4, breakBlock);
        this.entity.addPathfinderGoal(8, new ExPathfinderGoalRandomStroll(0.6));
        this.entity.addPathfinderGoal(9, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(10, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityInsentient));

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        if (this.currentWave < 13) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalMeleeAttackVindicator(1));
        } else if (this.currentWave < 19) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalMeleeAttackVindicator(1.1));
        } else {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalMeleeAttackVindicator(1.2));
        }

        super.spawn();

        if (this.currentWave > 18) {
            this.entity.setMaxHealth(this.currentWave * 6);
            this.entity.setHealth(this.currentWave * 6);
        } else if (this.currentWave > 13) {
            this.entity.setMaxHealth(80);
            this.entity.setHealth(80);
        } else if (this.currentWave > 8) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        }
    }

}
