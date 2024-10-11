/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BoomerangAxe extends CooldownWeapon implements UserInventoryInteractListener {

  public static final ExItemStack ITEM = new ExItemStack(Material.WOODEN_AXE).setDisplayName("§6Boomerang Axe")
      .unbreakable().immutable();

  private static final LevelableProperty.Builder SPEED_LEVELS = new LevelableProperty.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelDescription("+0.2 Speed")
      .levelLoreLine(1)
      .levelDecimalDigit(1)
      .levelLoreName("Speed")
      .addTagLevel(null, 1f)
      .addTagLevel(new Price(6, Currency.BRONZE), 1.2f)
      .addTagLevel(new Price(6, Currency.SILVER), 1.4f)
      .addTagLevel(new Price(6, Currency.GOLD), 1.6f)
      .addTagLevel(new Price(24, Currency.BRONZE), 1.8f)
      .addTagLevel(new Price(32, Currency.SILVER), 2.0f)
      .addTagLevel(new Price(64, Currency.BRONZE), 2.2f)
      .addTagLevel(new Price(24, Currency.GOLD), 2.4f)
      .addTagLevel(new Price(48, Currency.SILVER), 2.6f);

  private static final LevelableProperty.Builder DAMAGE_LEVELS = new LevelableProperty.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(1)
      .levelDescription("+0.5 ❤")
      .levelLoreLine(2)
      .levelDecimalDigit(1)
      .levelUnit("❤")
      .levelLoreName("Damage")
      .addTagLevel(null, 4f)
      .addTagLevel(new Price(5, Currency.BRONZE), 4.5f)
      .addTagLevel(new Price(5, Currency.SILVER), 5f)
      .addTagLevel(new Price(5, Currency.GOLD), 5.5f)
      .addTagLevel(new Price(31, Currency.BRONZE), 6f)
      .levelDescription("+1 ❤")
      .addTagLevel(new Price(33, Currency.SILVER), 7f)
      .addTagLevel(new Price(53, Currency.BRONZE), 8f)
      .addTagLevel(new Price(27, Currency.GOLD), 9f)
      .addTagLevel(new Price(48, Currency.SILVER), 10f);


  private static final LevelableProperty.Builder DISTANCE_LEVELS = new LevelableProperty.Builder()
      .name("Distance")
      .display(new ExItemStack(Material.CHAIN))
      .defaultLevel(1)
      .levelDescription("+1 Block")
      .levelLoreLine(3)
      .levelDecimalDigit(0)
      .levelLoreName("Distance")
      .addTagLevel(null, 10)
      .addTagLevel(new Price(16, Currency.BRONZE), 11)
      .addTagLevel(new Price(14, Currency.SILVER), 12)
      .addTagLevel(new Price(10, Currency.GOLD), 13)
      .addTagLevel(new Price(41, Currency.BRONZE), 14);

  public static final UpgradeableGoodItem.Builder BOOMERANG_AXE = new UpgradeableGoodItem.Builder()
      .name("Boomerang Axe")
      .display(ITEM.cloneWithId())
      .price(new Price(4, Currency.GOLD))
      .startItem(ITEM.cloneWithId())
      .addLevelableProperty(SPEED_LEVELS)
      .addLevelableProperty(DAMAGE_LEVELS)
      .addLevelableProperty(DISTANCE_LEVELS);

  private final Map<ArmorStand, BukkitTask> tasks = new HashMap<>();

  public BoomerangAxe() {
    super(BOOMERANG_AXE.getStartItem());
    Server.getInventoryEventManager().addInteractListener(this, BOOMERANG_AXE.getStartItem());
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    float speed = SPEED_LEVELS.getValueFromItem(item);
    float damage = DAMAGE_LEVELS.getValueFromItem(item);
    int maxDistance = DISTANCE_LEVELS.getValueFromItem(item);

    final Location startLoc = user.getLocation().clone().add(0, -0.35, 0);

    ArmorStand stand = user.getExWorld().spawn(startLoc, ArmorStand.class);

    final org.bukkit.util.Vector startVec = user.getLocation().getDirection().normalize()
        .multiply(speed);

    Vector vec = startVec.clone();

    stand.setVisible(false);
    stand.setItem(EquipmentSlot.HEAD, BOOMERANG_AXE.getStartItem());
    stand.setInvulnerable(true);
    stand.setGravity(false);
    stand.setCollidable(false);
    stand.setKiller(user.getPlayer());
    stand.setHeadPose(new EulerAngle(Math.PI / 2, 0, 0));

    AtomicReference<Float> rotation = new AtomicReference<>((float) 0);

    AtomicInteger counter = new AtomicInteger();

    AtomicInteger damageCounter = new AtomicInteger();

    this.tasks.put(stand, Server.runTaskTimerSynchrony(() -> {
      Location loc = stand.getLocation();

      double distance = loc.distanceSquared(startLoc);

      if (distance < 0.2 && !startVec.equals(vec)) {
        this.dropBoomerang(stand);
        return;
      }

      if (distance >= maxDistance * maxDistance) {
        vec.multiply(-1);
      }

      if (!MobDefServer.EMPTY_MATERIALS.contains(
          stand.getEyeLocation().add(vec).getBlock().getType())) {
        if (counter.get() >= 1) {
          this.dropBoomerang(stand);
          return;
        }

        vec.multiply(-1);
        counter.getAndIncrement();
      }

      if (damageCounter.get() % 2 == 0) {
        for (LivingEntity entity : loc.getNearbyLivingEntities(1, 2)) {
          if (MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            entity.damage(damage * 2, user.getPlayer());
          }
        }
      }

      stand.setVelocity(vec);
      stand.teleport(loc.add(vec));
      rotation.updateAndGet(v -> v + 30);
      stand.setRotation(rotation.get(), 0);

      damageCounter.getAndIncrement();
    }, 0, 1, GameMobDefence.getPlugin()));
  }

  @Override
  public int getCooldown(ExItemStack item) {
    return 20;
  }

  private void dropBoomerang(ArmorStand stand) {
    stand.remove();
    BukkitTask task = this.tasks.remove(stand);
    task.cancel();
  }
}
