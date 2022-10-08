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

import de.timesnake.basic.bukkit.util.world.ExLocation;

public class HeightBlock {

    private int level;
    private final ExLocation location;
    private HeightBlock next;

    public HeightBlock(int level, ExLocation location, HeightBlock next) {
        this.level = level;
        this.location = location.getExBlock().getLocation();
        this.next = next;
    }

    public int getLevel() {
        return level;
    }

    public ExLocation getLocation() {
        return location;
    }

    public HeightBlock getNext() {
        return next;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    protected void setLevel(int level) {
        this.level = level;
    }

    protected void setNext(HeightBlock next) {
        this.next = next;
    }
}
