package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExIronGolem;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

public class IronGolem extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.IRON_BLOCK, "§6Iron Golem", "§7Place the block to" +
            " spawn the golem", "§7The golem tries to hold his position");

    public IronGolem() {
        super(EntityType.IRON_GOLEM, ITEM);
    }

    @Override
    public int getLeftEntities(ExItemStack item) {
        return 0;
    }

    @Override
    public void updateItem(ExItemStack item, int left) {

    }

    @Override
    public void spawnEntities(Location location) {
        ExIronGolem golem = new ExIronGolem(location.getWorld(), false);
        golem.setPosition(location.getX(), location.getY(), location.getZ());

        golem.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(1.0D));
        golem.addPathfinderGoal(2, new ExPathfinderGoalMoveTowardsTarget(0.9D, 32.0F));
        golem.addPathfinderGoal(3, new ExPathfinderGoalLocation(location.getX(), location.getY(), location.getZ(), 1,
                32, 2));
        golem.addPathfinderGoal(7, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        golem.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        golem.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            golem.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, 5, true, true));
        }

        golem.setPersistent(true);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), golem);
    }
}
