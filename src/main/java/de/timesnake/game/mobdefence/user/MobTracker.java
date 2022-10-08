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

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class MobTracker implements UserInventoryInteractListener {

    public static final ExItemStack TRACKER = new ExItemStack(Material.COMPASS, "§6Mob Tracker").setSlot(8);

    public MobTracker() {
        Server.getInventoryEventManager().addInteractListener(this, TRACKER);
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        Collection<Entity> mobs = MobDefServer.getMobManager().getAliveMobs();

        user.getPlayer().setCompassTarget(user.getLocation());

        if (mobs.isEmpty()) {
            user.sendActionBarText(Component.text("No mobs alive", ExTextColor.WARNING));
            return;
        }

        if (mobs.size() > 3) {
            user.sendActionBarText(Component.text("Too many mobs to track", ExTextColor.WARNING));
            return;
        }

        Entity nearestEntity = null;
        double distance = 100;

        for (Entity entity : mobs) {
            double entityDistanceSquared = entity.getLocation().distanceSquared(user.getLocation());
            if (entityDistanceSquared < distance * distance) {
                nearestEntity = entity;
                distance = Math.sqrt(entityDistanceSquared);
            }
        }

        if (nearestEntity == null) {
            user.sendActionBarText(Component.text("No mob nearby", ExTextColor.WARNING));
            return;
        }

        user.getPlayer().setCompassTarget(nearestEntity.getLocation());
        user.sendActionBarText(Component.text("Nearest mob: ", ExTextColor.PERSONAL)
                .append(Component.text((int) distance, ExTextColor.VALUE))
                .append(Component.text(" blocks", ExTextColor.PERSONAL)));
    }
}
