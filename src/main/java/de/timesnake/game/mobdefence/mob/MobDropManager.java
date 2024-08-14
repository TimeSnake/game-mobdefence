/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDropManager implements Listener {

  public MobDropManager() {
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onItemDrop(EntityDeathEvent e) {
    e.setDroppedExp(0);
    e.getDrops().clear();

    if (MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
      if (e.getEntityType().equals(EntityType.ENDERMITE) || e.getEntityType()
          .equals(EntityType.SILVERFISH)) {
        return;
      }

      if (Math.random() < MobDefServer.EMERALD_CHANCE / MobDefServer.getPlayerAmount()) {
        e.getDrops().add(new Price(1, Currency.EMERALD).asItem());
      }
      if (Math.random() < MobDefServer.GOLD_CHANCE) {
        e.getDrops().add(new Price(1, Currency.GOLD).asItem());
      }
      if (Math.random() < MobDefServer.SILVER_CHANCE) {
        e.getDrops().add(new Price(1, Currency.SILVER).asItem());
      }
      if (Math.random() < MobDefServer.BRONZE_CHANCE) {
        e.getDrops().add(new Price(1, Currency.BRONZE).asItem());
      }
    }
  }
}
