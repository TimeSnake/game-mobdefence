/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import org.bukkit.Material;

public enum Currency {

  BRONZE("Bronze", new ExItemStack(Material.BRICK, "§6Bronze")),
  SILVER("Silver", new ExItemStack(Material.IRON_INGOT, "§6Silver")),
  GOLD("Gold", new ExItemStack(Material.GOLD_INGOT, "§6Gold")),
  EMERALD("Emerald", new ExItemStack(Material.EMERALD, "§6Emerald"));

  private final String name;
  private final ExItemStack item;

  Currency(String name, ExItemStack item) {
    this.name = name;
    this.item = item;
  }

  public String getName() {
    return name;
  }

  public ExItemStack getItem() {
    return item;
  }


}
