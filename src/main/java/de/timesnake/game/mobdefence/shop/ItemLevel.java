/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.function.Function;

public abstract class ItemLevel extends Level implements Function<ExItemStack, ExItemStack> {

  private final ExItemStack targetItem;

  protected ItemLevel(ExItemStack targetItem, int level, Price price, String description) {
    super(level, price, description);
    this.targetItem = targetItem;
  }

  protected ItemLevel(ExItemStack targetItem, int level, int unlockWave, Price price, String description) {
    super(level, unlockWave, price, description);
    this.targetItem = targetItem;
  }

  @Override
  public void run(MobDefUser user) {
    ExItemStack userItem = user.getItem(this.targetItem);
    if (userItem == null) {
      this.logger.warn("Can not find item '{}' for user '{}'", this.targetItem.getType().name().toLowerCase(),
          user.getName());
      return;
    }
    ExItemStack leveledItem = this.apply(userItem);
    if (leveledItem == null) {
      this.logger.warn("Failed to apply level to item '{}' for user '{}'",
          this.targetItem.getType().name().toLowerCase(), user.getName());
      return;
    }
    user.replaceExItemStack(userItem, leveledItem);
  }
}