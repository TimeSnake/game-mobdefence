package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.weapon.bullet.BulletManager;
import de.timesnake.game.mobdefence.special.weapon.bullet.PiercingBullet;
import de.timesnake.game.mobdefence.special.weapon.bullet.TargetFinder;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ShulkerBullet;

import java.util.List;

public class Wand extends CooldownWeapon {

    private static final double FIRE_RATE = 2; // per second
    private static final double DAMAGE = 1.5;
    private static final double SPEED = 2;

    private static final ItemLevelType<?> SPEED_LEVELS = new ItemLevelType<>("Speed",
            new ExItemStack(Material.FEATHER), 1, 4, ItemLevel.getLoreNumberLevels("Speed", 1, 1, "", 2,
            List.of(new ShopPrice(4, ShopCurrency.BRONZE), new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(4,
                    ShopCurrency.GOLD)), "+0.2 Speed", List.of(2.4, 2.8, 3.2)));

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage",
            new ExItemStack(Material.RED_DYE), 1, 8, ItemLevel.getLoreNumberLevels("Damage", 2, 1, "❤", 2,
            List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER),
                    new ShopPrice(5, ShopCurrency.GOLD), new ShopPrice(10, ShopCurrency.GOLD),
                    new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD),
                    new ShopPrice(32, ShopCurrency.SILVER)), "+0.5 ❤",
            List.of(2, 2.5, 3, 3.5, 4, 4.5, 5)));

    private static final ItemLevelType<?> FIRE_RATE_LEVELS = new ItemLevelType<>("Fire Rate",
            new ExItemStack(Material.FIRE_CHARGE), 1, 5, ItemLevel.getLoreNumberLevels("Fire Rate", 3, 0, "per sec.",
            2, List.of(new ShopPrice(6, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(6,
                    ShopCurrency.GOLD), new ShopPrice(64, ShopCurrency.BRONZE)), "+1 per second", List.of(3, 4, 5, 6)));

    private static final ItemLevelType<?> PIERCING_LEVELS = new ItemLevelType<>("Piercing",
            new ExItemStack(Material.TORCH), 0, 6, ItemLevel.getLoreNumberLevels("Piercing", 4, 0, "mobs", 1,
            List.of(new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(11, ShopCurrency.GOLD), new ShopPrice(32,
                    ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(16,
                    ShopCurrency.GOLD), new ShopPrice(64, ShopCurrency.BRONZE)), "+1 mob", List.of(1, 2, 3, 4, 5, 6)));

    private static final ItemLevelType<?> MULTISHOT_LEVELS = new ItemLevelType<>("Multishot",
            new ExItemStack(Material.SOUL_TORCH), 0, 2, ItemLevel.getLoreNumberLevels("Multishot", 4, 0, "bullets", 1
            , List.of(new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.GOLD)), "+1 bullet",
            List.of(2, 3)));
    public static final LevelItem WAND = new LevelItem("Wand",
            new ExItemStack(Material.STICK, "§3Wand").enchant().setLore("", SPEED_LEVELS.getBaseLevelLore(SPEED),
                    DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), FIRE_RATE_LEVELS.getBaseLevelLore(FIRE_RATE)),
            new ExItemStack(Material.STICK).enchant(), List.of(SPEED_LEVELS, DAMAGE_LEVELS, FIRE_RATE_LEVELS,
            PIERCING_LEVELS, MULTISHOT_LEVELS));

    static {
        PIERCING_LEVELS.setConflictingTypes(List.of(MULTISHOT_LEVELS));
        MULTISHOT_LEVELS.setConflictingTypes(List.of(PIERCING_LEVELS));
    }

    public Wand() {
        super(WAND.getItem());
    }


    @Override
    public void onInteract(ExItemStack wand, MobDefUser user) {
        List<String> lore = wand.getLore();

        double speed = Double.parseDouble(SPEED_LEVELS.getValueFromLore(lore));
        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(lore));

        int piercing = 0;
        String piercingString = PIERCING_LEVELS.getValueFromLore(lore);
        if (piercingString != null) {
            try {
                piercing = Integer.parseInt(piercingString);
            } catch (NumberFormatException ignored) {
            }
        }

        int multiShot = 1;
        String multiShotString = MULTISHOT_LEVELS.getValueFromLore(lore);
        if (multiShotString != null) {
            try {
                multiShot = Integer.parseInt(multiShotString);
            } catch (NumberFormatException ignored) {
            }
        }

        BulletManager bulletManager = MobDefServer.getWeaponManager().getBulletManager();

        for (int shots = 0; shots < multiShot; shots++) {
            int finalPiercing = piercing;
            Server.runTaskLaterSynchrony(() -> bulletManager.shootBullet(new WandBullet(user, speed, damage,
                            finalPiercing), user.getLocation().getDirection().multiply(speed)), shots * 2,
                    GameMobDefence.getPlugin());

        }
    }

    @Override
    public int getCooldown(ExItemStack item) {
        return (int) (1 / Double.parseDouble(FIRE_RATE_LEVELS.getValueFromLore(item.getLore())) * 20);
    }

    private static class WandBullet extends PiercingBullet {

        public WandBullet(User user, double speed, double damage, int piercing) {
            super(user, user.getEyeLocation().add(0, -0.5, 0), TargetFinder.STRAIGHT, speed, damage, piercing);
        }

        @Override
        public Entity spawn(Location location) {
            ShulkerBullet bullet = location.getWorld().spawn(location, ShulkerBullet.class);
            bullet.setShooter(this.shooter.getPlayer());
            return bullet;
        }
    }

}
