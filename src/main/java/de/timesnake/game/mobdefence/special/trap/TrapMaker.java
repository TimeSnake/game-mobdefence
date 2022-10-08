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

package de.timesnake.game.mobdefence.special.trap;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.game.mobdefence.kit.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum TrapMaker {

    EXPLOSION(new IncreasingItemTrade(31, new ShopPrice(8, ShopCurrency.BRONZE), 1, 8,
            List.of(new ExItemStack(Material.CRIMSON_BUTTON, "§6Explosion Trap").setLore("§fRadius: §73 blocks")),
            new ExItemStack(Material.CRIMSON_BUTTON, "§6Explosion Trap"), "§fRadius: §73 blocks", "§7Explodes if 3 " +
            "mobs are nearby")) {
        @Override
        public Trap newInstance(ExBlock block) {
            return new RangedTrap(block, 2, 3) {
                @Override
                public boolean trigger(Collection<LivingEntity> entities) {
                    this.getLocation().getWorld().createExplosion(this.getLocation(), 4, false, false);
                    return super.trigger(entities);
                }
            };
        }
    },

    ARROW(new IncreasingItemTrade(32, new ShopPrice(3, ShopCurrency.SILVER), 1, 8,
            List.of(new ExItemStack(Material.STONE_BUTTON, "§6Arrow Trap").setLore("§fRadius: §77 blocks")),
            new ExItemStack(Material.STONE_BUTTON, "§6Arrow Trap"), "§fRadius: §77 blocks", "§fUses: §716", "§7Shoots" +
            " against mobs")) {
        @Override
        public Trap newInstance(ExBlock block) {
            return new MultipleRangedTrap(block, 7, 1, 16) {
                @Override
                public boolean trigger(Collection<LivingEntity> entities) {

                    for (LivingEntity entity : entities) {
                        this.getLocation().getWorld().playSound(this.getLocation(), Sound.ENTITY_ARROW_SHOOT, 2, 1);
                        Location eyeLoc = entity.getEyeLocation();
                        Location loc = this.getLocation();
                        Vector vec =
                                new Vector(eyeLoc.getX(), eyeLoc.getY(), eyeLoc.getZ()).subtract(new Vector(loc.getX(), loc.getY(), loc.getZ()));

                        loc.getWorld().spawnArrow(loc, vec, 5, 1);
                    }

                    return super.trigger(entities);
                }
            };
        }
    },

    SLOWNESS(new IncreasingItemTrade(33, new ShopPrice(7, ShopCurrency.BRONZE), 1, 8,
            List.of(new ExItemStack(Material.POLISHED_BLACKSTONE_BUTTON, "§6Slowness Trap").setLore("§fRadius: §75 " +
                    "blocks")), new ExItemStack(Material.POLISHED_BLACKSTONE_BUTTON, "§6Slowness Trap"), "§fRadius: " +
            "§75 blocks", "§fUses: §73", "§7Gives mobs slowness V for 20s")) {
        @Override
        public Trap newInstance(ExBlock block) {
            return new MultipleRangedTrap(block, 5, 2, 3) {
                @Override
                public boolean trigger(Collection<LivingEntity> entities) {
                    this.getLocation().getWorld().playSound(this.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 2, 1);
                    for (LivingEntity entity : entities) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 20, 4));
                    }

                    return super.trigger(entities);
                }
            };
        }
    },

    POISON(new IncreasingItemTrade(34, new ShopPrice(7, ShopCurrency.BRONZE), 1, 8,
            List.of(new ExItemStack(Material.WARPED_BUTTON, "§6Poison Trap").setLore("§fRadius: §74 blocks")),
            new ExItemStack(Material.WARPED_BUTTON, "§6Poison Trap"), "§fRadius: §74 blocks", "§7Gives mobs poison " +
            "III for 20s")) {
        @Override
        public Trap newInstance(ExBlock block) {
            return new RangedTrap(block, 4, 4) {
                @Override
                public boolean trigger(Collection<LivingEntity> entities) {
                    this.getLocation().getWorld().playSound(this.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 2, 1);
                    for (LivingEntity entity : entities) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 20, 3));
                    }

                    return super.trigger(entities);
                }
            };
        }
    };


    public static List<ShopTrade> getShopTrades() {
        List<ShopTrade> trades = new ArrayList<>();

        for (TrapMaker trapMaker : TrapMaker.values()) {
            trades.add(trapMaker.getTrade());
        }
        return trades;
    }

    private final ExItemStack item;
    private final ItemTrade trade;

    TrapMaker(ItemTrade trade) {
        this.trade = trade;
        this.item = trade.getSellingItems().iterator().next();
    }

    public ExItemStack getItem() {
        return item;
    }

    public ItemTrade getTrade() {
        return trade;
    }

    public abstract Trap newInstance(ExBlock block);
}
