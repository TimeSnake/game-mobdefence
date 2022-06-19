package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SwingSword extends CooldownWeapon implements UserInventoryInteractListener {

    private static final double DAMAGE = 3;
    private static final double RADIUS = 1.5;
    private static final int COOLDOWN = 8; // in sec

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage",
            new ExItemStack(Material.RED_DYE), 1, 11, ItemLevel.getLoreNumberLevels("Damage", 1, 1, "❤", 2,
            List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(15, ShopCurrency.SILVER), new ShopPrice(7,
                    ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.BRONZE), new ShopPrice(25,
                    ShopCurrency.SILVER), new ShopPrice(15, ShopCurrency.GOLD), new ShopPrice(48,
                    ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.SILVER), new ShopPrice(64,
                    ShopCurrency.BRONZE), new ShopPrice(64, ShopCurrency.SILVER)), "+1 ❤", List.of(4, 5, 6, 7, 8, 9,
                    10, 11, 12, 13)));

    private static final ItemLevelType<?> RADIUS_LEVELS = new ItemLevelType<>("Radius",
            new ExItemStack(Material.TARGET), 1, 5, ItemLevel.getLoreNumberLevels("Radius", 2, 2, "blocks", 2,
            List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(14, ShopCurrency.SILVER), new ShopPrice(32,
                    ShopCurrency.BRONZE), new ShopPrice(13, ShopCurrency.GOLD)), "+0.25 blocks", List.of(1.75, 2,
                    2.25, 2.5)));

    private static final ItemLevelType<?> COOLDOWN_LEVELS = new ItemLevelType<>("Cooldown",
            new ExItemStack(Material.FEATHER), 1, 5, ItemLevel.getLoreNumberLevels("Cooldown", 3, 0, "s", 2,
            List.of(new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(18, ShopCurrency.SILVER), new ShopPrice(16,
                    ShopCurrency.GOLD), new ShopPrice(34, ShopCurrency.SILVER)), List.of("-2 s", "-2 s", "-2 s", "-2 " +
                    "s", "-1 s"), List.of(8, 6, 4, 2, 1)));

    public static final LevelItem SWORD = new LevelItem("Swing Sword", true, new ShopPrice(8, ShopCurrency.GOLD),
            new ExItemStack(Material.GOLDEN_SWORD, true).setDisplayName("§6Swing Sword").enchant().setLore("",
                    DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), RADIUS_LEVELS.getBaseLevelLore(RADIUS),
                    COOLDOWN_LEVELS.getBaseLevelLore(COOLDOWN)),
            new ExItemStack(Material.GOLDEN_SWORD, true).enchant(), List.of(DAMAGE_LEVELS, RADIUS_LEVELS,
            COOLDOWN_LEVELS));

    private final Map<ArmorStand, BukkitTask> tasks = new HashMap<>();

    public SwingSword() {
        super(SWORD.getItem());
        Server.getInventoryEventManager().addInteractListener(this, SWORD.getItem());
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(item.getItemMeta().getLore()));
        double radius = Double.parseDouble(RADIUS_LEVELS.getValueFromLore(item.getItemMeta().getLore()));

        Location loc = user.getLocation().clone().add(0, -0.35, 0);

        for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, 1.5, radius,
                e -> MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getType()))) {

            Vector vec = entity.getLocation().toVector().subtract(loc.toVector());
            double knockback = 2 / (vec.length() > 1 ? vec.length() : 1D);

            ((LivingEntity) entity).damage(damage * 2, user.getPlayer());
            entity.setVelocity(vec.setY(0).normalize().setY(1).normalize().multiply(knockback));
        }

        ArmorStand stand = user.getExWorld().spawn(loc, ArmorStand.class);

        stand.setVisible(false);
        stand.getEquipment().setItem(EquipmentSlot.HEAD, SWORD.getItem());
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setCustomName(user.getPlayer().getName());
        stand.setCustomNameVisible(false);
        stand.setHeadPose(new EulerAngle(Math.PI / 2, 0, 0));

        AtomicReference<Float> rotation = new AtomicReference<>((float) 0);

        this.tasks.put(stand, Server.runTaskTimerSynchrony(() -> {
            if (rotation.get() >= 720) {
                stand.remove();
                BukkitTask task = this.tasks.remove(stand);
                task.cancel();
                return;
            }

            rotation.updateAndGet(v -> v + 36);
            stand.setRotation(rotation.get(), 0);

        }, 0, 1, GameMobDefence.getPlugin()));
    }

    @Override
    public int getCooldown(ExItemStack item) {
        return Integer.parseInt(COOLDOWN_LEVELS.getValueFromLore(item.getItemMeta().getLore())) * 20;
    }
}
