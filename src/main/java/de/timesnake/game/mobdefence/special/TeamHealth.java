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

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamHealth extends Levelable<LevelType<Level<Integer>>> {

    public static final int BASE_MAX_HEALTH = 8 * 2;

    private static List<Level<Integer>> getHealthLevels(int start, List<ShopPrice> prices,
                                                        List<Integer> maxHealthLimits) {
        List<Level<Integer>> levels = new ArrayList<>();

        Iterator<ShopPrice> priceIt = prices.listIterator();
        Iterator<Integer> maxHealthIt = maxHealthLimits.listIterator();

        for (int level = start; priceIt.hasNext() && maxHealthIt.hasNext(); level++) {
            levels.add(new Level<>(level, priceIt.next(), "+1 ❤ Max Health", maxHealthIt.next()));
        }
        return levels;
    }

    private int maxHealth = BASE_MAX_HEALTH;

    protected TeamHealth(String name, ExItemStack displayItem, List<LevelType<Level<Integer>>> levelTypes) {
        super(name, displayItem, levelTypes);
    }

    protected TeamHealth(TeamHealth levelable) {
        super(levelable);
    }

    public void reset() {
        this.maxHealth = BASE_MAX_HEALTH;
    }

    @Override
    public LevelType<Level<Integer>> cloneLevelType(LevelType<Level<Integer>> levelType) {
        return levelType;
    }

    @Override
    public Levelable<LevelType<Level<Integer>>> clone() {
        return new TeamHealth(this);
    }

    @Override
    public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
        LevelType<?> levelType = this.levelTypeByItemId.get(item.getId());

        if (levelType == null) {
            return;
        }

        levelType.tryLevelUp(user);

        inv.setItemStack(levelType.getDisplayItem());
    }    public static final TeamHealth MAX_HEALTH = new TeamHealth("Health", new ExItemStack(Material.NETHER_WART),
            List.of(new LevelType<>("Max Health", new ExItemStack(Material.FIRE_CORAL_BLOCK), 1, 13,
                    getHealthLevels(2, List.of(new ShopPrice(1, ShopCurrency.EMERALD), new ShopPrice(2,
                            ShopCurrency.EMERALD), new ShopPrice(3, ShopCurrency.EMERALD), new ShopPrice(4,
                            ShopCurrency.EMERALD), new ShopPrice(5, ShopCurrency.EMERALD), new ShopPrice(6,
                            ShopCurrency.EMERALD), new ShopPrice(7, ShopCurrency.EMERALD), new ShopPrice(8,
                            ShopCurrency.EMERALD), new ShopPrice(9, ShopCurrency.EMERALD), new ShopPrice(10,
                            ShopCurrency.EMERALD), new ShopPrice(11, ShopCurrency.EMERALD), new ShopPrice(12,
                            ShopCurrency.EMERALD)), List.of(9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20))) {
                @Override
                protected boolean levelUp(MobDefUser user, Level<Integer> level) {
                    for (User gameUser : Server.getInOutGameUsers()) {
                        gameUser.getPlayer().setMaxHealth(level.getValue() * 2);
                    }

                    MAX_HEALTH.setMaxHealth(level.getValue() * 2);

                    MobDefServer.broadcastGameMessage(user.getChatNameComponent()
                            .append(Component.text(" leveled up max " + "health", ExTextColor.WARNING)));
                    return true;
                }
            }));

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }




}
