/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.SimpleGood;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.SnowGolemBuilder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snowman extends BlockSpawner implements Listener {

  public static final ExItemStack ITEM = new ExItemStack(Material.CARVED_PUMPKIN,
      "§6Snowmen", "§7Place the block to spawn a snowman").immutable();

  public static final SimpleGood.Builder SNOWMAN = new SimpleGood.Builder()
      .price(new Price(8, Currency.GOLD))
      .giveItems(Snowman.ITEM.cloneWithId().asQuantity(4))
      .slot(47);

  public Snowman() {
    super(EntityType.SNOW_GOLEM, ITEM);
  }

  @Override
  public void spawnEntities(Location location) {
    SnowGolem snowman = new SnowGolemBuilder()
        .applyOnEntity(e -> {
          e.setPos(location.getX(), location.getY(), location.getZ());
          e.setPumpkin(false);
          e.setPersistenceRequired(true);
        })
        .setMaxHealthAndHealth(40)
        .addPathfinderGoal(1, e -> new RangedAttackGoal(e, 0D, 4, 10.0F))
        .addPathfinderGoal(3, e -> new LookAtPlayerGoal(e, Player.class, 6.0F))
        .addPathfinderGoal(4, e -> new RandomStrollGoal(e, 0))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .addTargetGoal(1, e -> new HurtByTargetGoal(e, MobDefServer.DEFENDER_CLASSES.toArray(Class[]::new)))
        .addTargetGoals(2, MobDefServer.ATTACKER_ENTITY_CLASSES.stream()
            .map(c -> e -> new NearestAttackableTargetGoal<>(e, c, true, false)))
        .build(((CraftWorld) location.getWorld()).getHandle());

    EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), snowman);
  }

  @EventHandler
  public void onArrowHit(ProjectileHitEvent e) {
    if (!(e.getEntity() instanceof Snowball snowball)) {
      return;
    }

    if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity entity)) {
      return;
    }

    if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
      e.setCancelled(true);
      return;
    }

    if (!(snowball.getShooter() instanceof org.bukkit.entity.Snowman)) {
      return;
    }

    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 1));
    entity.damage(1, snowball);
  }
}
