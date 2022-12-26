/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.special.trap;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;

public abstract class MultipleRangedTrap extends RangedTrap {

    protected int uses;

    public MultipleRangedTrap(ExBlock block, double range, int mobAmount, int uses) {
        super(block, range, mobAmount);
        this.uses = uses;
    }

    public int getUses() {
        return uses;
    }

    @Override
    public boolean trigger(Collection<LivingEntity> entities) {
        if (this.uses <= 0) {
            return super.trigger(entities);
        }
        this.uses--;
        return true;
    }
}
