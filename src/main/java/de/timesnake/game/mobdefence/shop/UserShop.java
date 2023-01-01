/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.game.mobdefence.user.MobDefUser;

public class UserShop extends Shop {

    private MobDefUser user;

    public UserShop(MobDefUser user, Shop.Builder builder) {
        super(builder);
        this.user = user;
    }

    public void setUser(MobDefUser user) {
        this.user = user;
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        if (!user.equals(event.getUser())) {
            return;
        }

        super.onUserInventoryClick(event);
    }
}
