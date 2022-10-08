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

import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ShopTrade {

    protected final int slot;

    protected final ExItemStack displayItem;

    protected ShopPrice price;

    protected final boolean oneTimeBuy;

    protected List<String> description;

    protected boolean bought = false;

    public ShopTrade(int slot, boolean oneTimeBuy, ShopPrice price, ExItemStack displayItem, String... description) {
        this.slot = slot;
        this.oneTimeBuy = oneTimeBuy;
        this.price = price;

        this.displayItem = displayItem.clone();

        this.displayItem.setDisplayName("§6" + this.displayItem.getItemMeta().getDisplayName());

        this.description = Arrays.asList(description);

        this.updateDisplayItemDescription();
    }

    public ShopTrade(boolean oneTimeBuy, ShopPrice price, ExItemStack displayItem, String... description) {
        this(0, oneTimeBuy, price, displayItem, description);
    }

    public ShopTrade(ShopTrade trade) {
        this.slot = trade.slot;
        this.displayItem = trade.displayItem.cloneWithId();
        this.price = trade.price;
        this.oneTimeBuy = trade.oneTimeBuy;
        this.bought = false;
        this.description = new ArrayList<>(trade.description);
    }

    protected void updateDisplayItemDescription() {
        List<String> lore = new ArrayList<>();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add("");
        lore.add("§9Price: §f" + this.price.getAmount() + " " + this.price.getCurrencyName());

        if (this.description.size() > 0) {
            lore.add("");
            lore.addAll(this.description);
        }

        this.displayItem.setExLore(lore);
    }

    @Override
    public abstract ShopTrade clone();

    public ExItemStack getDisplayItem() {
        return displayItem;
    }

    public ShopPrice getPrice() {
        return price;
    }

    public void sellTo(MobDefUser user, ExInventory inventory) {
        this.sell(user);
        this.bought = true;
    }

    public abstract void sell(MobDefUser user);

    public boolean isOneTimeBuy() {
        return oneTimeBuy;
    }

    public boolean isBought() {
        return bought;
    }

    public int getSlot() {
        return slot;
    }

}
