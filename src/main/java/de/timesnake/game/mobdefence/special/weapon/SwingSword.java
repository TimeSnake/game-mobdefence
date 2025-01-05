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
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SwingSword extends CooldownWeapon implements UserInventoryInteractListener {

  private static final ExItemStack ITEM = new ExItemStack(Material.GOLDEN_SWORD)
      .unbreakable().setDisplayName("§6Swing Sword").enchant().immutable();

  private static final LevelableProperty.Builder DAMAGE_LEVELS = new LevelableProperty.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(1)
      .levelDescription("+1 ❤")
      .levelUnit("❤")
      .levelDecimalDigit(0)
      .levelLoreLine(1)
      .levelLoreName("Damage")
      .addTagLevel(null, 3)
      .addTagLevel(new Price(9, Currency.BRONZE), 4)
      .addTagLevel(new Price(15, Currency.SILVER), 5)
      .addTagLevel(new Price(7, Currency.GOLD), 6)
      .addTagLevel(new Price(27, Currency.BRONZE), 7)
      .addTagLevel(new Price(25, Currency.SILVER), 8)
      .addTagLevel(new Price(15, Currency.GOLD), 9)
      .addTagLevel(new Price(48, Currency.BRONZE), 10)
      .addTagLevel(new Price(48, Currency.SILVER), 11)
      .addTagLevel(new Price(64, Currency.BRONZE), 12)
      .addTagLevel(new Price(64, Currency.SILVER), 13);

  private static final LevelableProperty.Builder RADIUS_LEVELS = new LevelableProperty.Builder()
      .name("Radius")
      .display(new ExItemStack(Material.TARGET))
      .defaultLevel(1)
      .levelDescription("+0.25 Blocks")
      .levelDecimalDigit(1)
      .levelUnit("blocks")
      .levelLoreLine(2)
      .levelLoreName("Radius")
      .addTagLevel(null, 1.5f)
      .addTagLevel(new Price(9, Currency.BRONZE), 1.75f)
      .addTagLevel(new Price(14, Currency.SILVER), 2f)
      .addTagLevel(new Price(32, Currency.BRONZE), 2.25f)
      .addTagLevel(new Price(13, Currency.GOLD), 2.5f);

  private static final LevelableProperty.Builder COOLDOWN_LEVELS = new LevelableProperty.Builder()
      .name("Cooldown")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelDecimalDigit(0)
      .levelUnit("s")
      .levelLoreLine(3)
      .levelLoreName("Cooldown")
      .levelDescription("-2 s")
      .addTagLevel(null, 10)
      .addTagLevel(new Price(12, Currency.BRONZE), 8)
      .addTagLevel(new Price(18, Currency.SILVER), 6)
      .addTagLevel(new Price(16, Currency.GOLD), 4)
      .addTagLevel(new Price(34, Currency.SILVER), 2)
      .levelDescription("-1 s")
      .addTagLevel(new Price(64, Currency.BRONZE), 1);

  public static final UpgradeableGoodItem.Builder SWORD = new UpgradeableGoodItem.Builder()
      .name("Swing Sword")
      .price(new Price(8, Currency.GOLD))
      .startItem(ITEM.cloneWithId())
      .display(ITEM.cloneWithId())
      .addLevelableProperty(RADIUS_LEVELS)
      .addLevelableProperty(DAMAGE_LEVELS)
      .addLevelableProperty(COOLDOWN_LEVELS);


  private final Map<ArmorStand, BukkitTask> tasks = new HashMap<>();

  public SwingSword() {
    super(ITEM);
    Server.getInventoryEventManager().addInteractListener(this, ITEM);
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    int damage = DAMAGE_LEVELS.getValueFromItem(item);
    float radius = RADIUS_LEVELS.getValueFromItem(item);

    Location loc = user.getLocation().clone().add(0, -0.35, 0);

    for (Entity entity : loc.getWorld().getNearbyEntities(loc, radius, 1.5, radius,
        e -> MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getType()))) {

      Vector vec = entity.getLocation().toVector().subtract(loc.toVector());
      double knockback = 2 / (vec.length() > 1 ? vec.length() : 1D);

      ((LivingEntity) entity).damage(damage * 2, user.getPlayer());
      entity.setVelocity(vec.setY(0).normalize().setY(1).normalize().multiply(knockback));
    }

    ArmorStand stand = user.getExWorld().spawn(loc, ArmorStand.class);

    stand.setVisible(false);
    stand.getEquipment().setItem(EquipmentSlot.HEAD, ITEM);
    stand.setInvulnerable(true);
    stand.setGravity(false);
    stand.setCollidable(false);
    stand.setHeadPose(new EulerAngle(Math.PI / 2, 0, 0));

    AtomicReference<Float> rotation = new AtomicReference<>((float) 0);

    this.tasks.put(stand, Server.runTaskTimerSynchrony(() -> {
      if (rotation.get() >= 720) {
        stand.remove();
        BukkitTask task = this.tasks.remove(stand);
        task.cancel();
        return;
      }

      rotation.updateAndGet(v -> v + 36);
      stand.setRotation(rotation.get(), 0);

    }, 0, 1, GameMobDefence.getPlugin()));
  }

  @Override
  public int getCooldown(ExItemStack item) {
    return COOLDOWN_LEVELS.<Integer>getValueFromItem(item) * 20;
  }
}
