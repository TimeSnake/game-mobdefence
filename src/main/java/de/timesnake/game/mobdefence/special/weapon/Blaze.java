package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.basic.entities.EntityManager;
import de.timesnake.basic.entities.entity.bukkit.ExBlaze;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
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

import java.util.List;

public class Blaze extends SpecialWeapon implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.MAGMA_BLOCK, "§6 3 Blazes", "Place the block to spawn a blaze", "§c3 Blazes");

    public static final ItemTrade BLAZE = new ItemTrade(false, new ShopPrice(16, ShopCurrency.SILVER), List.of(Blaze.ITEM), Blaze.ITEM);

    public Blaze() {
        super(ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onBlockPlace(UserBlockPlaceEvent e) {
        User user = e.getUser();

        ExItemStack item = new ExItemStack(e.getItemInHand()).cloneWithId();

        if (!item.equals(ITEM)) {
            return;
        }

        int left = Integer.parseInt(item.getLore().get(1).replace("§c", "").replace(" Blazes", "")) - 1;


        if (left > 0) {
            item.setLore("Place the block to spawn a blaze", "§c" + left + " Blazes");
            user.getInventory().setItem(e.getHand(), item);
        } else {
            user.getInventory().setItem(e.getHand(), null);
        }

        Location loc = e.getBlock().getLocation();

        ExBlaze blaze = new ExBlaze(loc.getWorld(), false);

        blaze.setPosition(loc.getX(), loc.getY(), loc.getZ());

        blaze.addPathfinderGoal(1, new ExPathfinderGoalBlazeFireball());
        blaze.addPathfinderGoal(8, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        blaze.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        blaze.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            blaze.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        blaze.setPersistent(true);

        blaze.setMaxHealth(40);
        blaze.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), blaze);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        if (!(e.getEntity() instanceof org.bukkit.entity.Blaze)) {
            return;
        }

        e.setCancelled(true);
        e.setDamage(0);

    }

}
