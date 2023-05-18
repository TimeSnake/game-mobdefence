/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;

public abstract class SpecialWeapon {

  protected final ExItemStack item;

  public SpecialWeapon(ExItemStack item) {
    this.item = item;
  }

  public ExItemStack getItem() {
    return item;
  }
}
