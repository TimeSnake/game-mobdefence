/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class CoreRegeneration {

    public static final ExItemStack ITEM = new ExItemStack(Material.BEACON, "Villager Regeneration", "Gives players " +
            "regeneration, within 7 blocks.");
    public static final double RADIUS = 7;

    private BukkitTask task;

    public CoreRegeneration() {
    }

    public void run(MobDefUser userActivated) {
        MobDefServer.broadcastGameMessage(userActivated.getChatNameComponent()
                .append(Component.text(" enabled villager regeneration", ExTextColor.WARNING)));

        this.task = Server.runTaskTimerSynchrony(() -> {
            Location coreLoc = MobDefServer.getMap().getCoreLocation();
            for (User user : Server.getInGameUsers()) {
                if (coreLoc.distanceSquared(user.getExLocation()) <= RADIUS * RADIUS) {
                    user.addPotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1);
                }
            }
        }, 0, 20 * 3, GameMobDefence.getPlugin());
    }

    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }

}
