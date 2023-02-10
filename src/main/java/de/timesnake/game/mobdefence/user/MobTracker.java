/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.basic.util.chat.ExTextColor;
import java.util.Collection;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class MobTracker implements UserInventoryInteractListener {

    public static final ExItemStack TRACKER = new ExItemStack(Material.COMPASS,
            "§6Mob Tracker").setSlot(8);

    public MobTracker() {
        Server.getInventoryEventManager().addInteractListener(this, TRACKER);
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        Collection<Entity> mobs = MobDefServer.getMobManager().getAliveMobs();

        user.getPlayer().setCompassTarget(user.getLocation());

        if (mobs.isEmpty()) {
            user.sendActionBarText(Component.text("No mobs alive", ExTextColor.WARNING));
            return;
        }

        if (mobs.size() > 3) {
            user.sendActionBarText(Component.text("Too many mobs to track", ExTextColor.WARNING));
            return;
        }

        Entity nearestEntity = null;
        double distance = 100;

        for (Entity entity : mobs) {
            double entityDistanceSquared = entity.getLocation().distanceSquared(user.getLocation());
            if (entityDistanceSquared < distance * distance) {
                nearestEntity = entity;
                distance = Math.sqrt(entityDistanceSquared);
            }
        }

        if (nearestEntity == null) {
            user.sendActionBarText(Component.text("No mob nearby", ExTextColor.WARNING));
            return;
        }

        user.getPlayer().setCompassTarget(nearestEntity.getLocation());
        user.sendActionBarText(Component.text("Nearest mob: ", ExTextColor.PERSONAL)
                .append(Component.text((int) distance, ExTextColor.VALUE))
                .append(Component.text(" blocks", ExTextColor.PERSONAL)));
    }
}
