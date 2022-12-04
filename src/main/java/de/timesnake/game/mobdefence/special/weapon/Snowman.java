/*
 * workspace.game-mobdefence.main
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

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Trade;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExSnowman;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snowman extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.CARVED_PUMPKIN, "§6 4 Snowmen", "§7Place the " +
            "block to spawn a snowman", "§c4 Snowmen").immutable();

    public static final Trade.Builder SNOWMAN = new Trade.Builder()
            .price(new Price(8, Currency.GOLD))
            .giveItems(Snowman.ITEM);

    public Snowman() {
        super(EntityType.SNOWMAN, ITEM, 1);
    }

    @Override
    public int getAmountFromString(String s) {
        return Integer.parseInt(s.replace("§c", "").replace(" Snowmen", ""));
    }

    @Override
    public String parseAmountToString(int amount) {
        return "§c" + amount + " Snowmen";
    }

    @Override
    public void spawnEntities(Location location) {
        ExSnowman snowman = new ExSnowman(location.getWorld(), false, false);
        snowman.setPosition(location.getX(), location.getY(), location.getZ());
        snowman.setSlot(ExEnumItemSlot.HEAD, null);

        snowman.addPathfinderGoal(1, new ExPathfinderGoalArrowAttack(0D, 1, 10.0F));
        snowman.addPathfinderGoal(3, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 6.0F));
        snowman.addPathfinderGoal(4, new ExPathfinderGoalRandomStrollLand(0, 0));
        snowman.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        snowman.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)));

        for (Class<? extends de.timesnake.library.entities.entity.extension.LivingEntity> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            snowman.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, false));
        }

        snowman.setPersistent(true);

        snowman.setMaxHealth(40);
        snowman.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), snowman);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            e.setCancelled(true);
            return;
        }

        if (!(snowball.getShooter() instanceof org.bukkit.entity.Snowman)) {
            return;
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
        entity.damage(1, snowball);
    }
}
