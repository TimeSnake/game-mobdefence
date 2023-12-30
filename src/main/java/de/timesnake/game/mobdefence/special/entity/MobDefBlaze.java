/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Trade;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.BlazeBuilder;
import de.timesnake.library.entities.pathfinder.BlazeAttackGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

public class MobDefBlaze extends BlockSpawner implements Listener {

  public static final ExItemStack ITEM = new ExItemStack(Material.MAGMA_BLOCK, "§6Blaze",
      "§7Place the block to spawn a blaze").immutable();

  public static final Trade.Builder BLAZE = new Trade.Builder()
      .price(new Price(16, Currency.SILVER))
      .giveItems(MobDefBlaze.ITEM.cloneWithId().asQuantity(3));

  public MobDefBlaze() {
    super(EntityType.BLAZE, ITEM);
  }

  @Override
  public void spawnEntities(Location location) {
    Blaze blaze = new BlazeBuilder()
        .applyOnEntity(e -> {
          e.setPos(location.getX(), location.getY(), location.getZ());
          e.setPersistenceRequired(true);
        })
        .setMaxHealthAndHealth(40)
        .addPathfinderGoal(1, e -> new BlazeAttackGoal(e))
        .addPathfinderGoal(8, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(8, e -> new RandomLookAroundGoal(e))
        .addTargetGoal(3, e -> new HurtByTargetGoal(e, MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)))
        .addTargetGoals(4, MobDefMob.ATTACKER_ENTITY_CLASSES.stream()
            .map(c -> e -> new NearestAttackableTargetGoal<>(e, c, true, false)))
        .build(((CraftWorld) location.getWorld()).getHandle());

    EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), blaze);
  }
}
