/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickListener;
import de.timesnake.game.mobdefence.shop.Shop;
import de.timesnake.game.mobdefence.shop.UserShop;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class KitShop implements UserInventoryClickListener, InventoryHolder {

    private final ExInventory inv;
    private final Map<ExItemStack, Shop> shopsByItem = new HashMap<>();
    private MobDefUser user;

    public KitShop(MobDefUser user) {
        this.user = user;
        MobDefKit kit = ((MobDefKit) user.getKit());

        this.inv = new ExInventory(9 * 6, Component.text(kit.getName() + " Shop"), this);

        for (Supplier<Shop> shopSupplier : kit.getShopSuppliers()) {
            Shop shop;
            try {
                shop = shopSupplier.get();
            } catch (BuilderNotFullyInstantiatedException e) {
                e.printStackTrace();
                return;
            }
            if (shop instanceof UserShop) {
                ((UserShop) shop).setUser(user);
            }
            this.shopsByItem.put(shop.getDisplayItem(), shop);
            this.inv.setItemStack(shop.getSlot(), shop.getDisplayItem());

            shop.getUpgradeables().forEach(u -> u.loadBaseForUser(user));
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
        Shop shop = this.shopsByItem.get(clickedItem);

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

        for (Shop shop : this.shopsByItem.values()) {
            if (shop instanceof UserShop) {
                ((UserShop) shop).setUser(user);
            }
        }
    }

}
