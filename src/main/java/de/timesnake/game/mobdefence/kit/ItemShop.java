package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickListener;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemShop implements UserInventoryClickListener, InventoryHolder {

    protected final int slot;
    protected final ExItemStack displayItem;
    protected final List<Levelable<?>> levelItems;
    protected final HashMap<Integer, Levelable<?>> levelItemsBySlot = new HashMap<>();
    protected final Map<Integer, ShopTrade> tradesByDisplayItemId = new HashMap<>();
    protected String name;
    protected ExInventory inv;

    public ItemShop(String name, int slot, ExItemStack displayItem, List<Levelable<?>> levelItems,
                    List<ShopTrade>... trades) {
        this.name = name;
        this.slot = slot;
        this.displayItem = displayItem;
        this.displayItem.setDisplayName("§6" + this.name);
        this.levelItems = levelItems;

        for (List<ShopTrade> tradeList : trades) {
            for (ShopTrade trade : tradeList) {
                this.tradesByDisplayItemId.put(trade.getDisplayItem().getId(), trade);
            }
        }
    }

    public ItemShop(ItemShop shop) {
        this.name = shop.name;
        this.slot = shop.slot;
        this.displayItem = shop.displayItem;

        this.levelItems = new ArrayList<>();

        for (Levelable<?> levelItem : shop.levelItems) {
            Levelable<?> clonedItem = levelItem.clone();
            this.levelItems.add(clonedItem);
        }

        for (ShopTrade trade : shop.getTrades()) {
            ShopTrade clonedItem = trade.clone();
            this.tradesByDisplayItemId.put(clonedItem.getDisplayItem().getId(), clonedItem);
        }

        // inventory

        if (this.getTrades().isEmpty()) {
            this.inv = Server.createExInventory(this.levelItems.size() * 9 + 18, this.name, this);

            int slot = 10;
            for (Levelable<?> levelItem : this.levelItems) {
                this.levelItemsBySlot.put(slot, levelItem);
                levelItem.fillInventoryRow(this.inv, slot);
                slot += 9;
            }
        } else if (this.levelItems.isEmpty()) {
            this.inv =
                    Server.createExInventory(this.getTrades().stream().max(Comparator.comparingInt(ShopTrade::getSlot)).get().getSlot() + 9, this.name, this);

            for (ShopTrade trade : this.getTrades()) {
                this.inv.setItemStack(trade.getSlot(), trade.getDisplayItem());
            }
        } else {
            this.inv = Server.createExInventory(54, this.name, this);

            int slot = this.levelItems.size() < 3 ? 10 : 1;

            for (Levelable<?> levelItem : this.levelItems) {
                this.levelItemsBySlot.put(slot, levelItem);
                levelItem.fillInventoryRow(this.inv, slot);
                slot += 9;
            }

            slot = slot < 4 * 9 ? slot + 18 : slot + 9;

            for (ShopTrade trade : this.getTrades()) {
                this.inv.setItemStack(slot, trade.getDisplayItem());
                slot++;
            }
        }

        Server.getInventoryEventManager().addClickListener(this, this);
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        event.setCancelled(true);

        MobDefUser user = (MobDefUser) event.getUser();
        ExItemStack clickedItem = event.getClickedItem();

        int baseSlot = event.getSlot() - (event.getSlot() % 9) + 1;

        Levelable<?> levelable = this.levelItemsBySlot.get(baseSlot);

        if (levelable != null) {
            levelable.onLevelClick(user, this.inv, clickedItem);
            user.updateInventory();
            return;
        }

        ShopTrade trade = this.tradesByDisplayItemId.get(clickedItem.getId());

        event.setCancelled(true);

        if (trade == null) {
            return;
        }

        if (trade.isOneTimeBuy() && trade.isBought()) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("You already bought this item", ExTextColor.WARNING));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return;
        }

        if (!user.containsAtLeast(trade.getPrice().asItem())) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Not enough money", ExTextColor.WARNING));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return;
        }

        user.removeCertainItemStack(trade.getPrice().asItem());

        trade.sellTo(user, this.inv);
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return this.slot;
    }

    public ExItemStack getDisplayItem() {
        return this.displayItem;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv.getInventory();
    }

    public ItemShop clone(MobDefUser user) {
        return new UserItemShop(user, this);
    }

    public Collection<ShopTrade> getTrades() {
        return tradesByDisplayItemId.values();
    }

}
