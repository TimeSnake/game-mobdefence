/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon.bullet;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.weapon.WeaponTargetType;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class PiercingBullet extends Bullet {

  protected Collection<LivingEntity> hitTargets = new HashSet<>();

  private int piercing;

  public PiercingBullet(User shooter, Location start, TargetFinder targetFinder, double speed,
      Double damage,
      int piercing) {
    super(shooter, start, targetFinder, speed, damage);
    this.piercing = piercing;
  }

  public PiercingBullet(User shooter, Location start, TargetFinder targetFinder, double speed,
      Double damage,
      int piercing, Collection<LivingEntity> hitTargets) {
    super(shooter, start, targetFinder, speed, damage);
    this.piercing = piercing;
    this.hitTargets = hitTargets;
  }

  @Override
  public void shootOnNextTarget(boolean first) {
    LivingEntity previousTarget = this.target;
    super.shootOnNextTarget(first);
    Server.runTaskLaterSynchrony(() -> this.hitTargets.remove(previousTarget), 20,
        GameMobDefence.getPlugin());
  }

  @Override
  protected LivingEntity nextTarget() {
    LivingEntity nextTarget = this.targetFinder.nextTarget(this.hitTargets,
        this.entity.getLocation());
    if (nextTarget != null) {
      this.hitTargets.add(nextTarget);
    }
    return nextTarget;
  }

  @Override
  public WeaponTargetType onHit(LivingEntity entity) {
    super.onHit(entity);
    return (this.piercing--) <= 0 ? WeaponTargetType.DESTROY : WeaponTargetType.ENTITY;
  }
}
