/*
 * workspace.game-mobdefence.main
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
