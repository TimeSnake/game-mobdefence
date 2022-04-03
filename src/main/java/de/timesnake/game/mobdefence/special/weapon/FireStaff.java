package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FireStaff extends InteractWeapon implements Listener, UserInventoryInteractListener {

    private static final String NAME = "firestaff";

    public static final double SPEED = 1; // blocks per second
    public static final int FIRE_RATE = 1; // per second
    public static final int FIRE_RADIUS = 2; // blocks
    public static final int BURNING_TIME = 3; // in seconds

    private static final ItemLevelType<?> SPEED_LEVELS = new ItemLevelType<>("Speed",
            new ExItemStack(Material.FEATHER), 1, 4, ItemLevel.getLoreNumberLevels("Speed", 1, 1, "", 2,
            List.of(new ShopPrice(4, ShopCurrency.BRONZE), new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(4,
                    ShopCurrency.GOLD)), "+0.2 Speed", List.of(1.2, 1.4, 1.6)));

    private static final ItemLevelType<?> FIRE_RADIUS_LEVELS = new ItemLevelType<>("Fire Radius",
            new ExItemStack(Material.TARGET), 1, 4, ItemLevel.getLoreNumberLevels("Fire Radius", 2, 1, "blocks", 2,
            List.of(new ShopPrice(7, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(5,
                    ShopCurrency.GOLD)), "+0.5 Blocks", List.of(2.5, 3, 3.5)));

    private static final ItemLevelType<?> BURNING_TIME_LEVELS = new ItemLevelType<>("Burning Time",
            new ExItemStack(Material.BLAZE_POWDER), 1, 4, ItemLevel.getLoreNumberLevels("Burning Time", 3, 0, "s", 2,
            List.of(new ShopPrice(8, ShopCurrency.BRONZE), new ShopPrice(7, ShopCurrency.SILVER), new ShopPrice(6,
                    ShopCurrency.GOLD)), "+1 Second", List.of(4, 5, 6)));

    private static final ItemLevelType<?> FIRE_RATE_LEVELS = new ItemLevelType<>("Fire Rate",
            new ExItemStack(Material.FIRE_CHARGE), 1, 4, ItemLevel.getLoreNumberLevels("Fire Rate", 4, 0, "per sec.",
            2, List.of(new ShopPrice(6, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(6,
                    ShopCurrency.GOLD)), "+1 per second", List.of(2, 3, 4)));

    public static final LevelItem FIRE_STAFF = new LevelItem("Fire Staff", true, new ShopPrice(4,
            ShopCurrency.SILVER), new ExItemStack(Material.BLAZE_ROD, "§6Fire Staff").setLore("",
            SPEED_LEVELS.getBaseLevelLore(SPEED), FIRE_RADIUS_LEVELS.getBaseLevelLore(FIRE_RADIUS),
            BURNING_TIME_LEVELS.getBaseLevelLore(BURNING_TIME), FIRE_RATE_LEVELS.getBaseLevelLore(FIRE_RATE)),
            new ExItemStack(Material.BLAZE_ROD), List.of(SPEED_LEVELS, FIRE_RADIUS_LEVELS, BURNING_TIME_LEVELS,
            FIRE_RATE_LEVELS));

    private final Set<User> fireStaffCooldownUser = new HashSet<>();

    public FireStaff() {
        super(FIRE_STAFF.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
        Server.getInventoryEventManager().addInteractListener(this, FIRE_STAFF.getItem());
    }

    @EventHandler
    public void onFireballHit(ProjectileHitEvent e) {
        Projectile proj = e.getEntity();

        if (proj.getCustomName() != null && proj.getCustomName().contains(NAME)) {

            String[] nameParts = proj.getCustomName().split(NAME);

            double radius = Double.parseDouble(nameParts[0]);
            int burningTime = Integer.parseInt(nameParts[1]);

            for (Entity entity : e.getEntity().getLocation().getNearbyEntitiesByType(LivingEntity.class, radius)) {
                if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                    entity.setFireTicks(burningTime * 20);
                }
            }
        }
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        if (!this.fireStaffCooldownUser.contains(user)) {
            this.fireStaffCooldownUser.add(user);

            List<String> lore = item.getLore();

            double speed = Double.parseDouble(SPEED_LEVELS.getValueFromLore(lore));
            double radius = Double.parseDouble(FIRE_RADIUS_LEVELS.getValueFromLore(lore));
            int burningTime = Integer.parseInt(BURNING_TIME_LEVELS.getValueFromLore(lore));
            int fireRate = Integer.parseInt(FIRE_RATE_LEVELS.getValueFromLore(lore));

            Fireball fireball = user.getExWorld().spawn(user.getPlayer().getEyeLocation(), Fireball.class);

            fireball.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));
            fireball.setDirection(user.getLocation().getDirection());
            fireball.setShooter(user.getPlayer());

            fireball.setCustomName(radius + NAME + burningTime);
            fireball.setCustomNameVisible(false);

            Server.runTaskLaterSynchrony(() -> this.fireStaffCooldownUser.remove(user), (int) ((1d / fireRate) * 20),
                    GameMobDefence.getPlugin());
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Fireball) {
            e.setYield(0);
            e.setCancelled(true);
        }
    }
}
