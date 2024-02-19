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
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.game.mobdefence.special.weapon.bullet.BulletManager;
import de.timesnake.game.mobdefence.special.weapon.bullet.PiercingBullet;
import de.timesnake.game.mobdefence.special.weapon.bullet.TargetFinder;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ShulkerBullet;

public class Wand extends CooldownWeapon {

  private static final ExItemStack ITEM = new ExItemStack(Material.STICK, "§3Wand").enchant()
      .immutable();

  private static final LevelType.Builder SPEED_LEVELS = new LevelType.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .baseLevel(1)
      .levelDescription("+0.4 Speed")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Speed")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(4, Currency.BRONZE), 2.4)
      .addLoreLvl(new Price(7, Currency.SILVER), 2.8)
      .addLoreLvl(new Price(9, Currency.GOLD), 3.2);

  private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .baseLevel(1)
      .levelDescription("+0.5 ❤")
      .levelUnit("❤")
      .levelDecimalDigit(1)
      .levelLoreLine(2)
      .levelLoreName("Damage")
      .levelItem(ITEM)
      .addLoreLvl(null, 1.5)
      .addLoreLvl(new Price(5, Currency.BRONZE), 2)
      .addLoreLvl(new Price(5, Currency.SILVER), 2.5)
      .addLoreLvl(new Price(5, Currency.GOLD), 3)
      .addLoreLvl(new Price(10, Currency.GOLD), 3.5)
      .addLoreLvl(new Price(16, Currency.SILVER), 4)
      .addLoreLvl(new Price(16, Currency.GOLD), 4.5)
      .addLoreLvl(new Price(32, Currency.SILVER), 5)
      .addLoreLvl(new Price(64, Currency.BRONZE), 5.5)
      .addLoreLvl(new Price(47, Currency.SILVER), 6);

  private static final LevelType.Builder FIRE_RATE_LEVELS = new LevelType.Builder()
      .name("Fire Rate")
      .display(new ExItemStack(Material.FIRE_CHARGE))
      .baseLevel(1)
      .levelDescription("+1 per sec.")
      .levelDecimalDigit(0)
      .levelUnit("per sec.")
      .levelLoreLine(3)
      .levelLoreName("Fire Rate")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(6, Currency.BRONZE), 3)
      .addLoreLvl(new Price(6, Currency.SILVER), 4)
      .addLoreLvl(new Price(6, Currency.GOLD), 5)
      .addLoreLvl(new Price(64, Currency.BRONZE), 6);

  private static final LevelType.Builder PIERCING_LEVELS = new LevelType.Builder()
      .name("Piercing")
      .display(new ExItemStack(Material.SOUL_TORCH))
      .baseLevel(0)
      .levelDescription("+1 Mob")
      .levelDecimalDigit(0)
      .levelLoreLine(4)
      .levelUnit("mobs")
      .levelLoreName("Piercing")
      .levelItem(ITEM)
      .addLoreLvl(new Price(10, Currency.SILVER), 1)
      .addLoreLvl(new Price(11, Currency.GOLD), 2)
      .addLoreLvl(new Price(32, Currency.BRONZE), 3)
      .addLoreLvl(new Price(24, Currency.SILVER), 4)
      .addLoreLvl(new Price(16, Currency.GOLD), 5)
      .addLoreLvl(new Price(64, Currency.BRONZE), 6);

  private static final LevelType.Builder MULTISHOT_LEVELS = new LevelType.Builder()
      .name("Multishot")
      .display(new ExItemStack(Material.TORCH))
      .baseLevel(1)
      .levelDescription("+1 Bullet")
      .levelDecimalDigit(0)
      .levelLoreLine(4)
      .levelUnit("bullets")
      .levelLoreName("Multishot")
      .levelItem(ITEM)
      .addLoreLvl(null, 1)
      .addLoreLvl(new Price(32, Currency.BRONZE), 2)
      .addLoreLvl(new Price(16, Currency.GOLD), 3);

  public static final UpgradeableItem.Builder WAND = new UpgradeableItem.Builder()
      .name("Wand")
      .display(ITEM.cloneWithId())
      .baseItem(ITEM.cloneWithId())
      .addLvlType(SPEED_LEVELS)
      .addLvlType(DAMAGE_LEVELS)
      .addLvlType(FIRE_RATE_LEVELS)
      .addLvlType(MULTISHOT_LEVELS)
      .addLvlType(PIERCING_LEVELS)
      .addConflictToLvlType(MULTISHOT_LEVELS, PIERCING_LEVELS);

  public Wand() {
    super(ITEM);
  }


  @Override
  public void onInteract(ExItemStack wand, MobDefUser user) {
    double speed = SPEED_LEVELS.getNumberFromLore(wand, Double::valueOf);
    double damage = DAMAGE_LEVELS.getNumberFromLore(wand, Double::valueOf);

    int piercing = 0;
    String piercingString = PIERCING_LEVELS.getNumberFromLore(wand);
    if (piercingString != null) {
      try {
        piercing = Integer.parseInt(piercingString);
      } catch (NumberFormatException ignored) {
      }
    }

    int multiShot = 1;
    String multiShotString = MULTISHOT_LEVELS.getNumberFromLore(wand);
    if (multiShotString != null) {
      try {
        multiShot = Integer.parseInt(multiShotString);
      } catch (NumberFormatException ignored) {
      }
    }

    BulletManager bulletManager = MobDefServer.getWeaponManager().getBulletManager();

    for (int shots = 0; shots < multiShot; shots++) {
      int finalPiercing = piercing;
      Server.runTaskLaterSynchrony(
          () -> bulletManager.shootBullet(new WandBullet(user, speed, damage,
              finalPiercing), user.getLocation().getDirection().multiply(speed)), shots * 2,
          GameMobDefence.getPlugin());

    }
  }

  @Override
  public int getCooldown(ExItemStack item) {
    return (int) (1 / FIRE_RATE_LEVELS.getNumberFromLore(item, Double::valueOf) * 20);
  }

  private static class WandBullet extends PiercingBullet {

    public WandBullet(User user, double speed, double damage, int piercing) {
      super(user, user.getEyeLocation().add(0, -0.5, 0), TargetFinder.STRAIGHT, speed, damage,
          piercing);
    }

    @Override
    public Entity spawn(Location location) {
      ShulkerBullet bullet = location.getWorld().spawn(location, ShulkerBullet.class);
      bullet.setShooter(this.shooter.getPlayer());
      return bullet;
    }
  }

}
