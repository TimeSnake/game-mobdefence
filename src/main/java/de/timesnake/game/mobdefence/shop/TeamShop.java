/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;

public class TeamShop extends Shop {

    public TeamShop(Shop.Builder builder) {
        super(builder);
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        super.onUserInventoryClick(event);
        for (User user : Server.getInGameUsers()) {
            user.updateInventory();
        }
    }
}
