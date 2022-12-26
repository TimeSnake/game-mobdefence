/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Trade;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PoisonArrow extends SpecialWeapon implements UserInventoryInteractListener, Listener {

    public static final ExItemStack ITEM =
            ExItemStack.getPotion(Material.TIPPED_ARROW, 8, PotionType.POISON, false, false).setDisplayName("§6Poison Arrow");
    public static final Trade.Builder TRADE = new Trade.Builder()
            .giveItems(ITEM)
            .price(new Price(5, Currency.BRONZE));

    private static final String NAME = "poison_arrow";

    public PoisonArrow() {
        super(ITEM);
        Server.getInventoryEventManager().addInteractListener(this, ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        user.removeCertainItemStack(ITEM.cloneWithId().asOne());

        Arrow arrow = user.getPlayer().getWorld().spawnArrow(user.getPlayer().getEyeLocation().add(0, -0.2, 0),
                user.getPlayer().getLocation().getDirection(), 2, 1);

        arrow.setCustomName(NAME);
        arrow.setCustomNameVisible(false);
        arrow.setShooter(user.getPlayer());
        arrow.setDamage(3);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) e.getEntity();

        if (arrow.getCustomName() != null && arrow.getCustomName().equals(NAME)) {
            arrow.getWorld().playEffect(arrow.getLocation(), Effect.POTION_BREAK, new Potion(PotionType.POISON));
            for (Entity entity : arrow.getNearbyEntities(3, 3, 3)) {
                if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 1));
                }
            }
        }
    }
}
