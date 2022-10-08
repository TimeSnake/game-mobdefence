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
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.Triple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrystalShard extends CooldownWeapon {

    private static final double DAMAGE = 6;

    private static final ItemLevelType<?> POWER = new ItemLevelType<>("Power", new ExItemStack(Material.RED_DYE), 1,
            7, ItemLevel.getLoreNumberLevels("Power", 1, 0, "", 1,
            List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(12, ShopCurrency.SILVER),
                    new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.SILVER),
                    new ShopPrice(14, ShopCurrency.GOLD), new ShopPrice(48, ShopCurrency.BRONZE),
                    new ShopPrice(43, ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.SILVER)),
            "+1 Power", List.of(2, 3, 4, 5, 6, 7, 8, 9, 10)));

    private static final ItemLevelType<?> PIERCING = new ItemLevelType<>("Piercing", new ExItemStack(Material.ARROW), 0,
            2, ItemLevel.getLoreNumberLevels("Piercing", 2, 0, "", 1,
            List.of(new ShopPrice(5, ShopCurrency.GOLD), new ShopPrice(16, ShopCurrency.BRONZE),
                    new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.BRONZE),
                    new ShopPrice(29, ShopCurrency.SILVER)),
            "+1", List.of(1, 2, 3, 4, 5)));
    public static final LevelItem SHARD = new LevelItem("Shard",
            new ExItemStack(Material.AMETHYST_SHARD).unbreakable().enchant().setLore("", POWER.getBaseLevelLore(1)).setDisplayName("§6Crystal Shard"),
            new ExItemStack(Material.BOW).unbreakable(), List.of(POWER, PIERCING));
    private static final ItemLevelType<?> FLAME = new ItemLevelType<>("Flame", new ExItemStack(Material.BLAZE_POWDER)
            , 0, 1, ItemLevel.getLoreNumberLevels("Flame", 3, 0, "", 1,
            List.of(new ShopPrice(64, ShopCurrency.BRONZE)), "Flame", List.of(1)));
    private Set<Triple<Item, Double, Integer>> shotShardsWithDamagePiercing = new HashSet<>();
    private BukkitTask task;

    public CrystalShard() {
        super(SHARD.getItem());
        this.run();
    }

    @Override
    public int getCooldown(ExItemStack item) {
        return 10;
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        if (!user.getPlayer().getInventory().containsAtLeast(SHARD.getItem(), 1)) {
            return;
        }

        //user.getPlayer().getInventory().removeItem(SPEER.getItem().asOne());

        int power = Integer.parseInt(POWER.getValueFromLore(item.getLore()));

        int piercing = 0;
        try {
            piercing = Integer.parseInt(PIERCING.getValueFromLore(item.getLore()));
        } catch (NumberFormatException ignored) {
        }

        Item shootItem = user.getWorld().spawn(user.getLocation(), Item.class);

        shootItem.setItemStack(new ItemStack(Material.AMETHYST_SHARD));
        shootItem.setVelocity(user.getPlayer().getLocation().getDirection().normalize().multiply(2));
        shootItem.setPickupDelay(Integer.MAX_VALUE);
        shootItem.setWillAge(false);

        this.shotShardsWithDamagePiercing.add(new Triple<>(shootItem, power + DAMAGE, piercing));

    }


    private void run() {
        this.task = Server.runTaskTimerSynchrony(() -> {

            Set<Triple<Item, Double, Integer>> shardsToRemove = new HashSet<>();

            for (Triple<Item, Double, Integer> entry : this.shotShardsWithDamagePiercing) {

                Item shard = entry.getA();
                double damage = entry.getB();

                Location loc = shard.getLocation();

                if (!BlockCheck.WALKABLE_IN.isTagged(loc.getBlock().getType()) || shard.getVelocity().length() < 0.2) {
                    shard.remove();
                    shardsToRemove.add(entry);
                }

                for (LivingEntity entity : loc.getNearbyLivingEntities(1, 2)) {
                    if (entity.getBoundingBox().contains(loc.toVector()) && !MobDefMob.DEFENDER_TYPES.contains(entity.getType())) {
                        entity.damage(damage);
                    }

                }
            }

            this.shotShardsWithDamagePiercing.removeAll(shardsToRemove);

        }, 0, 1, GameMobDefence.getPlugin());
    }
}
