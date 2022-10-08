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

package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.map.MobDefMap;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeightMapManager implements Listener {

    private final HashMap<MapType, HeightMap> mapsByType = new HashMap<>();
    private BukkitTask heightMapUpdaterTask;

    public HeightMapManager(ExLocation core) {
        for (MapType type : MapType.values()) {
            HeightMap heightMap = new HeightMap(core, type.getCheckGroups());
            this.mapsByType.put(type, heightMap);
            heightMap.update();
        }
    }

    public void startHeightMapUpdater() {
        if (this.heightMapUpdaterTask == null) {
            this.heightMapUpdaterTask = Server.runTaskTimerAsynchrony(this::updateMaps, 0,
                    MobDefMap.HEIGHT_MAP_UPDATE_DELAY, GameMobDefence.getPlugin());
        }
    }

    public HashMap<MapType, HeightMap> getMapsByType() {
        return mapsByType;
    }

    public HeightMap getDefaultMap() {
        return this.mapsByType.get(MapType.NORMAL);
    }

    public HeightMap getMap(MapType type) {
        return this.mapsByType.get(type);
    }

    public void stopHeightMapUpdater() {
        if (this.heightMapUpdaterTask != null) {
            this.heightMapUpdaterTask.cancel();
            this.heightMapUpdaterTask = null;
        }
    }

    public void resetMaps() {
        for (Map.Entry<MapType, HeightMap> entry : this.mapsByType.entrySet()) {
            entry.getValue().reset();
        }
    }

    public void updateMaps() {
        for (Map.Entry<MapType, HeightMap> entry : this.mapsByType.entrySet()) {
            entry.getValue().update();
        }
    }

    public enum MapType {
        NORMAL(List.of(BlockCheck.WALK_IN, new BlockCheck.HighBlockBreak(MobDefMob.BREAK_LEVEL,
                BlockCheck.HIGH_BREAKABLE, BlockCheck.NORMAL_BREAKABLE)), List.of(BlockCheck.ON_SOLID_1H),
                List.of(new BlockCheck.DiagonalBlocked(MobDefMob.BREAK_LEVEL, BlockCheck.HIGH_BREAKABLE,
                        BlockCheck.NORMAL_BREAKABLE)), List.of(new BlockCheck.FloorFenceWallBlocked())),

        /*SMALL(List.of(BlockCheck.WALK_IN_SMALL),
                List.of(BlockCheck.ON_SOLID_1H)),

        BREAKER(List.of(BlockCheck.WALK_IN, new BlockCheck.HardBreakable(2), new BlockCheck.HighBlockBreak(Mob
        .BREAK_LEVEL, BlockCheck.HIGH_BREAKABLE, BlockCheck.BREAKABLE)),
                List.of(BlockCheck.ON_SOLID_1H),
                List.of(new BlockCheck.DiagonalBlocked(BlockCheck.HIGH_BREAKABLE, BlockCheck.BREAKABLE),
                        new BlockCheck.FloorFenceWallBlocked(BlockCheck.HIGH_BREAKABLE))),


         */
        WALL_FINDER(List.of(BlockCheck.WALK_IN, BlockCheck.WALL), List.of(BlockCheck.ON_SOLID_1H),
                List.of(new BlockCheck.DiagonalBlocked(MobDefMob.BREAK_LEVEL)));

        private final List<List<BlockCheck>> checkGroups;

        MapType(List<BlockCheck>... checkGroups) {
            this.checkGroups = Arrays.asList(checkGroups);
        }

        public List<List<BlockCheck>> getCheckGroups() {
            return checkGroups;
        }
    }
}
