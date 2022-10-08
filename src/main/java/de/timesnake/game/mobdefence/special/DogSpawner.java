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

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.entities.entity.ExtendedCraftEntity;
import de.timesnake.library.entities.entity.bukkit.ExWolf;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.wrapper.EntityClass;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

import java.util.ArrayList;
import java.util.List;

public class DogSpawner extends EntitySpawner {

    public static final ItemLevelType<?> AMOUNT_LEVELS = new ItemLevelType<>("Amount",
            new ExItemStack(Material.WOLF_SPAWN_EGG), 1, 5, ItemLevel.getLoreNumberLevels("Amount", 1, 0, "Dogs", 2,
            List.of(new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(14, ShopCurrency.SILVER), new ShopPrice(24,
                    ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.GOLD)), "+1 Dog", List.of(5, 6, 7, 8)));
    public static final ItemLevelType<?> HEALTH_LEVELS = new ItemLevelType<>("Health",
            new ExItemStack(Material.RED_DYE), 1, 5, ItemLevel.getLoreNumberLevels("Health", 2, 1, "❤", 2,
            List.of(new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(32, ShopCurrency.BRONZE),
                    new ShopPrice(11, ShopCurrency.GOLD), new ShopPrice(22, ShopCurrency.SILVER)),
            "+2.5 ❤", List.of(20, 25, 30, 35)));
    private static final double BASE_HEALTH = 15;
    public static final LevelItem LEVEL_ITEM = new LevelItem("§6Call Dogs", new ExItemStack(Material.BONE, "§6Call " +
            "Dogs").setLore("", AMOUNT_LEVELS.getBaseLevelLore(4), HEALTH_LEVELS.getBaseLevelLore(BASE_HEALTH)), new ExItemStack(Material.BONE, "§6Call Dogs"),
            List.of(AMOUNT_LEVELS, HEALTH_LEVELS));

    private static final int MAX = 4;

    public DogSpawner() {
        super(LEVEL_ITEM.getItem(), 20 * 60);
    }

    @Override
    public List<? extends ExtendedCraftEntity<?>> getEntities(User user, ExItemStack item) {

        int dogs = 0;
        for (Entity wolf : user.getWorld().getEntitiesByClasses(Wolf.class)) {
            if (wolf instanceof Wolf && ((Wolf) wolf).getOwnerUniqueId() != null && ((Wolf) wolf).getOwnerUniqueId().equals(user.getUniqueId())) {
                dogs++;
            }
        }

        if (dogs >= MAX) {
            user.sendActionBarText(Component.text("Too many are alive", ExTextColor.WARNING));
            return new ArrayList<>();
        }

        int amount = Integer.parseInt(AMOUNT_LEVELS.getValueFromLore(item.getLore()));
        double health = 2 * Double.parseDouble(HEALTH_LEVELS.getValueFromLore(item.getLore()));

        List<ExtendedCraftEntity<?>> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(this.getDog(user, health));
        }

        return entities;
    }

    private ExWolf getDog(User user, double health) {
        ExWolf entity = new ExWolf(user.getExWorld().getBukkitWorld(), false);

        entity.setTamed(true);
        entity.setOwnerUUID(user.getUniqueId());
        entity.setWillSit(false);

        entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        entity.addPathfinderGoal(4, new ExPathfinderGoalLeapAtTarget(0.4F));
        entity.addPathfinderGoal(5, new ExPathfinderGoalMeleeAttack(1.0D));
        entity.addPathfinderGoal(6, new ExPathfinderGoalFollowOwner(1.0D, 10.0F, 2.0F, false));
        entity.addPathfinderGoal(8, new ExCustomPathfinderGoalRandomStrollLand(1.0D));
        entity.addPathfinderGoal(10, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        entity.addPathfinderGoal(10, new ExPathfinderGoalRandomLookaround());

        entity.addPathfinderGoal(1, new ExPathfinderGoalOwnerHurtByTarget());
        entity.addPathfinderGoal(2, new ExPathfinderGoalOwnerHurtTarget());

        entity.addPathfinderGoal(3, new ExCustomPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));
        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            entity.addPathfinderGoal(4, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, 10, true,
                    false));
        }


        entity.setMaxHealth(health);
        entity.setHealth(health);

        entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);

        return entity;
    }
}
