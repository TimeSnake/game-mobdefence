package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

public class SplashBow extends SpecialWeapon implements Listener {

    private static final String ARROW_NAME = "splashArrow";
    private static final String RADIUS_NAME = "radius";

    public static final int RADIUS = 2;
    public static final double DAMAGE = 2;

    public static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 7, ItemLevel.getLoreNumberLevels("Damage", 1, 1, "❤", 2, List.of(new ShopPrice(8, ShopCurrency.BRONZE), new ShopPrice(8, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(25, ShopCurrency.SILVER), new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(14, ShopCurrency.GOLD)), "+0.5 ❤", List.of(2.5, 3, 3.5, 4, 4.5, 5)));

    public static final ItemLevelType<?> RADIUS_LEVELS = new ItemLevelType<>("Radius", new ExItemStack(Material.TARGET), 1, 3, ItemLevel.getLoreNumberLevels("Radius", 2, 0, "blocks", 2, List.of(new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD)), "+1 block", List.of(3, 4)));

    public static final LevelItem BOW = new LevelItem("Splash Bow", false, new ShopPrice(6, ShopCurrency.GOLD), new ExItemStack(Material.BOW, Material.BOW.getMaxDurability() - 24, true).setDisplayName("§6Splash Bow").setLore("", DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), RADIUS_LEVELS.getBaseLevelLore(RADIUS)), new ExItemStack(Material.BOW, Material.BOW.getMaxDurability() - 24, true), List.of(DAMAGE_LEVELS, RADIUS_LEVELS));

    public SplashBow() {
        super(BOW.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Arrow arrow = ((Arrow) e.getEntity());
        Player player = ((Player) arrow.getShooter());

        arrow.remove();

        if (arrow.getCustomName() == null) {
            return;
        }

        if (!arrow.getCustomName().contains(ARROW_NAME)) {
            return;
        }

        String[] nameParts = arrow.getCustomName().split(RADIUS_NAME);

        double damage = Double.parseDouble(nameParts[0].replaceAll(ARROW_NAME, ""));
        int radius = Integer.parseInt(nameParts[1]);

        arrow.getWorld().createExplosion(arrow, radius, false, false);
    }


    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (!(e.getProjectile() instanceof Arrow)) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        ExItemStack item = new ExItemStack(e.getBow());

        if (!item.equals(BOW.getItem())) {
            return;
        }

        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(item.getLore()));
        int radius = Integer.parseInt(RADIUS_LEVELS.getValueFromLore(item.getLore()));

        Arrow arrow = ((Arrow) e.getProjectile());
        arrow.setCustomName(ARROW_NAME + damage + RADIUS_NAME + radius);
        arrow.setCustomNameVisible(false);
    }
}
