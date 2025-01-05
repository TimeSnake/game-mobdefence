/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Iceball extends SpecialWeapon implements Listener {

  public static final ExItemStack ITEM = new ExItemStack(Material.SNOWBALL)
      .setDisplayName("§6Ice Ball")
      .immutable();
  private static final String NAME = "iceball";

  private static final NamespacedKey NAME_KEY = NamespacedKey.fromString("iceball:name");
  private static final NamespacedKey DAMAGE_KEY = NamespacedKey.fromString("iceball:damage");

  private static final LevelableProperty.Builder SPEED_LEVELS = new LevelableProperty.Builder()
      .name("Speed")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelDescription("+0.5 Speed")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Speed")
      .addTagLevel(null, 2f)
      .addTagLevel(new Price(1, Currency.GOLD), 2.5f)
      .addTagLevel(new Price(14, Currency.BRONZE), 3f)
      .addTagLevel(new Price(16, Currency.SILVER), 3.5f)
      .addTagLevel(new Price(26, Currency.BRONZE), 4f)
      .addTagLevel(new Price(13, Currency.GOLD), 4.5f)
      .addTagLevel(new Price(33, Currency.BRONZE), 5f)
      .addTagLevel(new Price(35, Currency.SILVER), 5.5f)
      .addTagLevel(new Price(64, Currency.BRONZE), 6f);
  private static final LevelableProperty.Builder DAMAGE_LEVELS = new LevelableProperty.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(1)
      .levelDescription("+0.5 ❤")
      .levelDecimalDigit(1)
      .levelUnit("❤")
      .levelLoreLine(2)
      .levelLoreName("Damage")
      .addTagLevel(null, 2f)
      .addTagLevel(new Price(2, Currency.GOLD), 2.5f)
      .addTagLevel(new Price(12, Currency.BRONZE), 3f)
      .addTagLevel(new Price(11, Currency.SILVER), 3.5f)
      .addTagLevel(new Price(22, Currency.BRONZE), 4f)
      .addTagLevel(new Price(9, Currency.GOLD), 4.5f)
      .addTagLevel(new Price(32, Currency.BRONZE), 5f)
      .addTagLevel(new Price(32, Currency.SILVER), 5.5f)
      .addTagLevel(new Price(44, Currency.BRONZE), 6f)
      .addTagLevel(new Price(14, Currency.GOLD), 6.5f)
      .addTagLevel(new Price(42, Currency.SILVER), 7f)
      .addTagLevel(new Price(64, Currency.BRONZE), 7.5f);

  public static final UpgradeableGoodItem.Builder ICEBALL = new UpgradeableGoodItem.Builder()
      .name("Iceball")
      .display(ITEM.cloneWithId())
      .startItem(ITEM.cloneWithId())
      .addLevelableProperty(SPEED_LEVELS)
      .addLevelableProperty(DAMAGE_LEVELS);

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

    float speed = SPEED_LEVELS.getValueFromItem(item);
    float damage = DAMAGE_LEVELS.getValueFromItem(item);

    Snowball snowball = user.getExWorld().spawn(user.getPlayer().getEyeLocation().add(0, -0.2, 0), Snowball.class);
    snowball.setPersistent(true);
    snowball.setShooter(user.getPlayer());
    snowball.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));

    snowball.getPersistentDataContainer().set(NAME_KEY, PersistentDataType.STRING, NAME);
    snowball.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.FLOAT, damage);

    this.cooldownUsers.add(user);
    Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), 5,
        GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent e) {
    if (!(e.getEntity() instanceof Snowball snowball)) {
      return;
    }

    if (!NAME.equals(snowball.getPersistentDataContainer().get(NAME_KEY, PersistentDataType.STRING))) {
      return;
    }

    float damage = snowball.getPersistentDataContainer().get(DAMAGE_KEY, PersistentDataType.FLOAT);

    if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity entity) {
      e.setCancelled(true);

      if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
        return;
      }

      entity.damage(damage * 2, snowball);
      entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 1));
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

    BlockIterator b = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), snowballVector, 0, 3);

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

      Vector hitPlain = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

      double dotProduct = snowballVector.dot(hitPlain);
      Vector u = hitPlain.multiply(dotProduct).multiply(2.0);

      float speed = (float) magnitude;
      speed *= 0.6F;

      Snowball newSnowball = snowball.getWorld().spawn(snowball.getLocation(), Snowball.class);
      newSnowball.setVelocity(snowballVector.subtract(u).normalize().multiply(speed));
      newSnowball.setShooter(snowball.getShooter());

      newSnowball.getPersistentDataContainer().set(NAME_KEY, PersistentDataType.STRING, NAME);
      newSnowball.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.FLOAT, damage);

      snowball.remove();
    }
  }


}
