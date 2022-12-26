/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.ExItemStack;

public class Price {

    private final int amount;
    private final Currency currency;

    public Price(int amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Currency getCurrency() {
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