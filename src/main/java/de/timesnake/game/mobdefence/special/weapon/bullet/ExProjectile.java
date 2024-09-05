/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon.bullet;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.UserSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public abstract class ExProjectile implements Listener {

  private final String name;

  public ExProjectile(String name) {
    this.name = name;
    //Server.registerListener(this, GameMobDefence.getPlugin());
  }

  private final UserSet<MobDefUser> cooldownUsers = new UserSet<>();

  public void launch(MobDefUser user, ExItemStack item) {
    if (this.cooldownUsers.contains(user)) {
      return;
    }

    double speed = this.getSpeed(item);
    double damage = this.getDamage(item);

    Entity entity = this.spawnProjectile(user, item);

    entity.setCustomName(name + ";" + damage);
    entity.setCustomNameVisible(false);
    entity.setPersistent(true);
    entity.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));

    if (entity instanceof Projectile projectile) {
      projectile.setShooter(user.getPlayer());
    }

    this.cooldownUsers.add(user);
    Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), this.getCooldown(item),
        GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent e) {
    if (!(e.getEntity() instanceof Snowball snowball)) {
      return;
    }

    if (snowball.getCustomName() == null || !snowball.getCustomName().contains(this.name)) {
      return;
    }

    double damage = Double.parseDouble(snowball.getCustomName().replaceAll(this.name, "")) * 2;

    if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity entity) {

      e.setCancelled(true);

      if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
        return;
      }

      entity.damage(damage, snowball);
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
      newSnowball.setCustomName(this.name + damage);
      newSnowball.setCustomNameVisible(false);
      newSnowball.setShooter(snowball.getShooter());

      snowball.remove();

    }
  }

  public abstract Entity spawnProjectile(MobDefUser user, ExItemStack item);

  public abstract double getSpeed(ExItemStack item);

  public abstract double getDamage(ExItemStack item);

  protected abstract int getCooldown(ExItemStack item);
}
