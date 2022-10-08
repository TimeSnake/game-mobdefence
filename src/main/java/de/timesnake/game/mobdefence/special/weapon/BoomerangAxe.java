/*
 * game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BoomerangAxe extends CooldownWeapon implements UserInventoryInteractListener {

    public static final double SPEED = 1;
    public static final double MAX_DISTANCE = 10;
    public static final double DAMAGE = 4;

    private static final ItemLevelType<?> SPEED_LEVELS = new ItemLevelType<>("Speed",
            new ExItemStack(Material.FEATHER), 1, 6, ItemLevel.getLoreNumberLevels("Speed", 1, 1, "", 2,
            List.of(new ShopPrice(6, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(6,
                    ShopCurrency.GOLD), new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(32,
                    ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.BRONZE), new ShopPrice(64,
                    ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.GOLD), new ShopPrice(48,
                    ShopCurrency.SILVER)), "+0.2 Speed", List.of(1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6)));

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage",
            new ExItemStack(Material.RED_DYE), 1, 9, ItemLevel.getLoreNumberLevels("Damage", 2, 1, "❤", 2,
            List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER), new ShopPrice(5,
                    ShopCurrency.GOLD), new ShopPrice(31, ShopCurrency.SILVER), new ShopPrice(48,
                    ShopCurrency.BRONZE), new ShopPrice(45, ShopCurrency.SILVER), new ShopPrice(64,
                    ShopCurrency.BRONZE), new ShopPrice(18, ShopCurrency.GOLD)), "+0.5 ❤", List.of(4.5, 5, 5.5, 6, 7,
                    8, 9, 10)));

    private static final ItemLevelType<?> DISTANCE_LEVELS = new ItemLevelType<>("Distance",
            new ExItemStack(Material.CHAIN), 1, 5, ItemLevel.getLoreNumberLevels("Distance", 3, 0, "blocks", 2,
            List.of(new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(14, ShopCurrency.SILVER), new ShopPrice(10,
                    ShopCurrency.GOLD), new ShopPrice(41, ShopCurrency.BRONZE)), "+1 Block", List.of(11, 12, 13, 14)));

    public static final LevelItem BOOMERANG_AXE = new LevelItem("Boomerang Axe", true, new ShopPrice(4,
            ShopCurrency.GOLD), new ExItemStack(Material.IRON_AXE, "§6Boomerang Axe").setLore("",
            SPEED_LEVELS.getBaseLevelLore(SPEED), DAMAGE_LEVELS.getBaseLevelLore(DAMAGE),
            DISTANCE_LEVELS.getBaseLevelLore(MAX_DISTANCE)), new ExItemStack(Material.IRON_AXE, "§6Boomerang Axe"),
            List.of(SPEED_LEVELS, DAMAGE_LEVELS, DISTANCE_LEVELS));

    private final Map<ArmorStand, BukkitTask> tasks = new HashMap<>();

    public BoomerangAxe() {
        super(BOOMERANG_AXE.getItem());
        Server.getInventoryEventManager().addInteractListener(this, BOOMERANG_AXE.getItem());
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        double speed = Double.parseDouble(SPEED_LEVELS.getValueFromLore(item.getLore()));
        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(item.getLore()));

        final Location startLoc = user.getLocation().clone().add(0, -0.35, 0);

        ArmorStand stand = user.getExWorld().spawn(startLoc, ArmorStand.class);

        final org.bukkit.util.Vector startVec = user.getLocation().getDirection().normalize().multiply(speed);

        Vector vec = startVec.clone();

        stand.setVisible(false);
        stand.setItem(EquipmentSlot.HEAD, BOOMERANG_AXE.getItem());
        stand.setInvulnerable(true);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setKiller(user.getPlayer());
        stand.setHeadPose(new EulerAngle(Math.PI / 2, 0, 0));

        AtomicReference<Float> rotation = new AtomicReference<>((float) 0);

        AtomicInteger counter = new AtomicInteger();

        this.tasks.put(stand, Server.runTaskTimerSynchrony(() -> {
            Location loc = stand.getLocation();

            double distance = loc.distanceSquared(startLoc);

            if (distance < 0.2 && !startVec.equals(vec)) {
                this.dropBoomerang(stand);
                return;
            }

            if (distance >= MAX_DISTANCE * MAX_DISTANCE) {
                vec.multiply(-1);
            }

            if (!BlockCheck.WALKABLE_IN.isTagged(stand.getEyeLocation().add(vec).getBlock().getType())) {
                if (counter.get() >= 1) {
                    this.dropBoomerang(stand);
                    return;
                }

                vec.multiply(-1);
                counter.getAndIncrement();
            }

            for (LivingEntity entity : loc.getNearbyLivingEntities(1, 2)) {
                if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                    entity.damage(damage * 2, user.getPlayer());
                }
            }

            stand.setVelocity(vec);
            stand.teleport(loc.add(vec));
            rotation.updateAndGet(v -> v + 30);
            stand.setRotation(rotation.get(), 0);

        }, 0, 1, GameMobDefence.getPlugin()));
    }

    @Override
    public int getCooldown(ExItemStack item) {
        return 20;
    }

    private void dropBoomerang(ArmorStand stand) {
        stand.remove();
        BukkitTask task = this.tasks.remove(stand);
        task.cancel();
    }
}
