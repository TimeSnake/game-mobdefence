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
