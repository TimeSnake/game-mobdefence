/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
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

  public static final ExItemStack AXE = new ExItemStack(Material.WOODEN_AXE, "§6Boomerang Axe")
      .unbreakable().immutable();

  private static final LevelType.Builder SPEED_LEVELS = new LevelType.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .baseLevel(1)
      .levelDescription("+0.2 Speed")
      .levelLoreLine(1)
      .levelDecimalDigit(1)
      .levelItem(AXE)
      .levelLoreName("Speed")
      .addLoreLvl(null, 1)
      .addLoreLvl(new Price(6, Currency.BRONZE), 1.2)
      .addLoreLvl(new Price(6, Currency.SILVER), 1.4)
      .addLoreLvl(new Price(6, Currency.GOLD), 1.6)
      .addLoreLvl(new Price(24, Currency.BRONZE), 1.8)
      .addLoreLvl(new Price(32, Currency.SILVER), 2.0)
      .addLoreLvl(new Price(64, Currency.BRONZE), 2.2)
      .addLoreLvl(new Price(24, Currency.GOLD), 2.4)
      .addLoreLvl(new Price(48, Currency.SILVER), 2.6);

  private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .baseLevel(1)
      .levelDescription("+0.5 ❤")
      .levelLoreLine(2)
      .levelDecimalDigit(1)
      .levelUnit("❤")
      .levelItem(AXE)
      .levelLoreName("Damage")
      .addLoreLvl(null, 4)
      .addLoreLvl(new Price(5, Currency.BRONZE), 4.5)
      .addLoreLvl(new Price(5, Currency.SILVER), 5)
      .addLoreLvl(new Price(5, Currency.GOLD), 5.5)
      .addLoreLvl(new Price(31, Currency.BRONZE), 6)
      .levelDescription("+1 ❤")
      .addLoreLvl(new Price(33, Currency.SILVER), 7)
      .addLoreLvl(new Price(53, Currency.BRONZE), 8)
      .addLoreLvl(new Price(27, Currency.GOLD), 9)
      .addLoreLvl(new Price(48, Currency.SILVER), 10);


  private static final LevelType.Builder DISTANCE_LEVELS = new LevelType.Builder()
      .name("Distance")
      .display(new ExItemStack(Material.CHAIN))
      .baseLevel(1)
      .levelDescription("+1 Block")
      .levelLoreLine(3)
      .levelDecimalDigit(0)
      .levelItem(AXE)
      .levelLoreName("Distance")
      .addLoreLvl(null, 10)
      .addLoreLvl(new Price(16, Currency.BRONZE), 11)
      .addLoreLvl(new Price(14, Currency.SILVER), 12)
      .addLoreLvl(new Price(10, Currency.GOLD), 13)
      .addLoreLvl(new Price(41, Currency.BRONZE), 14);

  public static final UpgradeableItem.Builder BOOMERANG_AXE = new UpgradeableItem.Builder()
      .name("Boomerang Axe")
      .display(AXE.cloneWithId())
      .price(new Price(4, Currency.GOLD))
      .baseItem(AXE.cloneWithId())
      .addLvlType(SPEED_LEVELS)
      .addLvlType(DAMAGE_LEVELS)
      .addLvlType(DISTANCE_LEVELS);

  private final Map<ArmorStand, BukkitTask> tasks = new HashMap<>();

  public BoomerangAxe() {
    super(BOOMERANG_AXE.getBaseItem());
    Server.getInventoryEventManager().addInteractListener(this, BOOMERANG_AXE.getBaseItem());
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    double speed = SPEED_LEVELS.getNumberFromLore(item, Double::valueOf);
    double damage = DAMAGE_LEVELS.getNumberFromLore(item, Double::valueOf);
    int maxDistance = DISTANCE_LEVELS.getNumberFromLore(item, Integer::valueOf);

    final Location startLoc = user.getLocation().clone().add(0, -0.35, 0);

    ArmorStand stand = user.getExWorld().spawn(startLoc, ArmorStand.class);

    final org.bukkit.util.Vector startVec = user.getLocation().getDirection().normalize()
        .multiply(speed);

    Vector vec = startVec.clone();

    stand.setVisible(false);
    stand.setItem(EquipmentSlot.HEAD, BOOMERANG_AXE.getBaseItem());
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

      if (!BlockCheck.WALKABLE_IN.isTagged(
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
          if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
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
