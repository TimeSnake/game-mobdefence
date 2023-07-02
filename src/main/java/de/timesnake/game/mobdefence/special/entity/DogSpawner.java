/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.entity.WolfBuilder;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

import java.util.ArrayList;
import java.util.List;

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
  public List<Entity> getEntities(User user,
                                  ExItemStack item) {

    int dogs = 0;
    for (org.bukkit.entity.Entity wolf : user.getWorld().getEntitiesByClasses(org.bukkit.entity.Wolf.class)) {
      if (wolf instanceof org.bukkit.entity.Wolf && ((org.bukkit.entity.Wolf) wolf).getOwnerUniqueId() != null
          && ((org.bukkit.entity.Wolf) wolf).getOwnerUniqueId().equals(user.getUniqueId())) {
        dogs++;
      }
    }

    if (dogs >= MAX) {
      user.sendActionBarText(Component.text("Too many are alive", ExTextColor.WARNING));
      return new ArrayList<>();
    }

    int amount = AMOUNT_LEVELS.getNumberFromLore(item, Integer::valueOf);
    float health = 2 * HEALTH_LEVELS.getNumberFromLore(item, Integer::valueOf);

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      entities.add(this.getDog(user, health));
    }

    return entities;
  }

  private Wolf getDog(User user, float health) {
    return new WolfBuilder(user.getExWorld().getHandle(), false, false)
        .applyOnEntity(e -> {
          e.setTame(true);
          e.setOwnerUUID(user.getUniqueId());
          e.setOrderedToSit(false);
          e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);
        })
        .setMaxHealthAndHealth(health)
        .addPathfinderGoal(1, e -> new FloatGoal(e))
        .addPathfinderGoal(4, e -> new LeapAtTargetGoal(e, 0.4F))
        .addPathfinderGoal(5, e -> new MeleeAttackGoal(e, 1.0, false))
        .addPathfinderGoal(6, e -> new FollowOwnerGoal(e, 1.2D, 10.0F, 2.0F, true))
        .addPathfinderGoal(8, e -> new RandomStrollGoal(e, 1.0D))
        .addPathfinderGoal(10, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(10, e -> new RandomLookAroundGoal(e))
        .addTargetGoal(1, e -> new OwnerHurtByTargetGoal(e))
        .addTargetGoal(2, e -> new OwnerHurtTargetGoal(e))
        .addTargetGoal(3, e -> new HurtByTargetGoal(e, MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)))
        .addTargetGoals(4, MobDefMob.ATTACKER_ENTITY_CLASSES.stream()
            .map(c -> e -> new NearestAttackableTargetGoal<>(e, c, true, false)))
        .build();
  }
}
