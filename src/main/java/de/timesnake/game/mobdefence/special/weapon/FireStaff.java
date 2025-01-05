/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;

public class FireStaff extends InteractWeapon implements Listener, UserInventoryInteractListener {

  private static final String NAME = "firestaff";
  private static final NamespacedKey NAME_KEY = NamespacedKey.fromString("firestaff:name");
  private static final NamespacedKey FIRE_RADIUS = NamespacedKey.fromString("firestaff:fire_radius");
  private static final NamespacedKey BURNING_TIME = NamespacedKey.fromString("firestaff:burning_time");

  private static final ExItemStack ITEM = new ExItemStack(Material.BLAZE_ROD)
      .setDisplayName("§6Fire Staff")
      .unbreakable().immutable();

  private static final LevelableProperty.Builder SPEED_LEVELS = new LevelableProperty.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelDescription("+0.2 Speed")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Speed")
      .addTagLevel(null, 2)
      .addTagLevel(new Price(4, Currency.BRONZE), 3)
      .addTagLevel(new Price(4, Currency.SILVER), 4)
      .addTagLevel(new Price(5, Currency.GOLD), 5);

  private static final LevelableProperty.Builder FIRE_RADIUS_LEVELS = new LevelableProperty.Builder()
      .name("Fire Radius")
      .display(new ExItemStack(Material.TARGET))
      .defaultLevel(1)
      .levelDescription("+0.5 Blocks")
      .levelDecimalDigit(1)
      .levelUnit("blocks")
      .levelLoreLine(2)
      .levelLoreName("Fire Radius")
      .addTagLevel(null, 2f)
      .addTagLevel(new Price(7, Currency.BRONZE), 2.5f)
      .addTagLevel(new Price(8, Currency.SILVER), 3f)
      .addTagLevel(new Price(7, Currency.GOLD), 3.5f)
      .addTagLevel(new Price(20, Currency.BRONZE), 4f);

  private static final LevelableProperty.Builder BURNING_TIME_LEVELS = new LevelableProperty.Builder()
      .name("Burning Time")
      .display(new ExItemStack(Material.BLAZE_POWDER))
      .defaultLevel(1)
      .levelDescription("+1 Second")
      .levelDecimalDigit(0)
      .levelUnit("s")
      .levelLoreLine(3)
      .levelLoreName("Burning Time")
      .addTagLevel(null, 3)
      .addTagLevel(new Price(8, Currency.BRONZE), 4)
      .addTagLevel(new Price(7, Currency.SILVER), 5)
      .addTagLevel(new Price(9, Currency.GOLD), 6)
      .addTagLevel(new Price(18, Currency.SILVER), 7);

  private static final LevelableProperty.Builder FIRE_RATE_LEVELS = new LevelableProperty.Builder()
      .name("Fire Rate")
      .display(new ExItemStack(Material.YELLOW_DYE))
      .defaultLevel(1)
      .levelDescription("+1 per sec.")
      .levelDecimalDigit(0)
      .levelUnit("per sec.")
      .levelLoreLine(4)
      .levelLoreName("Fire Rate")
      .addTagLevel(null, 1)
      .addTagLevel(new Price(6, Currency.BRONZE), 2)
      .addTagLevel(new Price(10, Currency.SILVER), 3)
      .addTagLevel(new Price(8, Currency.GOLD), 4)
      .addTagLevel(new Price(15, Currency.GOLD), 5);

  public static final UpgradeableGoodItem.Builder FIRE_STAFF = new UpgradeableGoodItem.Builder()
      .name("Fire Staff")
      .display(new ExItemStack(Material.BLAZE_ROD, "§6Fire Staff"))
      .price(new Price(4, Currency.SILVER))
      .startItem(ITEM.cloneWithId())
      .addLevelableProperty(SPEED_LEVELS)
      .addLevelableProperty(FIRE_RADIUS_LEVELS)
      .addLevelableProperty(BURNING_TIME_LEVELS)
      .addLevelableProperty(FIRE_RATE_LEVELS);

  private final Set<User> fireStaffCooldownUser = new HashSet<>();

  public FireStaff() {
    super(ITEM);
    Server.registerListener(this, GameMobDefence.getPlugin());
    Server.getInventoryEventManager().addInteractListener(this, ITEM);
  }

  @EventHandler
  public void onFireballHit(ProjectileHitEvent e) {
    Projectile proj = e.getEntity();

    if (NAME.equals(proj.getPersistentDataContainer().get(NAME_KEY, PersistentDataType.STRING))) {
      float radius = proj.getPersistentDataContainer().get(FIRE_RADIUS, PersistentDataType.FLOAT);
      int burningTime = proj.getPersistentDataContainer().get(BURNING_TIME, PersistentDataType.INTEGER);

      for (Entity entity : e.getEntity().getLocation().getNearbyEntitiesByType(LivingEntity.class, radius)) {
        if (MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
          ((Monster) entity).damage(2, proj);
          entity.setFireTicks(burningTime * 20);
        }
      }
    }
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    if (!this.fireStaffCooldownUser.contains(user)) {
      this.fireStaffCooldownUser.add(user);

      int speed = SPEED_LEVELS.getValueFromItem(item);
      float radius = FIRE_RADIUS_LEVELS.getValueFromItem(item);
      int burningTime = BURNING_TIME_LEVELS.getValueFromItem(item);
      int fireRate = FIRE_RATE_LEVELS.getValueFromItem(item);

      Fireball fireball = user.getExWorld().spawn(user.getPlayer().getEyeLocation(), Fireball.class);

      fireball.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));
      fireball.setDirection(user.getLocation().getDirection());
      fireball.setShooter(user.getPlayer());
      fireball.getPersistentDataContainer().set(NAME_KEY, PersistentDataType.STRING, NAME);
      fireball.getPersistentDataContainer().set(FIRE_RADIUS, PersistentDataType.FLOAT, radius);
      fireball.getPersistentDataContainer().set(BURNING_TIME, PersistentDataType.INTEGER, burningTime);

      Server.runTaskLaterSynchrony(() -> this.fireStaffCooldownUser.remove(user), (int) ((1d / fireRate) * 20),
          GameMobDefence.getPlugin());
    }
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
    if (e.getEntity() instanceof Fireball) {
      e.setYield(0);
      e.setCancelled(true);
    }
  }
}
