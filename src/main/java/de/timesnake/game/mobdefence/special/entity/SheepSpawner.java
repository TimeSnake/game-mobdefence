/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.entity.SheepBuilder;
import de.timesnake.library.entities.pathfinder.PetGoal;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SheepSpawner extends EntitySpawner {

  public static final ExItemStack ITEM = new ExItemStack(Material.WHEAT, "§6Herd Sheeps");

  public static final LevelableProperty.Builder AMOUNT_LEVELS = new LevelableProperty.Builder()
      .name("Amount")
      .display(new ExItemStack(Material.SHEEP_SPAWN_EGG))
      .defaultLevel(1)
      .levelDescription("+1 Sheep")
      .levelLoreLine(1)
      .levelLoreName("Sheeps")
      .addTagLevel(null, 4)
      .addTagLevel(new Price(12, Currency.BRONZE), 5)
      .addTagLevel(new Price(14, Currency.SILVER), 6)
      .addTagLevel(new Price(24, Currency.BRONZE), 7)
      .addTagLevel(new Price(16, Currency.GOLD), 8);

  public static final UpgradeableGoodItem.Builder LEVEL_ITEM = new UpgradeableGoodItem.Builder()
      .name("§6Herd Sheep")
      .display(new ExItemStack(Material.WHEAT, "§6Sheeps"))
      .startItem(ITEM)
      .addLevelableProperty(AMOUNT_LEVELS);

  private static final int MAX = 4;

  public SheepSpawner() {
    super(LEVEL_ITEM.getStartItem(), 5 * 20);
  }

  @Override
  public List<? extends Entity> getEntities(
      User user, ExItemStack item) {

    int sheep = 0;
    for (org.bukkit.entity.Entity s : user.getExWorld().getEntitiesByClasses(org.bukkit.entity.Sheep.class)) {
      if (s.customName() != null && s.customName().equals(Component.text(user.getName()))) {
        sheep++;
      }
    }

    if (sheep >= MAX) {
      user.sendActionBarText(Component.text("Too many are alive", ExTextColor.WARNING));
      return new ArrayList<>();
    }

    int amount = AMOUNT_LEVELS.getValueFromItem(item);

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      entities.add(this.getSheep(user));
    }

    return entities;
  }

  private Sheep getSheep(User user) {
    return new SheepBuilder()
        .setMaxHealthAndHealth(20)
        .applyOnEntity(e -> {
          e.setGlowingTag(true);
          e.setInvisible(true);
          e.setCustomName(net.minecraft.network.chat.Component.literal(user.getName()));
          e.setCustomNameVisible(false);
        })
        .addPathfinderGoal(0, e -> new FloatGoal(e))
        .addPathfinderGoal(1, e -> new PetGoal(e, user.getMinecraftPlayer(), 1.3, 4, 7, true))
        .addPathfinderGoal(2, e -> new RandomStrollGoal(e, 1.0))
        .addPathfinderGoal(3, e -> new LookAtPlayerGoal(e, Player.class, 6.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .build(user.getExWorld().getHandle());
  }
}
