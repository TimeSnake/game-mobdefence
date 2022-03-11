package de.timesnake.game.mobdefence.special.weapon.bullet;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.special.weapon.WeaponTargetType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BulletManager implements Listener {

    private final HashMap<Entity, Bullet> bulletByEntity = new HashMap<>();

    public BulletManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    public void shootBullet(Bullet bullet, Vector velocity) {
        bullet.getEntity().setPersistent(true);

        if (velocity != null) {
            bullet.getEntity().setVelocity(velocity);
        }

        this.bulletByEntity.put(bullet.getEntity(), bullet);
        bullet.shootOnNextTarget(true);
    }

    public void removeBullet(Bullet bullet) {
        this.bulletByEntity.remove(bullet.getEntity());
    }

    @EventHandler
    public void onBulletHit(ProjectileHitEvent e) {
        Bullet bullet = this.bulletByEntity.get(e.getEntity());

        if (bullet == null) {
            return;
        }

        if (e.getHitBlock() != null) {
            e.setCancelled(true);
            bullet.remove();
            return;
        }

        if (e.getHitEntity() == null || !MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getHitEntity().getType())) {
            e.setCancelled(true);
            return;
        }

        LivingEntity entity = (LivingEntity) e.getHitEntity();

        e.setCancelled(true);

        WeaponTargetType targetType = bullet.onHit(entity);

        switch (targetType) {
            case DESTROY -> bullet.remove();
            case ENTITY -> bullet.shootOnNextTarget(false);
            case RETURN -> bullet.returnToSender();
        }
    }

}
