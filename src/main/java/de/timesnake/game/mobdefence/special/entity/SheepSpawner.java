/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
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

  public static final LevelType.Builder AMOUNT_LEVELS = new LevelType.Builder()
      .name("Amount")
      .display(new ExItemStack(Material.SHEEP_SPAWN_EGG))
      .baseLevel(1)
      .levelDescription("+1 Sheep")
      .levelLoreLine(1)
      .levelLoreName("Sheeps")
      .addLoreLvl(null, 4)
      .addLoreLvl(new Price(12, Currency.BRONZE), 5)
      .addLoreLvl(new Price(14, Currency.SILVER), 6)
      .addLoreLvl(new Price(24, Currency.BRONZE), 7)
      .addLoreLvl(new Price(16, Currency.GOLD), 8);

  public static final UpgradeableItem.Builder LEVEL_ITEM = new UpgradeableItem.Builder()
      .name("§6Herd Sheep")
      .display(new ExItemStack(Material.WHEAT, "§6Sheeps"))
      .baseItem(new ExItemStack(Material.WHEAT, "§6Herd Sheeps"))
      .addLvlType(AMOUNT_LEVELS);

  private static final int MAX = 4;

  public SheepSpawner() {
    super(LEVEL_ITEM.getBaseItem(), 5 * 20);
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

    int amount = AMOUNT_LEVELS.getNumberFromLore(item, Integer::valueOf);

    List<Entity> entities = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      entities.add(this.getSheep(user));
    }

    return entities;
  }

  private Sheep getSheep(User user) {
    return new SheepBuilder(user.getExWorld().getHandle(), false, false)
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
        .build();
  }
}
