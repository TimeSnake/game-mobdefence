/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.trap;

import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

public abstract class Trap {

  protected final ExBlock block;

  public Trap(ExBlock block) {
    this.block = block;
  }

  public ExBlock getBlock() {
    return block;
  }

  public ExLocation getLocation() {
    return this.block.getLocation();
  }

  public boolean trigger(Collection<LivingEntity> entities) {
    this.block.getBlock().setType(Material.AIR);
    return true;
  }
}
