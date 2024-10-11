/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
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

  public static final ExItemStack ITEM = new ExItemStack(Material.BONE).setDisplayName("§6Call Dogs");

  public static final LevelableProperty.Builder AMOUNT_LEVELS = new LevelableProperty.Builder()
      .name("Amount")
      .display(new ExItemStack(Material.WOLF_SPAWN_EGG))
      .defaultLevel(1)
      .levelDescription("+1 Dog")
      .levelLoreLine(1)
      .levelLoreName("Dogs")
      .addTagLevel(null, 4)
      .addTagLevel(new Price(12, Currency.BRONZE), 5)
      .addTagLevel(new Price(14, Currency.SILVER), 6)
      .addTagLevel(new Price(24, Currency.BRONZE), 7)
      .addTagLevel(new Price(16, Currency.GOLD), 8);

  public static final LevelableProperty.Builder HEALTH_LEVELS = new LevelableProperty.Builder()
      .name("Health")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(1)
      .levelDescription("+2.5 ❤")
      .levelLoreLine(2)
      .levelLoreName("Health")
      .addTagLevel(null, 15)
      .addTagLevel(new Price(16, Currency.BRONZE), 20)
      .addTagLevel(new Price(32, Currency.BRONZE), 25)
      .addTagLevel(new Price(11, Currency.GOLD), 30)
      .addTagLevel(new Price(22, Currency.SILVER), 35);

  public static final UpgradeableGoodItem.Builder LEVEL_ITEM = new UpgradeableGoodItem.Builder()
      .name("Call Dogs")
      .display(new ExItemStack(Material.BONE).setDisplayName("§6Dogs"))
      .startItem(ITEM)
      .addLevelableProperty(AMOUNT_LEVELS)
      .addLevelableProperty(HEALTH_LEVELS);

  private static final int MAX = 4;

  public DogSpawner() {
    super(LEVEL_ITEM.getStartItem(), 20 * 60);
  }

  @Override
  public List<Entity> getEntities(User user, ExItemStack item) {

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

    int amount = AMOUNT_LEVELS.getValueFromItem(item);
    float health = 2 * HEALTH_LEVELS.<Integer>getValueFromItem(item);

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      entities.add(this.getDog(user, health));
    }

    return entities;
  }

  private Wolf getDog(User user, float health) {
    return new WolfBuilder()
        .applyOnEntity(e -> {
          e.setTame(true, true);
          e.setOwnerUUID(user.getUniqueId());
          e.setOrderedToSit(false);
          e.getBukkitLivingEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);
        })
        .setMaxHealthAndHealth(health)
        .addPathfinderGoal(1, e -> new FloatGoal(e))
        .addPathfinderGoal(4, e -> new LeapAtTargetGoal(e, 0.4F))
        .addPathfinderGoal(5, e -> new MeleeAttackGoal(e, 1.0, false))
        .addPathfinderGoal(6, e -> new FollowOwnerGoal(e, 1.2D, 10.0F, 2.0F))
        .addPathfinderGoal(8, e -> new RandomStrollGoal(e, 1.0D))
        .addPathfinderGoal(10, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(10, e -> new RandomLookAroundGoal(e))
        .addTargetGoal(1, e -> new OwnerHurtByTargetGoal(e))
        .addTargetGoal(2, e -> new OwnerHurtTargetGoal(e))
        .addTargetGoal(3, e -> new HurtByTargetGoal(e, MobDefServer.DEFENDER_CLASSES.toArray(Class[]::new)))
        .addTargetGoals(4, MobDefServer.ATTACKER_ENTITY_CLASSES.stream()
            .map(c -> e -> new NearestAttackableTargetGoal<>(e, c, true, false)))
        .build(user.getExWorld().getHandle());
  }
}
