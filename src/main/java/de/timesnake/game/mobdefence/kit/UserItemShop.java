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

import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.game.mobdefence.user.MobDefUser;

public class UserItemShop extends ItemShop {

    private MobDefUser user;

    public UserItemShop(MobDefUser user, ItemShop shop) {
        super(shop);
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
