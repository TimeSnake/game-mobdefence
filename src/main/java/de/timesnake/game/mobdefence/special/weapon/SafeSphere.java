/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem.Builder;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class SafeSphere extends ReloadableWeapon {

  private static final double RADIUS = 3;

  public static final ExItemStack ITEM = new ExItemStack(Material.SHIELD)
      .setDisplayName("§6Safe Sphere")
      .setLore("§7Creates a safe sphere,", "§7which mobs can not enter.")
      .setDropable(false)
      .immutable();

  private static final LevelableProperty.Builder COOLDOWN = new LevelableProperty.Builder()
      .name("Cooldown")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelLoreLine(3)
      .levelDecimalDigit(0)
      .levelLoreName("Cooldown")
      .levelUnit("s")
      .levelDescription("-2 s")
      .addTagLevel(null, 30)
      .addTagLevel(Price.bronze(32), 28)
      .addTagLevel(Price.silver(32), 26)
      .addTagLevel(Price.gold(24), 24)
      .addTagLevel(Price.bronze(46), 22)
      .addTagLevel(Price.bronze(64), 20)
      .addTagLevel(Price.silver(53), 18);

  private static final LevelableProperty.Builder DURATION = new LevelableProperty.Builder()
      .name("Duration")
      .display(new ExItemStack(Material.CLOCK))
      .defaultLevel(1)
      .levelLoreLine(4)
      .levelDecimalDigit(0)
      .levelLoreName("Duration")
      .levelUnit("s")
      .levelDescription("+1 s")
      .addTagLevel(null, 3)
      .addTagLevel(Price.bronze(35), 4)
      .addTagLevel(Price.silver(45), 5)
      .addTagLevel(Price.gold(28), 6)
      .addTagLevel(Price.bronze(54), 7)
      .addTagLevel(Price.bronze(64), 8)
      .addTagLevel(Price.silver(59), 9);

  public static final UpgradeableGoodItem.Builder SAFE_SPHERE = new Builder()
      .name("Save Sphere")
      .startItem(ITEM)
      .price(new Price(32, Currency.SILVER))
      .unlockedAtWave(7)
      .display(ITEM)
      .addLevelableProperty(COOLDOWN)
      .addLevelableProperty(DURATION);


  private final HashMap<MobDefUser, BukkitTask> taskByUser = new HashMap<>();

  public SafeSphere() {
    super(ITEM);
  }

  @Override
  public void update(MobDefUser user, ExItemStack item, int delay) {
    super.update(user, item, delay);
  }

  @Override
  public int getCooldown(ExItemStack item) {
    return COOLDOWN.getValueFromItem(item);
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    super.onInteract(item, user);

    this.createSphere(user, DURATION.getValueFromItem(item));
  }

  private void createSphere(MobDefUser user, int duration) {
    Location location = user.getLocation();
    this.taskByUser.put(user, Server.runTaskTimerSynchrony(i -> this.knockbackMobs(location),
        duration, true, 10, 10, GameMobDefence.getPlugin()));
  }

  private void knockbackMobs(Location location) {
    for (LivingEntity entity : location.getNearbyLivingEntities(RADIUS)) {
      if (MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
        entity.setVelocity(entity.getLocation().toVector()
            .subtract(location.toVector()).normalize().multiply(1.5));
      }
    }
  }

  @Override
  public void cancelAll() {
    super.cancelAll();
    this.taskByUser.values().forEach(BukkitTask::cancel);
    this.taskByUser.clear();
  }
}
