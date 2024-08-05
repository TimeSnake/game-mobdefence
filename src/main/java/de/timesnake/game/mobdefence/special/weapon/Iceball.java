/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Iceball extends SpecialWeapon implements Listener {

  public static final ExItemStack ITEM = new ExItemStack(Material.SNOWBALL,
      "§6Ice Ball").immutable();
  private static final String NAME = "iceball";

  private static final LevelType.Builder SPEED_LEVELS = new LevelType.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .baseLevel(1)
      .levelDescription("+0.5 Speed")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Speed")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(1, Currency.GOLD), 2.5)
      .addLoreLvl(new Price(14, Currency.BRONZE), 3)
      .addLoreLvl(new Price(16, Currency.SILVER), 3.5)
      .addLoreLvl(new Price(26, Currency.BRONZE), 4)
      .addLoreLvl(new Price(13, Currency.GOLD), 4.5)
      .addLoreLvl(new Price(33, Currency.BRONZE), 5)
      .addLoreLvl(new Price(35, Currency.SILVER), 5.5)
      .addLoreLvl(new Price(64, Currency.BRONZE), 6);
  private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .baseLevel(1)
      .levelDescription("+0.5 ❤")
      .levelDecimalDigit(1)
      .levelUnit("❤")
      .levelLoreLine(2)
      .levelLoreName("Damage")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(2, Currency.GOLD), 2.5)
      .addLoreLvl(new Price(15, Currency.BRONZE), 3)
      .addLoreLvl(new Price(14, Currency.SILVER), 3.5)
      .addLoreLvl(new Price(28, Currency.BRONZE), 4)
      .addLoreLvl(new Price(15, Currency.GOLD), 4.5)
      .addLoreLvl(new Price(35, Currency.BRONZE), 5)
      .addLoreLvl(new Price(36, Currency.SILVER), 5.5)
      .addLoreLvl(new Price(44, Currency.BRONZE), 6)
      .addLoreLvl(new Price(23, Currency.GOLD), 6.5)
      .addLoreLvl(new Price(42, Currency.SILVER), 7)
      .addLoreLvl(new Price(34, Currency.GOLD), 7.5);

  public static final UpgradeableItem.Builder ICEBALL = new UpgradeableItem.Builder()
      .name("Iceball")
      .display(ITEM.cloneWithId())
      .baseItem(ITEM.cloneWithId())
      .addLvlType(SPEED_LEVELS)
      .addLvlType(DAMAGE_LEVELS);

  private final Set<User> cooldownUsers = new HashSet<>();

  public Iceball() {
    super(ITEM);
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onPlayerLaunchProjectile(PlayerLaunchProjectileEvent e) {

    ExItemStack item = new ExItemStack(e.getItemStack());

    if (!item.equals(ITEM)) {
      return;
    }

    e.setCancelled(true);

    User user = Server.getUser(e.getPlayer());

    if (this.cooldownUsers.contains(user)) {
      return;
    }

    double speed = SPEED_LEVELS.getNumberFromLore(item, Double::valueOf);
    double damage = DAMAGE_LEVELS.getNumberFromLore(item, Double::valueOf);

    Snowball snowball = user.getExWorld()
        .spawn(user.getPlayer().getEyeLocation().add(0, -0.2, 0), Snowball.class);

    snowball.setCustomName(NAME + damage);
    snowball.setCustomNameVisible(false);
    snowball.setPersistent(true);
    snowball.setShooter(user.getPlayer());
    snowball.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));

    this.cooldownUsers.add(user);
    Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), 5,
        GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent e) {
    if (!(e.getEntity() instanceof Snowball snowball)) {
      return;
    }

    if (snowball.getCustomName() == null || !snowball.getCustomName().contains(NAME)) {
      return;
    }

    double damage = Double.parseDouble(snowball.getCustomName().replaceAll(NAME, "")) * 2;

    if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity entity) {

      e.setCancelled(true);

      if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
        return;
      }

      entity.damage(damage, snowball);
      entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
    }

    if (e.getHitBlock() == null) {
      return;
    }

    Vector snowballVector = snowball.getVelocity();

    final double magnitude = Math.sqrt(Math.pow(snowballVector.getX(), 2) + Math.pow(snowballVector.getY(), 2)
            + Math.pow(snowballVector.getZ(), 2));

    if (magnitude < 0.2) {

      return;
    }

    Location hitLoc = snowball.getLocation();

    BlockIterator b = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), snowballVector, 0,
        3);

    Block blockBefore = snowball.getLocation().getBlock();
    Block nextBlock = b.next();

    while (b.hasNext() && nextBlock.getType() == Material.AIR) {
      blockBefore = nextBlock;
      nextBlock = b.next();
    }

    BlockFace blockFace = nextBlock.getFace(blockBefore);

    if (blockFace != null) {

      // Convert blockFace SELF to UP:
      if (blockFace == BlockFace.SELF) {
        blockFace = BlockFace.UP;
      }

      Vector hitPlain = new Vector(blockFace.getModX(), blockFace.getModY(),
          blockFace.getModZ());

      double dotProduct = snowballVector.dot(hitPlain);
      Vector u = hitPlain.multiply(dotProduct).multiply(2.0);

      float speed = (float) magnitude;
      speed *= 0.6F;

      Snowball newSnowball = snowball.getWorld().spawn(snowball.getLocation(), Snowball.class);
      newSnowball.setVelocity(snowballVector.subtract(u).normalize().multiply(speed));
      newSnowball.setCustomName(NAME + damage);
      newSnowball.setCustomNameVisible(false);
      newSnowball.setShooter(snowball.getShooter());

      snowball.remove();

    }
  }


}
