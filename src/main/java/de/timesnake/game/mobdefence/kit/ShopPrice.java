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