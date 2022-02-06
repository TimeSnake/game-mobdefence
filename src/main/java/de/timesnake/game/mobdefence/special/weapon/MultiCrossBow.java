package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class MultiCrossBow extends SpecialWeapon implements Listener {

    private static final String ARROW_NAME = "multi_crossbow";

    private static final ItemLevelType<?> MULTI_SHOT_LEVELS = new ItemLevelType<>("Multi Shot", new ExItemStack(Material.SOUL_TORCH), 0, 8, ItemLevel.getLoreNumberLevels("Multi Shot", 1, 0, "arrows", 1, List.of(new ShopPrice(8, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(4, ShopCurrency.GOLD), new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.SILVER)), "+2 Arrow", List.of(3, 5, 7, 9, 11, 13, 15, 17)));

    private static final ItemLevelType<?> PIERCING_LEVELS_LEVELS = new ItemLevelType<>("Piercing", new ExItemStack(Material.TORCH), 0, 5, ItemLevel.getLoreNumberLevels("Piercing", 2, 0, "mobs", 1, List.of(new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(11, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.GOLD)), "+1 mob", List.of(1, 2, 3, 4, 5)));

    public static final LevelItem CROSSBOW = new LevelItem("Multi Crossbow", true, new ShopPrice(16, ShopCurrency.BRONZE), new ExItemStack(Material.CROSSBOW, true, true).setDisplayName("§6Multi Crossbow").setLore("", MULTI_SHOT_LEVELS.getBaseLevelLore(0), PIERCING_LEVELS_LEVELS.getBaseLevelLore(0)), new ExItemStack(Material.CROSSBOW).enchant(), List.of(MULTI_SHOT_LEVELS, PIERCING_LEVELS_LEVELS));


    public MultiCrossBow() {
        super(CROSSBOW.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (!(e.getProjectile() instanceof Arrow)) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        User user = Server.getUser((Player) e.getEntity());
        ExItemStack item = new ExItemStack(e.getBow());

        if (!item.equals(CROSSBOW.getItem())) {
            return;
        }

        int multiShot = Integer.parseInt(MULTI_SHOT_LEVELS.getValueFromLore(item.getLore()));
        int piercing = Integer.parseInt(PIERCING_LEVELS_LEVELS.getValueFromLore(item.getLore()));

        Arrow arrow = ((Arrow) e.getProjectile());
        arrow.setCustomName(ARROW_NAME + piercing);
        arrow.setCustomNameVisible(false);
        arrow.setPersistent(true);

        Vector vector = arrow.getVelocity().normalize();

        double angle = 0.3 / (multiShot - 1);

        for (int shot = -multiShot / 2; shot < (multiShot + 1) / 2; shot++) {
            if (shot == 0) {
                continue;
            }

            Arrow multiArrow = arrow.getWorld().spawnArrow(arrow.getLocation(), vector.clone().rotateAroundY(angle * shot), (float) arrow.getVelocity().length(), 0.2f);

            multiArrow.setCustomName(ARROW_NAME + piercing);
            multiArrow.setCustomNameVisible(false);
            multiArrow.setShotFromCrossbow(true);
            multiArrow.setShooter(user.getPlayer());
            multiArrow.setPersistent(true);
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) e.getHitEntity();

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            return;
        }

        Arrow arrow = ((Arrow) e.getEntity());

        if (arrow.getCustomName() == null || !arrow.getCustomName().contains(ARROW_NAME)) {
            return;
        }

        e.setCancelled(true);

        int piercing = Integer.parseInt(arrow.getCustomName().replaceAll(ARROW_NAME, ""));

        entity.damage(arrow.getDamage(), arrow);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));

        if (piercing == 0) {
            arrow.remove();
        }

    }
}
