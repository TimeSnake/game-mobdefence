package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.ArrayList;
import java.util.List;

public class ItemTrade extends ShopTrade {

    private final List<ExItemStack> sellingItems;

    public ItemTrade(int slot, boolean oneTimeBuy, ShopPrice price, List<ExItemStack> sellingItems,
                     ExItemStack displayItem, String... description) {
        super(slot, oneTimeBuy, price, displayItem, description);
        this.sellingItems = sellingItems;
    }

    public ItemTrade(boolean oneTimeBuy, ShopPrice price, List<ExItemStack> sellingItems, ExItemStack displayItem,
                     String... description) {
        this(0, oneTimeBuy, price, sellingItems, displayItem, description);
    }

    public ItemTrade(ItemTrade itemTrade) {
        super(itemTrade);
        this.sellingItems = new ArrayList<>();
        for (ExItemStack sellingItem : itemTrade.sellingItems) {
            this.sellingItems.add(sellingItem.cloneWithId());
        }
    }

    @Override
    public ItemTrade clone() {
        return new ItemTrade(this);
    }

    @Override
    public void sell(MobDefUser user) {
        for (ExItemStack sellingItem : this.sellingItems) {
            user.addItem(sellingItem.cloneWithId());
        }
    }

    public List<ExItemStack> getSellingItems() {
        return sellingItems;
    }
}
