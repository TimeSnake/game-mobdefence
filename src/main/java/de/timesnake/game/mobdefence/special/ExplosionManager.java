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
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class ExplosionManager implements Listener {

    public static final List<Material> EXPLODEABLE = new ArrayList<>();

    static {
        EXPLODEABLE.addAll(BlockCheck.NORMAL_BREAKABLE_MATERIALS);
        EXPLODEABLE.addAll(BlockCheck.HIGH_BREAKABLE_MATERIALS);
        EXPLODEABLE.addAll(Tag.STONE_BRICKS.getValues());
        EXPLODEABLE.addAll(Tag.FENCES.getValues());
        EXPLODEABLE.addAll(Tag.WOODEN_TRAPDOORS.getValues());
        EXPLODEABLE.addAll(Tag.WOODEN_DOORS.getValues());
        EXPLODEABLE.addAll(Tag.WOODEN_SLABS.getValues());
        EXPLODEABLE.addAll(Tag.LOGS.getValues());
        EXPLODEABLE.addAll(Tag.PLANKS.getValues());
        EXPLODEABLE.addAll(Tag.WALLS.getValues());
    }

    public ExplosionManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType().equals(EntityType.CREEPER)) {
            e.setYield(0);
            e.blockList().removeIf(block -> block.getY() < e.getLocation().getY() || !EXPLODEABLE.contains(block.getType()));

            MobDefServer.getMap().getHeightMapManager().updateMaps();
        }
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent e) {
        Material type = e.getBlock().getType();

        if (!(BlockCheck.NORMAL_BREAKABLE.isTagged(type) || BlockCheck.HIGH_BREAKABLE.isTagged(type) || e.getBlock().isEmpty() || type.equals(Material.FIRE))) {
            e.setCancelled(true);
        }
    }
}
