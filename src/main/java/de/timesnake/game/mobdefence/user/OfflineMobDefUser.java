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

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.basic.loungebridge.util.user.OfflineUser;
import de.timesnake.game.mobdefence.kit.KitShop;

public class OfflineMobDefUser extends OfflineUser {

    private final boolean alive;
    private final KitShop shop;

    public OfflineMobDefUser(MobDefUser user) {
        super(user);
        this.alive = user.isAlive();
        this.shop = user.getShop();
    }

    @Override
    public void loadInto(GameUser user) {
        super.loadInto(user);
        ((MobDefUser) user).loadKit();

        ((MobDefUser) user).setShop(this.shop);
        ((MobDefUser) user).setAlive(this.alive);

        ((MobDefUser) user).loadGameSideboard();
    }
}
