package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.chat.ChatColor;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickListener;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class KitShop implements UserInventoryClickListener, InventoryHolder {

    private MobDefUser user;

    private final ExInventory inv;
    private final Map<ExItemStack, ItemShop> shopsByItem = new HashMap<>();

    public KitShop(MobDefUser user) {
        this.user = user;
        MobDefKit kit = ((MobDefKit) user.getKit());

        this.inv = Server.createExInventory(9 * 6, ChatColor.WHITE + kit.getName() + " Shop", this);

        for (ItemShop shop : kit.getShopInventories()) {
            shop = shop.clone(user);
            this.shopsByItem.put(shop.getDisplayItem(), shop);
            this.inv.setItemStack(shop.getSlot(), shop.getDisplayItem());
        }

        Server.getInventoryEventManager().addClickListener(this, this);
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {

        event.setCancelled(true);

        if (!this.user.equals(event.getUser())) {
            return;
        }

        ExItemStack clickedItem = event.getClickedItem();
        ItemShop shop = this.shopsByItem.get(clickedItem);

        if (shop == null) {
            return;
        }

        user.openInventory(shop.getInventory());

    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv.getInventory();
    }

    public void setUser(MobDefUser user) {
        this.user = user;

        for (ItemShop shop : this.shopsByItem.values()) {
            if (shop instanceof UserItemShop) {
                ((UserItemShop) shop).setUser(user);
            }
        }
    }

}
