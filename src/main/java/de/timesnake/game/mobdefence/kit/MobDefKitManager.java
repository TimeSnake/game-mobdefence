/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.loungebridge.util.user.KitManager;
import java.util.Collection;
import java.util.List;

public class MobDefKitManager extends KitManager<MobDefKit> {

  public static final List<MobDefKit> KITS = List.of(MobDefKit.KNIGHT, MobDefKit.ARCHER,
      MobDefKit.ALCHEMIST, MobDefKit.WIZARD, MobDefKit.LUMBERJACK);

  public MobDefKitManager() {
    super(false);
  }

  @Override
  public Collection<MobDefKit> getKits() {
    return KITS;
  }
}
