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

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class Bow extends SpecialWeapon implements UserInventoryInteractListener, Listener {

    private static final ExItemStack BOW_ITEM = new ExItemStack(Material.BOW)
            .addExEnchantment(Enchantment.ARROW_INFINITE, 1)
            .unbreakable()
            .immutable();

    private static final LevelType.Builder POWER = new LevelType.Builder()
            .name("Power")
            .display(new ExItemStack(Material.RED_DYE))
            .baseLevel(0)
            .levelDescription("+1 Power")
            .levelEnchantment(Enchantment.ARROW_DAMAGE)
            .levelItem(BOW_ITEM)
            .addEnchantmentLvl(new Price(9, Currency.BRONZE), 1)
            .addEnchantmentLvl(new Price(12, Currency.SILVER), 2)
            .addEnchantmentLvl(new Price(8, Currency.GOLD), 3)
            .addEnchantmentLvl(new Price(27, Currency.SILVER), 4)
            .addEnchantmentLvl(new Price(14, Currency.GOLD), 5)
            .addEnchantmentLvl(new Price(48, Currency.BRONZE), 6)
            .addEnchantmentLvl(new Price(43, Currency.SILVER), 7);

    private static final LevelType.Builder FLAME = new LevelType.Builder()
            .name("Flame")
            .display(new ExItemStack(Material.BLAZE_POWDER))
            .baseLevel(0)
            .levelDescription("Flame Arrows")
            .levelEnchantment(Enchantment.ARROW_FIRE)
            .levelItem(BOW_ITEM)
            .addEnchantmentLvl(new Price(8, Currency.GOLD), 3);

    private static final LevelType.Builder PIERCING = new LevelType.Builder()
            .name("Piercing")
            .display(new ExItemStack(Material.ZOMBIE_HEAD))
            .baseLevel(0)
            .levelDescription("+1 Mob")
            .levelLoreName("Piercing")
            .levelUnit("mobs")
            .levelDecimalDigit(0)
            .levelLoreLine(1)
            .levelItem(BOW_ITEM)
            .addLoreLvl(new Price(16, Currency.BRONZE), 1)
            .addLoreLvl(new Price(16, Currency.SILVER), 2)
            .addLoreLvl(new Price(32, Currency.BRONZE), 3)
            .addLoreLvl(new Price(48, Currency.BRONZE), 4)
            .addLoreLvl(new Price(64, Currency.BRONZE), 5);

    public static final UpgradeableItem.Builder BOW = new UpgradeableItem.Builder()
            .name("Bow")
            .display(BOW_ITEM.cloneWithId())
            .baseItem(BOW_ITEM.cloneWithId())
            .addLvlType(POWER)
            .addLvlType(FLAME)
            .addLvlType(PIERCING);

    private final Set<User> cooldownUser = new HashSet<>();

    public Bow() {
        super(BOW.getBaseItem());
        //Server.getInventoryEventManager().addInteractListener(this, BOW.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }

        if (!event.getClickedItem().equals(BOW.getBaseItem())) {
            return;
        }

        User user = event.getUser();

        event.setCancelled(true);

        if (this.cooldownUser.contains(user)) {
            return;
        }

        if (!user.getPlayer().getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1)) {
            return;
        }

        user.getPlayer().getInventory().removeItem(new ItemStack(Material.ARROW, 1));

        Arrow arrow = user.getPlayer().getWorld().spawnArrow(user.getPlayer().getEyeLocation().add(0, -0.2, 0),
                user.getPlayer().getLocation().getDirection(),
                event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) + 2, 1);

        if (event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0) {
            arrow.setFireTicks(Integer.MAX_VALUE);
        }

        arrow.setShooter(user.getPlayer());
        arrow.setDamage(event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) + 1.3f);

        this.cooldownUser.add(user);

        Server.runTaskLaterSynchrony(() -> {
            this.cooldownUser.remove(user);
        }, 14, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onPlayerLaunchProjectile(PlayerLaunchProjectileEvent e) {
        ExItemStack bow = ExItemStack.getItem(e.getItemStack(), false);

        if (!BOW.getBaseItem().equals(bow)) {
            return;
        }

        Arrow arrow = ((Arrow) e.getProjectile());

        int piercing = PIERCING.getNumberFromLore(bow, Integer::valueOf);

        arrow.setPierceLevel(piercing);

    }
}
