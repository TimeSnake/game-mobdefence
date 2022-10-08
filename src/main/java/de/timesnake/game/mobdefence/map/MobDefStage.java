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

package de.timesnake.game.mobdefence.map;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MobDefStage {

    private final Integer number;

    private final HashMap<Integer, MobSpawn> spawnsByIndex = new HashMap<>();
    private int chanceSum;

    private final HeightMapManager heightMapManager;

    private final Random random = new Random();

    public MobDefStage(Integer number, ExLocation coreLocation, Map<Integer, ExLocation> mapLocs) {
        this.heightMapManager = new HeightMapManager(coreLocation);
        this.number = number;

        int stageIndex = number * MobDefMap.STAGE_LOC_SIZE;

        int i = 0;
        for (int index = stageIndex + MobDefMap.MOB_SPAWN_START_INDEX; index < (number + 1) * MobDefMap.STAGE_LOC_SIZE; index++) {

            int priority = (index - stageIndex) / 10;

            ExLocation loc = mapLocs.get(index);

            if (loc == null) {
                continue;
            }

            this.spawnsByIndex.put(i, new MobSpawn(priority, loc));
            Server.printText(Plugin.MOB_DEFENCE, "Loaded spawn " + index + " with index " + i);

            this.chanceSum += 10 - priority;

            i++;
        }
    }

    public Integer getNumber() {
        return this.number;
    }

    public HeightMapManager getHeightMapManager() {
        return heightMapManager;
    }

    public MobSpawn getRandomMobPath() {
        int random = this.random.nextInt(this.chanceSum);

        Iterator<Map.Entry<Integer, MobSpawn>> spawnsIt = this.spawnsByIndex.entrySet().iterator();
        java.util.Map.Entry<Integer, MobSpawn> entry;

        do {
            entry = spawnsIt.next();
            random -= 10 - entry.getValue().getPriority();
        } while (random > 0);

        return entry.getValue();
    }

}
