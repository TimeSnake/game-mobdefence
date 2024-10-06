/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.game.mobdefence.special.weapon.bullet.BulletManager;
import de.timesnake.game.mobdefence.special.weapon.bullet.PiercingBullet;
import de.timesnake.game.mobdefence.special.weapon.bullet.TargetFinder;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LlamaSpit;

import java.util.Objects;

public class Wand extends CooldownWeapon {

  private static final ExItemStack ITEM = new ExItemStack(Material.STICK, "§3Wand").enchant()
      .immutable();

  private static final LevelableProperty.Builder SPEED_LEVELS = new LevelableProperty.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelDescription("+0.4 Speed")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Speed")
      .levelItem(ITEM)
      .addTagLevel(null, 2f)
      .addTagLevel(new Price(4, Currency.BRONZE), 2.4f)
      .addTagLevel(new Price(7, Currency.SILVER), 2.8f)
      .addTagLevel(new Price(9, Currency.GOLD), 3.2f);

  private static final LevelableProperty.Builder DAMAGE_LEVELS = new LevelableProperty.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(1)
      .levelDescription("+0.5 ❤")
      .levelUnit("❤")
      .levelDecimalDigit(1)
      .levelLoreLine(2)
      .levelLoreName("Damage")
      .levelItem(ITEM)
      .addTagLevel(null, 1.5f)
      .addTagLevel(new Price(5, Currency.BRONZE), 2f)
      .addTagLevel(new Price(5, Currency.SILVER), 2.5f)
      .addTagLevel(new Price(5, Currency.GOLD), 3f)
      .addTagLevel(new Price(10, Currency.GOLD), 3.5f)
      .addTagLevel(new Price(16, Currency.SILVER), 4f)
      .addTagLevel(new Price(16, Currency.GOLD), 4.5f)
      .addTagLevel(new Price(32, Currency.SILVER), 5f)
      .addTagLevel(new Price(64, Currency.BRONZE), 5.5f)
      .addTagLevel(new Price(47, Currency.SILVER), 6f);

  private static final LevelableProperty.Builder FIRE_RATE_LEVELS = new LevelableProperty.Builder()
      .name("Fire Rate")
      .display(new ExItemStack(Material.YELLOW_DYE))
      .defaultLevel(1)
      .levelDescription("+1 per sec.")
      .levelDecimalDigit(0)
      .levelUnit("per sec.")
      .levelLoreLine(3)
      .levelLoreName("Fire Rate")
      .levelItem(ITEM)
      .addTagLevel(null, 2)
      .addTagLevel(new Price(6, Currency.BRONZE), 3)
      .addTagLevel(new Price(6, Currency.SILVER), 4)
      .addTagLevel(new Price(6, Currency.GOLD), 5)
      .addTagLevel(new Price(64, Currency.BRONZE), 6);

  private static final LevelableProperty.Builder PIERCING_LEVELS = new LevelableProperty.Builder()
      .name("Piercing")
      .display(new ExItemStack(Material.SOUL_TORCH))
      .defaultLevel(0)
      .levelDescription("+1 Mob")
      .levelDecimalDigit(0)
      .levelLoreLine(4)
      .levelUnit("mobs")
      .levelLoreName("Piercing")
      .levelItem(ITEM)
      .addTagLevel(new Price(10, Currency.SILVER), 1)
      .addTagLevel(new Price(11, Currency.GOLD), 2)
      .addTagLevel(new Price(32, Currency.BRONZE), 3)
      .addTagLevel(new Price(24, Currency.SILVER), 4)
      .addTagLevel(new Price(16, Currency.GOLD), 5)
      .addTagLevel(new Price(64, Currency.BRONZE), 6);

  private static final LevelableProperty.Builder MULTISHOT_LEVELS = new LevelableProperty.Builder()
      .name("Multishot")
      .display(new ExItemStack(Material.TORCH))
      .defaultLevel(1)
      .levelDescription("+1 Bullet")
      .levelDecimalDigit(0)
      .levelLoreLine(4)
      .levelUnit("bullets")
      .levelLoreName("Multishot")
      .levelItem(ITEM)
      .addTagLevel(null, 1)
      .addTagLevel(new Price(32, Currency.BRONZE), 2)
      .addTagLevel(new Price(16, Currency.GOLD), 3);

  public static final UpgradeableGoodItem.Builder WAND = new UpgradeableGoodItem.Builder()
      .name("Wand")
      .display(ITEM.cloneWithId())
      .startItem(ITEM.cloneWithId())
      .addLevelableProperty(SPEED_LEVELS)
      .addLevelableProperty(DAMAGE_LEVELS)
      .addLevelableProperty(FIRE_RATE_LEVELS)
      .addLevelableProperty(MULTISHOT_LEVELS)
      .addLevelableProperty(PIERCING_LEVELS)
      .addConflictToLvlType(MULTISHOT_LEVELS, PIERCING_LEVELS);

  public Wand() {
    super(ITEM);
  }


  @Override
  public void onInteract(ExItemStack wand, MobDefUser user) {
    float speed = SPEED_LEVELS.getValueFromItem(wand);
    float damage = DAMAGE_LEVELS.getValueFromItem(wand);

    int piercing = Objects.requireNonNullElse(PIERCING_LEVELS.getValueFromItem(wand), 0);
    int multiShot = Objects.requireNonNullElse(MULTISHOT_LEVELS.getValueFromItem(wand), 1);

    BulletManager bulletManager = MobDefServer.getWeaponManager().getBulletManager();

    for (int shots = 0; shots < multiShot; shots++) {
      Server.runTaskLaterSynchrony(() -> bulletManager.shootBullet(new WandBullet(user, speed, damage,
              piercing), user.getLocation().getDirection().multiply(speed)), shots * 2,
          GameMobDefence.getPlugin());
    }
  }

  @Override
  public int getCooldown(ExItemStack item) {
    return 1 / FIRE_RATE_LEVELS.<Integer>getValueFromItem(item) * 20;
  }

  private static class WandBullet extends PiercingBullet {

    public WandBullet(User user, double speed, double damage, int piercing) {
      super(user, user.getEyeLocation().add(0, -0.8, 0), TargetFinder.STRAIGHT, speed, damage,
          piercing);
    }

    @Override
    public Entity spawn(Location location) {
      LlamaSpit bullet = location.getWorld().spawn(location, LlamaSpit.class);
      bullet.setShooter(this.shooter.getPlayer());
      bullet.setGravity(false);
      return bullet;

      //LlamaSpit spit = new LlamaSpitBuilder()
      //    .applyOnEntity(e -> {
      //      e.setPos(block.getX(), block.getY(), block.getZ());
      //      e.setOwner(this.shooter.getMinecraftPlayer());
      //    })
      //    .build(((CraftWorld) block.getWorld()).getHandle());
      //return spit.getBukkitEntity();
    }
  }

}
