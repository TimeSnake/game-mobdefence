/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.game.mobdefence.user.MobDefUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Level {

  protected Logger logger = LogManager.getLogger("mob-def.kit.level");

  protected final int level;
  protected final Price price;
  protected final String description;
  protected int unlockWave = 0;

  protected Level(int level, int unlockWave, Price price, String description) {
    this(level, price, description);
    this.unlockWave = unlockWave;
  }

  protected Level(int level, Price price, String description) {
    this.level = level;
    this.price = price;
    this.description = description;
  }

  public int getLevel() {
    return level;
  }

  public Price getPrice() {
    return price;
  }

  public String getDescription() {
    return description;
  }

  public int getUnlockWave() {
    return unlockWave;
  }

  public abstract void run(MobDefUser user);

}
