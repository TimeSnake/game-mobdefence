/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.weapon.SpecialWeapon;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public abstract class BlockSpawner extends SpecialWeapon implements Listener {

  protected final EntityType entityType;


  public BlockSpawner(EntityType type, ExItemStack item) {
    super(item);
    this.entityType = type;
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  public abstract void spawnEntities(Location location);

  @EventHandler
  public void onBlockPlace(UserBlockPlaceEvent e) {
    User user = e.getUser();

    ExItemStack item = new ExItemStack(e.getItemInHand()).cloneWithId();

    if (!item.equals(this.item)) {
      return;
    }

    this.spawnEntities(e.getBlock().getLocation().add(0.5, 0, 0.5));

    if (item.getAmount() > 1) {
      user.setItem(e.getHand(), item.asQuantity(item.getAmount() - 1));
    } else {
      user.getInventory().setItem(e.getHand(), null);
    }
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Projectile
        && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
      return;
    }

    if (!(e.getEntity().getType().equals(this.entityType))) {
      return;
    }

    e.setCancelled(true);
    e.setDamage(0);

  }
}
