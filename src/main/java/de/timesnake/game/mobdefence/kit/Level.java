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

public class Level<V> {

    protected final int level;
    protected final ShopPrice price;
    protected final String description;

    protected int unlockWave = 0;

    protected final V value;

    public Level(int level, ShopPrice price, String description, V value) {
        this.level = level;
        this.price = price;
        this.description = description;
        this.value = value;
    }

    public Level(int level, int unlockWave, ShopPrice price, String description, V value) {
        this(level, price, description, value);
        this.unlockWave = unlockWave;
    }

    public int getLevel() {
        return level;
    }

    public ShopPrice getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public V getValue() {
        return value;
    }

    public int getUnlockWave() {
        return unlockWave;
    }
}
