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

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class PoisonArrow extends SpecialWeapon implements UserInventoryInteractListener, Listener {

    public static final ExItemStack ITEM =
            ExItemStack.getPotion(Material.TIPPED_ARROW, 3, PotionType.POISON, false, false).setDisplayName("§6Poison Arrow");
    public static final ItemTrade TRADE = new ItemTrade(3, false, new ShopPrice(5, ShopCurrency.BRONZE),
            List.of(ITEM), ITEM);
    private static final String NAME = "poison_arrow";

    public PoisonArrow() {
        super(ITEM);
        Server.getInventoryEventManager().addInteractListener(this, ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        user.removeCertainItemStack(ITEM.cloneWithId().asOne());

        Arrow arrow = user.getPlayer().getWorld().spawnArrow(user.getPlayer().getEyeLocation().add(0, -0.2, 0),
                user.getPlayer().getLocation().getDirection(), 2, 1);

        arrow.setCustomName(NAME);
        arrow.setCustomNameVisible(false);
        arrow.setShooter(user.getPlayer());
        arrow.setDamage(3);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) e.getEntity();

        if (arrow.getCustomName() != null && arrow.getCustomName().equals(NAME)) {
            arrow.getWorld().playEffect(arrow.getLocation(), Effect.POTION_BREAK, new Potion(PotionType.POISON));
            for (Entity entity : arrow.getNearbyEntities(3, 3, 3)) {
                if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                    ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 1));
                }
            }
        }
    }
}
