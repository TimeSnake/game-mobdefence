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

package de.timesnake.game.mobdefence.special.weapon.bullet;

import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;

@FunctionalInterface
public interface TargetFinder {

    TargetFinder STRAIGHT = (excludedTargets, location) -> null;
    TargetFinder NEAREST_ATTACKER =
            (excludedTargets, location) -> (LivingEntity) location.getWorld().getNearbyEntities(location, 16, 16, 16,
                    e -> MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getType()) && !excludedTargets.contains(e)).stream().findFirst().orElse(null);

    LivingEntity nextTarget(Collection<LivingEntity> excludedTargets, Location location);
}
