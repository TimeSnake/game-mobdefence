/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class KitShopManager implements Listener {

    public KitShopManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getUniqueId().equals(MobDefServer.getCoreEntity().getUniqueId())) {
            event.setCancelled(true);
            MobDefUser user = (MobDefUser) Server.getUser(event.getPlayer());
            if (user.getKit() == null) {
                return;
            }

            KitShop shop = user.getShop();

            if (shop != null) {
                user.openInventory(shop.getInventory());
            }
        }
    }
}
