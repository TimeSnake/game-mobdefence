/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.Set;

public class FireStaff extends InteractWeapon implements Listener, UserInventoryInteractListener {

  public static final double SPEED = 1; // blocks per second
  public static final int FIRE_RATE = 1; // per second
  public static final int FIRE_RADIUS = 2; // blocks
  public static final int BURNING_TIME = 3; // in seconds
  private static final String NAME = "firestaff";

  private static final ExItemStack ITEM = new ExItemStack(Material.BLAZE_ROD, "§6Fire Staff")
      .unbreakable().immutable();

  private static final LevelType.Builder SPEED_LEVELS = new LevelType.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .baseLevel(1)
      .levelDescription("+0.2 Speed")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Speed")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(4, Currency.BRONZE), 3)
      .addLoreLvl(new Price(4, Currency.SILVER), 4)
      .addLoreLvl(new Price(5, Currency.GOLD), 5);

  private static final LevelType.Builder FIRE_RADIUS_LEVELS = new LevelType.Builder()
      .name("Fire Radius")
      .display(new ExItemStack(Material.TARGET))
      .baseLevel(1)
      .levelDescription("+0.5 Blocks")
      .levelDecimalDigit(1)
      .levelUnit("blocks")
      .levelLoreLine(2)
      .levelLoreName("Fire Radius")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(7, Currency.BRONZE), 2.5)
      .addLoreLvl(new Price(8, Currency.SILVER), 3)
      .addLoreLvl(new Price(7, Currency.GOLD), 3.5)
      .addLoreLvl(new Price(20, Currency.BRONZE), 4);

  private static final LevelType.Builder BURNING_TIME_LEVELS = new LevelType.Builder()
      .name("Burning Time")
      .display(new ExItemStack(Material.BLAZE_POWDER))
      .baseLevel(1)
      .levelDescription("+1 Second")
      .levelDecimalDigit(0)
      .levelUnit("s")
      .levelLoreLine(3)
      .levelLoreName("Burning Time")
      .levelItem(ITEM)
      .addLoreLvl(null, 3)
      .addLoreLvl(new Price(8, Currency.BRONZE), 4)
      .addLoreLvl(new Price(7, Currency.SILVER), 5)
      .addLoreLvl(new Price(9, Currency.GOLD), 6)
      .addLoreLvl(new Price(18, Currency.SILVER), 7);

  private static final LevelType.Builder FIRE_RATE_LEVELS = new LevelType.Builder()
      .name("Fire Rate")
      .display(new ExItemStack(Material.YELLOW_DYE))
      .baseLevel(1)
      .levelDescription("+1 per sec.")
      .levelDecimalDigit(0)
      .levelUnit("per sec.")
      .levelLoreLine(4)
      .levelLoreName("Fire Rate")
      .levelItem(ITEM)
      .addLoreLvl(null, 1)
      .addLoreLvl(new Price(6, Currency.BRONZE), 2)
      .addLoreLvl(new Price(10, Currency.SILVER), 3)
      .addLoreLvl(new Price(8, Currency.GOLD), 4)
      .addLoreLvl(new Price(15, Currency.GOLD), 5);

  public static final UpgradeableItem.Builder FIRE_STAFF = new UpgradeableItem.Builder()
      .name("Fire Staff")
      .display(new ExItemStack(Material.BLAZE_ROD, "§6Fire Staff"))
      .price(new Price(4, Currency.SILVER))
      .baseItem(ITEM.cloneWithId())
      .addLvlType(SPEED_LEVELS)
      .addLvlType(FIRE_RADIUS_LEVELS)
      .addLvlType(BURNING_TIME_LEVELS)
      .addLvlType(FIRE_RATE_LEVELS);

  private final Set<User> fireStaffCooldownUser = new HashSet<>();

  public FireStaff() {
    super(ITEM);
    Server.registerListener(this, GameMobDefence.getPlugin());
    Server.getInventoryEventManager().addInteractListener(this, ITEM);
  }

  @EventHandler
  public void onFireballHit(ProjectileHitEvent e) {
    Projectile proj = e.getEntity();

    if (proj.getCustomName() != null && proj.getCustomName().contains(NAME)) {

      String[] nameParts = proj.getCustomName().split(NAME);

      double radius = Double.parseDouble(nameParts[0]);
      int burningTime = Integer.parseInt(nameParts[1]);

      for (Entity entity : e.getEntity().getLocation()
          .getNearbyEntitiesByType(LivingEntity.class, radius)) {
        if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
          entity.setFireTicks(burningTime * 20);
        }
      }
    }
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    if (!this.fireStaffCooldownUser.contains(user)) {
      this.fireStaffCooldownUser.add(user);

      double speed = SPEED_LEVELS.getNumberFromLore(item, Double::valueOf);
      double radius = FIRE_RADIUS_LEVELS.getNumberFromLore(item, Double::valueOf);
      int burningTime = BURNING_TIME_LEVELS.getNumberFromLore(item, Integer::valueOf);
      int fireRate = FIRE_RATE_LEVELS.getNumberFromLore(item, Integer::valueOf);

      Fireball fireball = user.getExWorld()
          .spawn(user.getPlayer().getEyeLocation(), Fireball.class);

      fireball.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));
      fireball.setDirection(user.getLocation().getDirection());
      fireball.setShooter(user.getPlayer());

      fireball.setCustomName(radius + NAME + burningTime);
      fireball.setCustomNameVisible(false);

      Server.runTaskLaterSynchrony(() -> this.fireStaffCooldownUser.remove(user),
          (int) ((1d / fireRate) * 20),
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
