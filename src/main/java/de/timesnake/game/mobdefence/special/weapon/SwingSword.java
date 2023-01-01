/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class SwingSword extends CooldownWeapon implements UserInventoryInteractListener {

    private static final ExItemStack ITEM = new ExItemStack(Material.GOLDEN_SWORD)
            .unbreakable().setDisplayName("§6Swing Sword").enchant().immutable();

    private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
            .name("Damage")
            .display(new ExItemStack(Material.RED_DYE))
            .baseLevel(1)
            .levelDescription("+1 ❤")
            .levelUnit("❤")
            .levelDecimalDigit(0)
            .levelLoreLine(1)
            .levelLoreName("Damage")
            .levelItem(ITEM)
            .addLoreLvl(null, 3)
            .addLoreLvl(new Price(9, Currency.BRONZE), 4)
            .addLoreLvl(new Price(15, Currency.SILVER), 5)
            .addLoreLvl(new Price(7, Currency.GOLD), 6)
            .addLoreLvl(new Price(27, Currency.BRONZE), 7)
            .addLoreLvl(new Price(25, Currency.SILVER), 8)
            .addLoreLvl(new Price(15, Currency.GOLD), 9)
            .addLoreLvl(new Price(48, Currency.BRONZE), 10)
            .addLoreLvl(new Price(48, Currency.SILVER), 11)
            .addLoreLvl(new Price(64, Currency.BRONZE), 12)
            .addLoreLvl(new Price(64, Currency.SILVER), 13);

    private static final LevelType.Builder RADIUS_LEVELS = new LevelType.Builder()
            .name("Radius")
            .display(new ExItemStack(Material.TARGET))
            .baseLevel(1)
            .levelDescription("+0.25 Blocks")
            .levelDecimalDigit(1)
            .levelUnit("blocks")
            .levelLoreLine(2)
            .levelLoreName("Radius")
            .levelItem(ITEM)
            .addLoreLvl(null, 1.5)
            .addLoreLvl(new Price(9, Currency.BRONZE), 1.75)
            .addLoreLvl(new Price(14, Currency.SILVER), 2)
            .addLoreLvl(new Price(32, Currency.BRONZE), 2.25)
            .addLoreLvl(new Price(13, Currency.GOLD), 2.5);

    private static final LevelType.Builder COOLDOWN_LEVELS = new LevelType.Builder()
            .name("Cooldown")
            .display(new ExItemStack(Material.FEATHER))
            .baseLevel(1)
            .levelDecimalDigit(0)
            .levelUnit("s")
            .levelLoreLine(3)
            .levelLoreName("Cooldown")
            .levelItem(ITEM)
            .levelDescription("-2 s")
            .addLoreLvl(null, 10)
            .addLoreLvl(new Price(12, Currency.BRONZE), 8)
            .addLoreLvl(new Price(18, Currency.SILVER), 6)
            .addLoreLvl(new Price(16, Currency.GOLD), 4)
            .addLoreLvl(new Price(34, Currency.SILVER), 2)
            .levelDescription("-1 s")
            .addLoreLvl(new Price(64, Currency.BRONZE), 1);

    public static final UpgradeableItem.Builder SWORD = new UpgradeableItem.Builder()
            .name("Swing Sword")
            .price(new Price(8, Currency.GOLD))
            .baseItem(ITEM.cloneWithId())
            .display(ITEM.cloneWithId())
            .addLvlType(RADIUS_LEVELS)
            .addLvlType(DAMAGE_LEVELS)
            .addLvlType(COOLDOWN_LEVELS);


    private final Map<ArmorStand, BukkitTask> tasks = new HashMap<>();

    public SwingSword() {
        super(ITEM);
        Server.getInventoryEventManager().addInteractListener(this, ITEM);
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        double damage = DAMAGE_LEVELS.getNumberFromLore(item, Double::valueOf);
        double radius = RADIUS_LEVELS.getNumberFromLore(item, Double::valueOf);

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
        stand.getEquipment().setItem(EquipmentSlot.HEAD, ITEM);
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
        return COOLDOWN_LEVELS.getNumberFromLore(item, Integer::valueOf) * 20;
    }
}
