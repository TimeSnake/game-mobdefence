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

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDropManager implements Listener {

    private static final double EMERALD_CHANCE = 0.08;
    private static final double GOLD_CHANCE = 0.15;
    private static final double SILVER_CHANCE = 0.4;
    private static final double BRONZE_CHANCE = 0.6;

    public MobDropManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onItemDrop(EntityDeathEvent e) {
        e.setDroppedExp(0);
        e.getDrops().clear();

        if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
            if (e.getEntityType().equals(EntityType.ENDERMITE) || e.getEntityType().equals(EntityType.SILVERFISH)) {
                return;
            }

            if (Math.random() < EMERALD_CHANCE / MobDefServer.getPlayerAmount()) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.EMERALD).asItem());
            }
            if (Math.random() < GOLD_CHANCE) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.GOLD).asItem());
            }
            if (Math.random() < SILVER_CHANCE) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.SILVER).asItem());
            }
            if (Math.random() < BRONZE_CHANCE) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.BRONZE).asItem());
            }
        }
    }
}
