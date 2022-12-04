/*
 * workspace.game-mobdefence.main
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

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.special.TeamHealth;
import de.timesnake.game.mobdefence.special.trap.TrapMaker;
import de.timesnake.game.mobdefence.special.weapon.WaterBottle;
import de.timesnake.game.mobdefence.user.ReviveManager;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;

public class BaseShops {

    public static final Shop.Builder BLOCK_SHOP = new Shop.Builder()
            .name("Blocks")
            .slot(31)
            .display(new ExItemStack(Material.OAK_FENCE))
            .addTrade(MobDefKit.OAK_FENCE, MobDefKit.OAK_FENCE_GATE, MobDefKit.OAK_PLANKS, MobDefKit.OAK_SLAB,
                    MobDefKit.IRON_BARS, MobDefKit.COBBLESTONE_WALL, MobDefKit.STONE_AXE, MobDefKit.IRON_PICKAXE)
            .addTrade(TrapMaker.getShopTrades().toArray(Trade.Builder[]::new))
            .type(Shop.Builder.Type.TEAM);

    public static final Shop.Builder BASIC_SHOP = new Shop.Builder()
            .name("Basics")
            .slot(29)
            .display(new ExItemStack(Material.COOKED_BEEF))
            .addTrade(MobDefKit.APPLE, MobDefKit.COOKED_BEEF, MobDefKit.PUMPKIN_PIE, MobDefKit.DRIED_KELP,
                    MobDefKit.GOLDEN_APPLE, MobDefKit.GOLDEN_CARROT, MobDefKit.MILK, WaterBottle.WATER,
                    MobDefKit.SPEED, MobDefKit.INSTANT_HEAL, MobDefKit.ENDER_PEARL)
            .type(Shop.Builder.Type.TEAM);

    public static final Shop.Builder TEAM_SHOP = new Shop.Builder()
            .name("Team Stuff")
            .slot(33)
            .display(new ExItemStack(Material.PLAYER_HEAD))
            .addUpgradeable(TeamHealth.HEALTH, ReviveManager.REVIVE)
            .addTrade(MobDefKit.IRON_GOLEM, MobDefKit.REGEN)
            .type(Shop.Builder.Type.TEAM);

    private static final List<Shop.Builder> SHOP_BUILDERS = List.of(BLOCK_SHOP, BASIC_SHOP, TEAM_SHOP);

    private final HashMap<String, Shop> shopsByName = new HashMap<>();

    public BaseShops() {
        this.resetShops();
    }

    public void resetShops() {
        this.shopsByName.clear();
        for (Shop.Builder shopBuilder : SHOP_BUILDERS) {
            Shop shop;
            try {
                shop = shopBuilder.build();
            } catch (BuilderNotFullyInstantiatedException e) {
                e.printStackTrace();
                continue;
            }
            this.shopsByName.put(shop.getName(), shop);
        }
    }

    public Shop getBlockShop() {
        return this.getShop("Blocks");
    }

    public Shop getShop(String name) {
        return this.shopsByName.get(name);
    }

    public Shop getBasicShop() {
        return this.getShop("Basics");
    }

    public Shop getTeamShop() {
        return this.getShop("Team Stuff");
    }
}