/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Trade;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExSnowman;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalArrowAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snowman extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.CARVED_PUMPKIN,
            "§6Snowmen", "§7Place the block to spawn a snowman").immutable();

    public static final Trade.Builder SNOWMAN = new Trade.Builder()
            .price(new Price(8, Currency.GOLD))
            .giveItems(Snowman.ITEM.cloneWithId().asQuantity(4));

    public Snowman() {
        super(EntityType.SNOWMAN, ITEM);
    }

    @Override
    public void spawnEntities(Location location) {
        ExSnowman snowman = new ExSnowman(location.getWorld(), false, false);
        snowman.setPosition(location.getX(), location.getY(), location.getZ());
        snowman.setSlot(ExEnumItemSlot.HEAD, null);

        snowman.addPathfinderGoal(1, new ExPathfinderGoalArrowAttack(0D, 1, 10.0F));
        snowman.addPathfinderGoal(3, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 6.0F));
        snowman.addPathfinderGoal(4, new ExPathfinderGoalRandomStrollLand(0, 0));
        snowman.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        snowman.addPathfinderGoal(1,
                new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)));

        for (Class<? extends LivingEntity> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            snowman.addPathfinderGoal(2,
                    new ExPathfinderGoalNearestAttackableTarget(entityClass, true, false));
        }

        snowman.setPersistent(true);

        snowman.setMaxHealth(40);
        snowman.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), snowman);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            e.setCancelled(true);
            return;
        }

        if (!(snowball.getShooter() instanceof org.bukkit.entity.Snowman)) {
            return;
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
        entity.damage(1, snowball);
    }
}
