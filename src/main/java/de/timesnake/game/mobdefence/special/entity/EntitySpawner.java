/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.weapon.SpecialWeapon;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.extension.Entity;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public abstract class EntitySpawner extends SpecialWeapon implements UserInventoryInteractListener {

  private final int cooldown;
  private final Set<User> cooldownUsers = new HashSet<>();

  public EntitySpawner(ExItemStack item, int cooldown) {
    super(item);
    this.cooldown = cooldown;
    Server.getInventoryEventManager().addInteractListener(this, this.getItem());
  }

  @Override
  public void onUserInventoryInteract(UserInventoryInteractEvent event) {

    User user = event.getUser();

    if (this.cooldownUsers.contains(user)) {
      user.sendActionBarText(Component.text("Please wait", ExTextColor.WARNING));
      return;
    }

    List<? extends Entity> entities = this.getEntities(user, event.getClickedItem());

    Location loc = user.getLocation();

    for (Entity entity : entities) {
      entity.getExtension().setPosition(loc.getX(), loc.getY(), loc.getZ());
      EntityManager.spawnEntity(user.getExWorld().getBukkitWorld(), entity);
    }

    this.cooldownUsers.add(user);

    Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), this.cooldown,
        GameMobDefence.getPlugin());
  }

  public abstract List<? extends Entity> getEntities(User user, ExItemStack item);
}
