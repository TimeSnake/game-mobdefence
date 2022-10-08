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

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.user.MobDefUser;

public abstract class InteractWeapon extends SpecialWeapon implements UserInventoryInteractListener {

    public InteractWeapon(ExItemStack item) {
        super(item);
        Server.getInventoryEventManager().addInteractListener(this, item);
    }

    public abstract void onInteract(ExItemStack item, MobDefUser user);

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        event.setCancelled(true);
        this.onInteract(event.getClickedItem(), ((MobDefUser) event.getUser()));
    }
}
