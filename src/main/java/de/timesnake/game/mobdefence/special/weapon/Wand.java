package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Wand extends SpecialWeapon implements UserInventoryInteractListener, Listener {

    private static final String BULLET_NAME = "wand_bullet";
    private static final String PIERCING_NAME = "piercing";
    private static final double FIRE_RATE = 2; // per second
    private static final double DAMAGE = 1.5;
    private static final double SPEED = 2;

    private static final ItemLevelType<?> SPEED_LEVELS = new ItemLevelType<>("Speed", new ExItemStack(Material.FEATHER), 1, 4, ItemLevel.getLoreNumberLevels("Speed", 1, 1, "", 2, List.of(new ShopPrice(4, ShopCurrency.BRONZE), new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(4, ShopCurrency.GOLD)), "+0.2 Speed", List.of(2.4, 2.8, 3.2)));

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 8, ItemLevel.getLoreNumberLevels("Damage", 2, 1, "❤", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER), new ShopPrice(5, ShopCurrency.GOLD), new ShopPrice(10, ShopCurrency.GOLD), new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.SILVER)), "+0.5 ❤", List.of(2, 2.5, 3, 3.5, 4, 4, 5, 6)));

    private static final ItemLevelType<?> FIRE_RATE_LEVELS = new ItemLevelType<>("Fire Rate", new ExItemStack(Material.FIRE_CHARGE), 1, 5, ItemLevel.getLoreNumberLevels("Fire Rate", 3, 0, "per sec.", 2, List.of(new ShopPrice(6, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(64, ShopCurrency.BRONZE)), "+1 per second", List.of(3, 4, 5, 6)));

    private static final ItemLevelType<?> PIERCING_LEVELS = new ItemLevelType<>("Piercing", new ExItemStack(Material.TORCH), 0, 5, ItemLevel.getLoreNumberLevels("Piercing", 4, 0, "mobs", 1, List.of(new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(11, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)), "+1 mob", List.of(1, 2, 3, 4, 5)));

    private static final ItemLevelType<?> MULTISHOT_LEVELS = new ItemLevelType<>("Multishot", new ExItemStack(Material.SOUL_TORCH), 0, 2, ItemLevel.getLoreNumberLevels("Multishot", 4, 0, "bullets", 1, List.of(new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.GOLD)), "+1 bullet", List.of(2, 3)));

    static {
        PIERCING_LEVELS.setConflictingTypes(List.of(MULTISHOT_LEVELS));
        MULTISHOT_LEVELS.setConflictingTypes(List.of(PIERCING_LEVELS));
    }

    public static final LevelItem WAND = new LevelItem("Wand", new ExItemStack(Material.STICK, "§3Wand").enchant().setLore("", SPEED_LEVELS.getBaseLevelLore(SPEED), DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), FIRE_RATE_LEVELS.getBaseLevelLore(FIRE_RATE)), new ExItemStack(Material.STICK).enchant(), List.of(SPEED_LEVELS, DAMAGE_LEVELS, FIRE_RATE_LEVELS, PIERCING_LEVELS, MULTISHOT_LEVELS));

    private final Set<User> cooldownUsers = new HashSet<>();

    public Wand() {
        super(WAND.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
        Server.getInventoryEventManager().addInteractListener(this, WAND.getItem());
    }


    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        if (this.cooldownUsers.contains(user)) {
            event.setCancelled(true);
            return;
        }

        ExItemStack wand = user.getExItem(WAND.getItem().getId());

        if (wand == null) {
            return;
        }

        List<String> lore = wand.getLore();

        double speed = Double.parseDouble(SPEED_LEVELS.getValueFromLore(lore));
        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(lore));
        double fireRate = Double.parseDouble(FIRE_RATE_LEVELS.getValueFromLore(lore));

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

        this.cooldownUsers.add(user);

        for (int shots = 0; shots < multiShot; shots++) {
            int finalPiercing = piercing;
            Server.runTaskLaterSynchrony(() -> {
                ShulkerBullet bullet = user.getExWorld().spawn(user.getPlayer().getEyeLocation().add(0, -0.5, 0), ShulkerBullet.class);
                bullet.setShooter(user.getPlayer());
                bullet.setVelocity(user.getLocation().getDirection().multiply(speed));
                bullet.setCustomName(BULLET_NAME + damage + PIERCING_NAME + finalPiercing);
                bullet.setCustomNameVisible(false);
                bullet.setPersistent(true);
            }, shots * 2, GameMobDefence.getPlugin());

        }

        Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), (int) (1 / fireRate * 20), GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onBulletHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof ShulkerBullet)) {
            return;
        }

        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity)) {
            return;
        }

        ShulkerBullet bullet = ((ShulkerBullet) e.getEntity());

        if (bullet.getCustomName() == null || !bullet.getCustomName().contains(BULLET_NAME)) {
            return;
        }

        LivingEntity entity = (LivingEntity) e.getHitEntity();

        e.setCancelled(true);

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            return;
        }

        String[] nameParts = bullet.getCustomName().replaceAll(BULLET_NAME, "").split(PIERCING_NAME);

        double damage = Double.parseDouble(nameParts[0]) * 2;
        int piercing = Integer.parseInt(nameParts[1]);

        entity.damage(damage, bullet);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));

        if (piercing == 0) {
            bullet.remove();
        }

    }
}
