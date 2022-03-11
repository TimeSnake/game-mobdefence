package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.basic.entities.EntityManager;
import de.timesnake.basic.entities.entity.bukkit.ExSnowman;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Snowman extends SpecialWeapon implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.CARVED_PUMPKIN, "§6 4 Snowmen", "Place the block to spawn a snowman", "§c4 Snowmen");

    public static final ItemTrade SNOWMAN = new ItemTrade(false, new ShopPrice(8, ShopCurrency.GOLD), List.of(Snowman.ITEM), Snowman.ITEM);

    public Snowman() {
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

        int left = Integer.parseInt(item.getLore().get(1).replace("§c", "").replace(" Snowmen", "")) - 1;


        if (left > 0) {
            item.setLore("Place the block to spawn a snowman", "§c" + left + " Snowmen");
            user.getInventory().setItem(e.getHand(), item);
        } else {
            user.getInventory().setItem(e.getHand(), null);
        }

        Location loc = e.getBlock().getLocation();

        ExSnowman snowman = new ExSnowman(loc.getWorld(), false);
        snowman.setPosition(loc.getX(), loc.getY(), loc.getZ());
        snowman.setSlot(ExEnumItemSlot.HEAD, null);

        snowman.addPathfinderGoal(1, new ExPathfinderGoalArrowAttack(1.25D, 1, 10.0F));
        snowman.addPathfinderGoal(3, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        snowman.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        snowman.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            snowman.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, false));
        }


        snowman.setPersistent(true);

        snowman.setMaxHealth(40);
        snowman.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), snowman);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        if (!(e.getEntity() instanceof org.bukkit.entity.Snowman)) {
            return;
        }

        e.setCancelled(true);
        e.setDamage(0);

    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball)) {
            return;
        }

        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) e.getHitEntity();

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            e.setCancelled(true);
            return;
        }

        Snowball snowball = ((Snowball) e.getEntity());

        if (!(snowball.getShooter() instanceof org.bukkit.entity.Snowman)) {
            return;
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
        entity.damage(1, snowball);
    }
}
