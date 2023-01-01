/*
 * Copyright (C) 2023 timesnake
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
