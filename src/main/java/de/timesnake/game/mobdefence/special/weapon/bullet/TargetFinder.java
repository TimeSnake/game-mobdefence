/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon.bullet;

import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;

@FunctionalInterface
public interface TargetFinder {

  TargetFinder STRAIGHT = (excludedTargets, location) -> null;
  TargetFinder NEAREST_ATTACKER =
      (excludedTargets, location) -> (LivingEntity) location.getWorld()
          .getNearbyEntities(location, 16, 16, 16,
              e -> MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getType())
                   && !excludedTargets.contains(e)).stream().findFirst()
          .orElse(null);

  LivingEntity nextTarget(Collection<LivingEntity> excludedTargets, Location location);
}
