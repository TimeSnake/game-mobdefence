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

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class SplashBow extends SpecialWeapon implements Listener {

    private static final ExItemStack ITEM = new ExItemStack(Material.BOW).setDamage(Material.BOW.getMaxDurability() - 24)
            .addExEnchantment(Enchantment.ARROW_INFINITE, 1)
            .setDisplayName("§6Splash Bow").immutable();

    private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
            .name("Damage")
            .display(new ExItemStack(Material.RED_DYE))
            .baseLevel(1)
            .levelDescription("+0.5 ❤")
            .levelUnit("❤")
            .levelDecimalDigit(0)
            .levelLoreLine(1)
            .levelLoreName("Damage")
            .levelItem(ITEM)
            .addLoreLvl(null, 2)
            .addLoreLvl(new Price(8, Currency.BRONZE), 2.5)
            .addLoreLvl(new Price(8, Currency.SILVER), 3)
            .addLoreLvl(new Price(24, Currency.BRONZE), 3.5)
            .addLoreLvl(new Price(25, Currency.SILVER), 4)
            .addLoreLvl(new Price(7, Currency.GOLD), 4.5)
            .addLoreLvl(new Price(14, Currency.GOLD), 5);


    private static final LevelType.Builder RADIUS_LEVELS = new LevelType.Builder()
            .name("Radius")
            .display(new ExItemStack(Material.TARGET))
            .baseLevel(1)
            .levelDescription("+1 Block")
            .levelDecimalDigit(1)
            .levelUnit("blocks")
            .levelLoreLine(2)
            .levelLoreName("Radius")
            .levelItem(ITEM)
            .addLoreLvl(null, 2)
            .addLoreLvl(new Price(12, Currency.SILVER), 3)
            .addLoreLvl(new Price(8, Currency.GOLD), 4);

    public static final UpgradeableItem.Builder BOW = new UpgradeableItem.Builder()
            .name("Splash Bow")
            .price(new Price(6, Currency.GOLD))
            .display(ITEM.cloneWithId())
            .baseItem(ITEM.cloneWithId())
            .addLvlType(DAMAGE_LEVELS)
            .addLvlType(RADIUS_LEVELS);

    private static final String ARROW_NAME = "splashArrow";
    private static final String RADIUS_NAME = "radius";

    public SplashBow() {
        super(ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow arrow)) {
            return;
        }

        if (!(e.getEntity().getShooter() instanceof Player)) {
            return;
        }

        arrow.remove();

        if (arrow.getCustomName() == null) {
            return;
        }

        if (!arrow.getCustomName().contains(ARROW_NAME)) {
            return;
        }

        String[] nameParts = arrow.getCustomName().split(RADIUS_NAME);

        double damage = Double.parseDouble(nameParts[0].replaceAll(ARROW_NAME, ""));
        int radius = Integer.parseInt(nameParts[1]);

        arrow.getWorld().createExplosion(arrow, radius, false, false);

    }


    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (!(e.getProjectile() instanceof Arrow arrow)) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        User user = Server.getUser(((Player) e.getEntity()));
        ExItemStack item = new ExItemStack(e.getBow());

        if (!item.equals(BOW.getBaseItem())) {
            return;
        }

        e.setConsumeItem(false);

        double damage = DAMAGE_LEVELS.getNumberFromLore(item, Double::valueOf);
        int radius = RADIUS_LEVELS.getNumberFromLore(item, Integer::valueOf);

        arrow.setCustomName(ARROW_NAME + damage + RADIUS_NAME + radius);
        arrow.setCustomNameVisible(false);

        Server.runTaskLaterSynchrony(() -> user.addItem(e.getConsumable()), 1, GameMobDefence.getPlugin());
    }
}
