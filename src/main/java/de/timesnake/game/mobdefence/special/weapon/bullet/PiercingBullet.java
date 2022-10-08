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

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.weapon.WeaponTargetType;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.HashSet;

public abstract class PiercingBullet extends Bullet {

    protected Collection<LivingEntity> hitTargets = new HashSet<>();

    private int piercing;

    public PiercingBullet(User shooter, Location start, TargetFinder targetFinder, double speed, Double damage,
                          int piercing) {
        super(shooter, start, targetFinder, speed, damage);
        this.piercing = piercing;
    }

    public PiercingBullet(User shooter, Location start, TargetFinder targetFinder, double speed, Double damage,
                          int piercing, Collection<LivingEntity> hitTargets) {
        super(shooter, start, targetFinder, speed, damage);
        this.piercing = piercing;
        this.hitTargets = hitTargets;
    }

    @Override
    public void shootOnNextTarget(boolean first) {
        LivingEntity previousTarget = this.target;
        super.shootOnNextTarget(first);
        Server.runTaskLaterSynchrony(() -> this.hitTargets.remove(previousTarget), 20, GameMobDefence.getPlugin());
    }

    @Override
    protected LivingEntity nextTarget() {
        LivingEntity nextTarget = this.targetFinder.nextTarget(this.hitTargets, this.entity.getLocation());
        if (nextTarget != null) this.hitTargets.add(nextTarget);
        return nextTarget;
    }

    @Override
    public WeaponTargetType onHit(LivingEntity entity) {
        super.onHit(entity);
        return (this.piercing--) <= 0 ? WeaponTargetType.DESTROY : WeaponTargetType.ENTITY;
    }
}
