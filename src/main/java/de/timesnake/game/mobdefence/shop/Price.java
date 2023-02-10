/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;

public class Price {

    public static Price bronze(int amount) {
        return new Price(amount, Currency.BRONZE);
    }

    public static Price silver(int amount) {
        return new Price(amount, Currency.SILVER);
    }

    public static Price gold(int amount) {
        return new Price(amount, Currency.GOLD);
    }

    public static Price emerald(int amount) {
        return new Price(amount, Currency.EMERALD);
    }

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