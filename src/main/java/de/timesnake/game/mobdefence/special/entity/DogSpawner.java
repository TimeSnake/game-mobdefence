/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.entity.bukkit.ExWolf;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.LivingEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFollowOwner;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLeapAtTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMeleeAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalOwnerHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalOwnerHurtTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

public class DogSpawner extends EntitySpawner {

    public static final LevelType.Builder AMOUNT_LEVELS = new LevelType.Builder()
            .name("Amount")
            .display(new ExItemStack(Material.WOLF_SPAWN_EGG))
            .baseLevel(1)
            .levelDescription("+1 Dog")
            .levelLoreLine(1)
            .levelLoreName("Dogs")
            .addLoreLvl(null, 4)
            .addLoreLvl(new Price(12, Currency.BRONZE), 5)
            .addLoreLvl(new Price(14, Currency.SILVER), 6)
            .addLoreLvl(new Price(24, Currency.BRONZE), 7)
            .addLoreLvl(new Price(16, Currency.GOLD), 8);

    public static final LevelType.Builder HEALTH_LEVELS = new LevelType.Builder()
            .name("Health")
            .display(new ExItemStack(Material.RED_DYE))
            .baseLevel(1)
            .levelDescription("+2.5 ❤")
            .levelLoreLine(2)
            .levelLoreName("Health")
            .addLoreLvl(null, 15)
            .addLoreLvl(new Price(16, Currency.BRONZE), 20)
            .addLoreLvl(new Price(32, Currency.BRONZE), 25)
            .addLoreLvl(new Price(11, Currency.GOLD), 30)
            .addLoreLvl(new Price(22, Currency.SILVER), 35);

    public static final UpgradeableItem.Builder LEVEL_ITEM = new UpgradeableItem.Builder()
            .name("Call Dogs")
            .display(new ExItemStack(Material.BONE, "§6Dogs"))
            .baseItem(new ExItemStack(Material.BONE, "§6Call Dogs"))
            .addLvlType(AMOUNT_LEVELS)
            .addLvlType(HEALTH_LEVELS);

    private static final int MAX = 4;

    public DogSpawner() {
        super(LEVEL_ITEM.getBaseItem(), 20 * 60);
    }

    @Override
    public List<de.timesnake.library.entities.entity.extension.Entity> getEntities(User user,
            ExItemStack item) {

        int dogs = 0;
        for (Entity wolf : user.getWorld().getEntitiesByClasses(Wolf.class)) {
            if (wolf instanceof Wolf && ((Wolf) wolf).getOwnerUniqueId() != null
                    && ((Wolf) wolf).getOwnerUniqueId().equals(user.getUniqueId())) {
                dogs++;
            }
        }

        if (dogs >= MAX) {
            user.sendActionBarText(Component.text("Too many are alive", ExTextColor.WARNING));
            return new ArrayList<>();
        }

        int amount = AMOUNT_LEVELS.getNumberFromLore(item, Integer::valueOf);
        double health = 2 * HEALTH_LEVELS.getNumberFromLore(item, Double::valueOf);

        List<de.timesnake.library.entities.entity.extension.Entity> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(this.getDog(user, health));
        }

        return entities;
    }

    private ExWolf getDog(User user, double health) {
        ExWolf entity = new ExWolf(user.getExWorld().getBukkitWorld(), false, false);

        entity.setTamed(true);
        entity.setOwnerUUID(user.getUniqueId());
        entity.setWillSit(false);

        entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        entity.addPathfinderGoal(4, new ExPathfinderGoalLeapAtTarget(0.4F));
        entity.addPathfinderGoal(5, new ExPathfinderGoalMeleeAttack(1.0D));
        entity.addPathfinderGoal(6, new ExPathfinderGoalFollowOwner(1.0D, 10.0F, 2.0F, false));
        entity.addPathfinderGoal(8, new ExPathfinderGoalRandomStrollLand(1.0D));
        entity.addPathfinderGoal(10, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        entity.addPathfinderGoal(10, new ExPathfinderGoalRandomLookaround());

        entity.addPathfinderGoal(1, new ExPathfinderGoalOwnerHurtByTarget());
        entity.addPathfinderGoal(2, new ExPathfinderGoalOwnerHurtTarget());

        entity.addPathfinderGoal(3,
                new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)));
        for (Class<? extends LivingEntity> entityClass : MobDefMob.ATTACKER_ENTITY_CLASSES) {
            entity.addPathfinderGoal(4,
                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, 10, true,
                            false));
        }

        entity.setMaxHealth(health);
        entity.setHealth(health);

        entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);

        return entity;
    }
}
