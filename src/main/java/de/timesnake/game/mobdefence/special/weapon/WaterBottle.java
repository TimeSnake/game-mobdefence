package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionType;

import java.util.List;

public class WaterBottle extends SpecialWeapon implements Listener {

    private static final double RADIUS = 3;

    public static final ExItemStack ITEM = new ExItemStack(Material.SPLASH_POTION, PotionType.WATER, false, false).setDisplayName("§6Water Bottle").setLore("§7Extinguish players").hideAll();

    public static final ItemTrade WATER = new ItemTrade(23, false, new ShopPrice(3, ShopCurrency.BRONZE), List.of(ITEM), new ExItemStack(Material.SPLASH_POTION, PotionType.WATER, false, false).setDisplayName("§6Water Bottle").setLore("§7Extinguish players").hideAll());

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
