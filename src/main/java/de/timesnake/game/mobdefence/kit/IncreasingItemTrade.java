package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class IncreasingItemTrade extends ItemTrade {

    private int priceIndex;
    private final List<ShopPrice> prices;

    public IncreasingItemTrade(int slot, List<ShopPrice> prices, List<ExItemStack> sellingItems,
                               ExItemStack displayItem) {
        super(slot, false, prices.get(0), sellingItems, displayItem, "§cThe price increases per buy");
        this.priceIndex = 0;
        this.prices = prices;
    }

    public IncreasingItemTrade(int slot, ShopPrice basePrice, int increase, int increaseMultiplier,
                               List<ExItemStack> sellingItems, ExItemStack displayItem, String... description) {
        super(slot, false, basePrice, sellingItems, displayItem, Stream.concat(Arrays.stream(description), Stream.of(
                "§cThe price increases per buy")).toArray(String[]::new));
        this.priceIndex = 0;
        this.prices = new LinkedList<>();

        for (int i = basePrice.getAmount(); i < 64; i += increase) {
            for (int j = 0; j < increaseMultiplier; j++) {
                this.prices.add(new ShopPrice(i, basePrice.getCurrency()));
            }
        }
    }

    public IncreasingItemTrade(IncreasingItemTrade trade) {
        super(trade);
        this.priceIndex = 0;
        this.prices = trade.prices;
    }

    @Override
    public IncreasingItemTrade clone() {
        return new IncreasingItemTrade(this);
    }

    @Override
    public void sellTo(MobDefUser user, ExInventory inventory) {
        super.sellTo(user, inventory);
        if (this.priceIndex < this.prices.size() - 1) {
            this.priceIndex++;
            super.price = this.prices.get(this.priceIndex);
            super.updateDisplayItemDescription();
            inventory.setItemStack(this.getSlot(), this.getDisplayItem());
        }
    }
}
