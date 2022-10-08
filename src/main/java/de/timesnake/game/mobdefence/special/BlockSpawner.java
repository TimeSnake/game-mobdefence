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

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.weapon.SpecialWeapon;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public abstract class BlockSpawner extends SpecialWeapon implements Listener {

    protected final EntityType entityType;

    protected final int loreLine;


    public BlockSpawner(EntityType type, ExItemStack item, int loreLine) {
        super(item);
        this.entityType = type;
        this.loreLine = loreLine;
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    public int getLeftEntities(ExItemStack item) {
        return this.getAmountFromString(item.getLore().get(this.loreLine));
    }

    public void updateItem(ExItemStack item, int left) {
        List<String> lore = item.getLore();
        lore.add(this.loreLine, this.parseAmountToString(left));
    }

    public abstract void spawnEntities(Location location);

    public abstract int getAmountFromString(String s);

    public abstract String parseAmountToString(int amount);

    @EventHandler
    public void onBlockPlace(UserBlockPlaceEvent e) {
        User user = e.getUser();

        ExItemStack item = new ExItemStack(e.getItemInHand()).cloneWithId();

        if (!item.equals(this.item)) {
            return;
        }

        this.spawnEntities(e.getBlock().getLocation());

        int left = this.getLeftEntities(item) - 1;

        if (left > 0) {
            this.updateItem(item, left);
            user.getInventory().setItem(e.getHand(), item);
        } else {
            user.getInventory().setItem(e.getHand(), null);
        }

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        if (!(e.getEntity().getType().equals(this.entityType))) {
            return;
        }

        e.setCancelled(true);
        e.setDamage(0);

    }
}
