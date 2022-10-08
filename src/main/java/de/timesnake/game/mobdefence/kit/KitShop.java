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
