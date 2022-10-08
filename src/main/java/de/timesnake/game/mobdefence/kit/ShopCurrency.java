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

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import org.bukkit.Material;

public enum ShopCurrency {

    BRONZE("Bronze", new ExItemStack(Material.BRICK, "§6Bronze")),
    SILVER("Silver", new ExItemStack(Material.IRON_INGOT, "§6Silver")),
    GOLD("Gold", new ExItemStack(Material.GOLD_INGOT, "§6Gold")),
    EMERALD("Emerald", new ExItemStack(Material.EMERALD, "§6Emerald"));

    private final String name;
    private final ExItemStack item;

    ShopCurrency(String name, ExItemStack item) {
        this.name = name;
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public ExItemStack getItem() {
        return item;
    }


}
