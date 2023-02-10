/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Trade;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionType;

public class WaterBottle extends SpecialWeapon implements Listener {

    public static final ExItemStack ITEM =
            ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.WATER, false, false)
                    .setDisplayName("§6Water Bottle").setLore("§7Extinguish players").hideAll();

    public static final Trade.Builder WATER = new Trade.Builder()
            .slot(23)
            .giveItems(ITEM)
            .description("Extinguish players")
            .price(new Price(3, Currency.BRONZE));

    private static final double RADIUS = 3;

    public WaterBottle() {
        super(ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof ThrownPotion)) {
            return;
        }

        if (!((ThrownPotion) e.getEntity()).getEffects().isEmpty()) {
            return;
        }

        Location loc = e.getEntity().getLocation();

        for (Player player : loc.getNearbyPlayers(RADIUS)) {
            player.setFireTicks(0);
        }
    }
}
