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
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.weapon.bullet.BulletManager;
import de.timesnake.game.mobdefence.special.weapon.bullet.PiercingBullet;
import de.timesnake.game.mobdefence.special.weapon.bullet.TargetFinder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RocketCrossBow extends SpecialWeapon implements Listener {

    private static final ItemLevelType<?> MULTI_SHOT_LEVELS = new ItemLevelType<>("Multi Shot",
            new ExItemStack(Material.SOUL_TORCH), 1, 8, ItemLevel.getLoreNumberLevels("Multi Shot", 1, 0, "arrows", 1
            , List.of(new ShopPrice(8, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(6,
                            ShopCurrency.SILVER), new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(4,
                            ShopCurrency.GOLD)
                    , new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(16,
                            ShopCurrency.SILVER)), "+2 Arrow", List.of(3, 5, 7, 9, 11, 13, 15, 17)));
    private static final ItemLevelType<?> DAMAGE = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1
            , 5, ItemLevel.getLoreNumberLevels("Damage", 2, 0, "❤", 2, List.of(new ShopPrice(24, ShopCurrency.BRONZE)
            , new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.GOLD), new ShopPrice(48,
                    ShopCurrency.SILVER)), "+1 ❤", List.of(6, 7, 8, 9)));
    public static final LevelItem CROSSBOW = new LevelItem("Rocket Crossbow", true, new ShopPrice(16,
            ShopCurrency.SILVER),
            new ExItemStack(Material.CROSSBOW).enchant().unbreakable().setDisplayName("§6Rocket Crossbow").setLore("",
                    MULTI_SHOT_LEVELS.getBaseLevelLore(1), DAMAGE.getBaseLevelLore(5), "§cAim on block or entity to " +
                            "target nearby entities"),
            new ExItemStack(Material.CROSSBOW).addExEnchantment(Enchantment.ARROW_INFINITE, 1),
            List.of(MULTI_SHOT_LEVELS, DAMAGE));
    private static final ItemLevelType<?> PIERCING_LEVELS_LEVELS = new ItemLevelType<>("Piercing",
            new ExItemStack(Material.TORCH), 0, 5, ItemLevel.getLoreNumberLevels("Piercing", 3, 0, "mobs", 1,
            List.of(new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(11, ShopCurrency.GOLD), new ShopPrice(32,
                    ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(24,
                    ShopCurrency.GOLD)), "+1 mob", List.of(1, 2, 3, 4, 5)));

    public RocketCrossBow() {
        super(CROSSBOW.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (!(e.getProjectile() instanceof Arrow)) {
            return;
        }

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        User user = Server.getUser((Player) e.getEntity());
        ExItemStack item = new ExItemStack(e.getBow());

        if (!item.equals(CROSSBOW.getItem())) {
            return;
        }

        ItemStack arrow = e.getConsumable();

        e.setCancelled(true);
        e.setConsumeItem(false);


        int multiShot = Integer.parseInt(MULTI_SHOT_LEVELS.getValueFromLore(item.getLore()));
        int damage = Integer.parseInt(DAMAGE.getValueFromLore(item.getLore()));
        // int piercing = Integer.parseInt(PIERCING_LEVELS_LEVELS.getValueFromLore(item.getLore()));

        BulletManager bulletManager = MobDefServer.getWeaponManager().getBulletManager();

        Collection<LivingEntity> hitTargets = new HashSet<>();

        Server.runTaskTimerSynchrony((time) ->
                        bulletManager.shootBullet(new FollowerArrow(user, 0.9, damage * 2D, 0, hitTargets),
                                user.getLocation().getDirection().normalize()), multiShot, true, 0, 4,
                GameMobDefence.getPlugin());

        Server.runTaskLaterSynchrony(() -> user.addItem(arrow), 1, GameMobDefence.getPlugin());
    }

    private static class FollowerArrow extends PiercingBullet {

        public FollowerArrow(User user, double speed, Double damage, int piercing,
                             Collection<LivingEntity> hitTargets) {
            super(user, user.getEyeLocation().add(0, -0.5, 0), TargetFinder.NEAREST_ATTACKER, speed, damage, piercing
                    , hitTargets);
        }

        @Override
        public Entity spawn(Location location) {
            Arrow arrow = location.getWorld().spawnArrow(location, new Vector(), 1F, 0.2F);
            arrow.setShooter(this.shooter.getPlayer());
            arrow.setGravity(false);
            arrow.setInvulnerable(true);
            return arrow;
        }

        @Override
        public LivingEntity getFirstTarget() {
            Entity entity = this.shooter.getTargetEntity(100);
            Location loc;
            if (entity != null) {
                loc = entity.getLocation();
            } else {
                Block block = this.shooter.getTargetBlock(100);
                if (block != null) {
                    loc = block.getLocation();
                } else {
                    loc = this.shooter.getLocation();
                }
            }

            return TargetFinder.NEAREST_ATTACKER.nextTarget(this.hitTargets, loc);
        }

        @Override
        public WeaponTargetType onHit(LivingEntity entity) {
            return super.onHit(entity);
        }
    }
}
