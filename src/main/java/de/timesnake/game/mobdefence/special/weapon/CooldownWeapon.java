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
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.HashSet;
import java.util.Set;

public abstract class CooldownWeapon extends InteractWeapon {

    private final Set<MobDefUser> cooldownUsers = new HashSet<>();

    public CooldownWeapon(ExItemStack item) {
        super(item);
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        MobDefUser user = ((MobDefUser) event.getUser());

        if (this.cooldownUsers.contains(user)) {
            event.setCancelled(true);
            return;
        }

        this.cooldownUsers.add(user);

        Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), this.getCooldown(event.getClickedItem()),
                GameMobDefence.getPlugin());

        super.onUserInventoryInteract(event);
    }

    public abstract int getCooldown(ExItemStack item);
}
