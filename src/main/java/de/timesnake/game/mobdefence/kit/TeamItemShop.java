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
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.List;

public class TeamItemShop extends ItemShop {

    public TeamItemShop(String name, int shopSlot, ExItemStack displayItem, List<Levelable<?>> levelItems,
                        List<ShopTrade>... trades) {
        super(name, shopSlot, displayItem, levelItems, trades);
    }

    public TeamItemShop(TeamItemShop shop) {
        super(shop);
        this.name = this.name + " I";
    }

    @Override
    public TeamItemShop clone(MobDefUser user) {
        return MobDefServer.getBaseShops().getShop(this.name);
    }

    @Override
    public TeamItemShop clone() {
        return new TeamItemShop(this);
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        super.onUserInventoryClick(event);
        System.out.println(this.name);

        for (User user : Server.getInGameUsers()) {
            user.updateInventory();
        }
    }
}
