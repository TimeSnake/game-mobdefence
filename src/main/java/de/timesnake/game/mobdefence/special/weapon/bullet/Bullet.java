package de.timesnake.game.mobdefence.special.weapon.bullet;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.weapon.WeaponTargetType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class Bullet implements Listener {

    protected final TargetFinder targetFinder;
    protected final double speed;
    protected final Double damage;
    protected User shooter;
    protected Entity entity;
    protected LivingEntity target;

    protected BukkitTask entityFollowTask;

    public Bullet(User shooter, Location start, TargetFinder targetFinder, double speed, Double damage) {
        this.shooter = shooter;
        this.targetFinder = targetFinder;
        this.speed = speed;
        this.damage = damage;

        this.entity = this.spawn(start);
    }

    public abstract Entity spawn(Location location);

    public void shootOnNextTarget(boolean first) {
        if (this.targetFinder.equals(TargetFinder.STRAIGHT)) {
            return;
        }

        if (first) {
            this.target = this.getFirstTarget();
        } else {
            this.target = this.nextTarget();
        }


        if (this.target == null) {
            this.remove();
            return;
        }


        this.entity = this.spawn(this.entity.getLocation());

        this.entityFollowTask = Server.runTaskTimerSynchrony(() -> {
            Vector vec =
                    this.target.getLocation().add(0, 1, 0).toVector().subtract(this.entity.getLocation().toVector()).normalize();
            this.entity.setVelocity(vec.multiply(this.speed));
        }, 0, 2, GameMobDefence.getPlugin());
    }

    protected LivingEntity getFirstTarget() {
        return this.nextTarget();
    }

    protected LivingEntity nextTarget() {
        return this.targetFinder.nextTarget(List.of(this.target), this.entity.getLocation());
    }

    public void remove() {
        if (this.entityFollowTask != null) {
            this.entityFollowTask.cancel();
        }

        this.entity.remove();
        MobDefServer.getWeaponManager().getBulletManager().removeBullet(this);
    }

    public WeaponTargetType onHit(LivingEntity entity) {
        if (this.entityFollowTask != null) {
            this.entityFollowTask.cancel();
        }

        if (this.damage != null) {
            entity.damage(this.damage);
        }

        return WeaponTargetType.DESTROY;
    }

    public Entity getEntity() {
        return entity;
    }

    public void returnToSender() {

    }
}
