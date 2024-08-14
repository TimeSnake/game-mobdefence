/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.IronGolemBuilder;
import de.timesnake.library.entities.pathfinder.LocationGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

public class MobDefIronGolem extends BlockSpawner implements Listener {

  public static final ExItemStack ITEM = new ExItemStack(Material.IRON_BLOCK, "§6Iron Golem",
      "§7Place a block to spawn a golem", "§7The golem tries to hold his position")
      .immutable();

  public MobDefIronGolem() {
    super(EntityType.IRON_GOLEM, ITEM);
  }

  @Override
  public void spawnEntities(Location location) {
    IronGolem golem = new IronGolemBuilder()
        .applyOnEntity(e -> {
          e.setPos(location.getX(), location.getY(), location.getZ());
          e.setPersistenceRequired(true);
        })
        .addPathfinderGoal(1, e -> new MeleeAttackGoal(e, 1.0D, false))
        .addPathfinderGoal(2, e -> new MoveTowardsTargetGoal(e, 0.9D, 32.0F))
        .addPathfinderGoal(3, e -> new LocationGoal(e, location.getX(), location.getY(),
            location.getZ(), 1, 32, 2))
        .addPathfinderGoal(7, e -> new LookAtPlayerGoal(e, Player.class, 6.0F))
        .addPathfinderGoal(8, e -> new RandomLookAroundGoal(e))
        .addTargetGoal(1, e -> new HurtByTargetGoal(e, MobDefServer.DEFENDER_CLASSES.toArray(Class[]::new)))
        .addTargetGoals(2, MobDefServer.ATTACKER_ENTITY_CLASSES.stream()
            .map(c -> e -> new NearestAttackableTargetGoal<>(e, c, true, false)))
        .build(((CraftWorld) location.getWorld()).getHandle());

    EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), golem);
  }
}
