package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.basic.util.chat.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class MobTracker implements UserInventoryInteractListener {

    public static final ExItemStack TRACKER = new ExItemStack(Material.COMPASS, "§6Mob Tracker").setSlot(8);

    public MobTracker() {
        Server.getInventoryEventManager().addInteractListener(this, TRACKER);
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        Collection<Entity> mobs = MobDefServer.getMobManager().getAliveMobs();

        user.getPlayer().setCompassTarget(user.getLocation());

        if (mobs.isEmpty()) {
            user.sendActionBarText(ChatColor.WARNING + "No mobs alive");
            return;
        }

        if (mobs.size() > 3) {
            user.sendActionBarText(ChatColor.WARNING + "Too many mobs to track");
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
            user.sendActionBarText(ChatColor.WARNING + "No mob nearby");
            return;
        }

        user.getPlayer().setCompassTarget(nearestEntity.getLocation());
        user.sendActionBarText(ChatColor.GOLD + "Nearest mob: " + ChatColor.WHITE + ((int) distance) + ChatColor.GOLD + " blocks");
    }
}
