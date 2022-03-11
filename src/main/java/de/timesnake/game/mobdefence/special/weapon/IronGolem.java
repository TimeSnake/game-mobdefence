package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.basic.entities.EntityManager;
import de.timesnake.basic.entities.entity.bukkit.ExEntityIronGolem;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class IronGolem extends SpecialWeapon implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.IRON_BLOCK, "§6Iron Golem", "Place the block to spawn the golem", "The golem tries to hold his position");

    public IronGolem() {
        super(ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onBlockPlace(UserBlockPlaceEvent e) {
        User user = e.getUser();

        if (!new ExItemStack(e.getItemInHand()).equals(ITEM)) {
            return;
        }

        Location loc = e.getBlock().getLocation();

        user.removeCertainItemStack(ITEM);

        ExEntityIronGolem golem = new ExEntityIronGolem(loc.getWorld(), false);
        golem.setPosition(loc.getX(), loc.getY(), loc.getZ());

        golem.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(1.0D));
        golem.addPathfinderGoal(2, new ExPathfinderGoalMoveTowardsTarget(0.9D, 32.0F));
        golem.addPathfinderGoal(3, new ExPathfinderGoalLocation(loc.getX(), loc.getY(), loc.getZ(), 1, 32, 2));
        golem.addPathfinderGoal(7, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        golem.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        golem.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            golem.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, 5, true, true));
        }

        golem.setPersistent(true);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), golem);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        if (!(e.getEntity() instanceof org.bukkit.entity.IronGolem)) {
            return;
        }

        e.setCancelled(true);
        e.setDamage(0);

    }
}
