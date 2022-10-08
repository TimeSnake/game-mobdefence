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

package de.timesnake.game.mobdefence.special.trap;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import org.bukkit.event.Listener;

public abstract class RangedTrap extends Trap implements Listener {

    protected final double range;
    protected final int mobAmount;

    public RangedTrap(ExBlock block, double range, int mobAmount) {
        super(block);
        this.range = range;
        this.mobAmount = mobAmount;
    }

    public double getRange() {
        return range;
    }

    public int getMobAmount() {
        return mobAmount;
    }

}
