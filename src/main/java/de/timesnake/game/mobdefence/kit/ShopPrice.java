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

public class ShopPrice {

    private final int amount;
    private final ShopCurrency currency;

    public ShopPrice(int amount, ShopCurrency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public ShopCurrency getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrencyName() {
        return this.currency.getName();
    }

    public ExItemStack asItem() {
        ExItemStack cloned = this.currency.getItem().cloneWithId();
        cloned.setAmount(this.amount);
        return cloned;
    }

    public String toString() {
        return this.amount + " " + this.currency.getName();
    }
}