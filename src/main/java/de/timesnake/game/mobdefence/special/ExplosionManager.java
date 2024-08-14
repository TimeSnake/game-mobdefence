/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionManager implements Listener {

  public ExplosionManager() {
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent e) {
    if (e.getEntityType().equals(EntityType.CREEPER)) {
      e.setYield(0);
      e.blockList().removeIf(block -> block.getY() < e.getLocation().getY() || !MobDefServer.EXPLODEABLE.contains(block.getType()));
      MobDefServer.getMap().getHeightMapManager().updateMaps();
    }
  }

  @EventHandler
  public void onBlockDrop(BlockDropItemEvent e) {
    Material type = e.getBlock().getType();

    if (MobDefServer.BREAKABLE_MATERIALS.contains(type) || e.getBlock().isEmpty() || type.equals(Material.FIRE)) {
      return;
    }

    e.setCancelled(true);
  }
}
