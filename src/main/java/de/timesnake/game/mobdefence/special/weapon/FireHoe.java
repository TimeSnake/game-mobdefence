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
import de.timesnake.basic.bukkit.util.user.event.EntityDamageByUserEvent;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FireHoe extends CooldownWeapon implements Listener {

    public static final double DAMAGE = 2.5;
    public static final int BURNING_TIME = 3; // in seconds
    public static final int SLOWNESS = 2;
    public static final int COOLDOWN = 7;
    public static final int FIRE_WIND_COOLDOWN = 40;

    private static final ExItemStack ITEM = new ExItemStack(Material.GOLDEN_HOE, "§6Frozen Fire Hoe")
            .immutable();

    private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
            .name("Damage")
            .display(new ExItemStack(Material.RED_DYE))
            .baseLevel(1)
            .levelDescription("+1 ❤")
            .levelUnit("❤")
            .levelDecimalDigit(0)
            .levelLoreLine(1)
            .levelLoreName("Damage")
            .levelItem(ITEM)
            .addLoreLvl(null, 2)
            .addLoreLvl(new Price(5, Currency.BRONZE), 3)
            .addLoreLvl(new Price(5, Currency.SILVER), 4)
            .addLoreLvl(new Price(5, Currency.GOLD), 5)
            .addLoreLvl(new Price(32, Currency.BRONZE), 6)
            .addLoreLvl(new Price(32, Currency.SILVER), 7);

    private static final LevelType.Builder BURNING_TIME_LEVELS = new LevelType.Builder()
            .name("Burning Time")
            .display(new ExItemStack(Material.BLAZE_POWDER))
            .baseLevel(1)
            .levelDescription("+1 Second")
            .levelUnit("s")
            .levelDecimalDigit(0)
            .levelLoreLine(2)
            .levelLoreName("Burning Time")
            .levelItem(ITEM)
            .addLoreLvl(null, 3)
            .addLoreLvl(new Price(5, Currency.BRONZE), 4)
            .addLoreLvl(new Price(4, Currency.SILVER), 5)
            .addLoreLvl(new Price(3, Currency.GOLD), 6)
            .addLoreLvl(new Price(12, Currency.BRONZE), 7)
            .addLoreLvl(new Price(14, Currency.SILVER), 8)
            .addLoreLvl(new Price(10, Currency.GOLD), 9);

    private static final LevelType.Builder SLOWNESS_LEVELS = new LevelType.Builder()
            .name("Slowness")
            .display(new ExItemStack(Material.FEATHER))
            .baseLevel(1)
            .levelDescription("+1 Second")
            .levelUnit("s")
            .levelDecimalDigit(0)
            .levelLoreLine(3)
            .levelLoreName("Slowness")
            .addLoreLvl(null, 3)
            .levelItem(ITEM)
            .addLoreLvl(new Price(24, Currency.BRONZE), 4)
            .addLoreLvl(new Price(48, Currency.BRONZE), 5);

    public static final UpgradeableItem.Builder FIRE_HOE = new UpgradeableItem.Builder()
            .name("Frozen Fire Hoe")
            .display(new ExItemStack(Material.GOLDEN_HOE, "§6Frozen Fire Hoe").enchant())
            .baseItem(ITEM.cloneWithId()
                    .enchant().setUnbreakable(true))
            .addLvlType(DAMAGE_LEVELS)
            .addLvlType(BURNING_TIME_LEVELS)
            .addLvlType(SLOWNESS_LEVELS);


    private final Set<User> cooldownUsers = new HashSet<>();

    public FireHoe() {
        super(FIRE_HOE.getBaseItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByUserEvent event) {
        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(event.getEntity().getType())) {
            return;
        }

        User user = event.getUser();

        ExItemStack hoe = ExItemStack.getItem(user.getInventory().getItemInMainHand(), false);

        if (hoe == null || !hoe.equals(FIRE_HOE.getBaseItem())) {
            return;
        }

        if (this.cooldownUsers.contains(user)) {
            return;
        }

        this.cooldownUsers.add(user);

        double damage = DAMAGE_LEVELS.getNumberFromLore(hoe, Double::valueOf);
        int burningTime = BURNING_TIME_LEVELS.getNumberFromLore(hoe, Integer::valueOf);
        int slowness = SLOWNESS_LEVELS.getNumberFromLore(hoe, Integer::valueOf);

        LivingEntity entity = (LivingEntity) event.getEntity();

        event.setDamage(damage * 2);

        entity.setFireTicks(burningTime * 20);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 3, slowness - 1));

        Server.runTaskLaterSynchrony(() -> cooldownUsers.remove(user), COOLDOWN, GameMobDefence.getPlugin());
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        Location loc =
                user.getLocation().add(0, 1.5, 0).add(user.getLocation().getDirection().normalize().multiply(0.3));
        World world = loc.getWorld();

        for (double angle = -Math.PI / 10; angle < Math.PI / 9; angle += Math.PI * 0.05) {
            Vector vec = loc.getDirection().clone().rotateAroundY(angle).normalize();
            double x = vec.getX();
            double z = vec.getZ();

            world.spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ(), 2, x, 0, z, 0, null);
        }

        for (LivingEntity entity : user.getLocation().getNearbyLivingEntities(5,
                e -> MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getType()))) {
            Vector vec = entity.getLocation().toVector().subtract(user.getLocation().toVector());
            float angle = vec.angle(new Vector(0, 0, 1));

            if (angle > -15 && angle < 20) {
                entity.damage(4);
                entity.setFireTicks(20 * 3);
            }
        }

    }

    @Override
    public int getCooldown(ExItemStack item) {
        return FIRE_WIND_COOLDOWN;
    }
}
