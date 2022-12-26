/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class ResistanceAura implements Listener {

    private static final double RADIUS = 5;


    private BukkitTask task;

    public void run() {
        this.task = Server.runTaskTimerSynchrony(() -> {
            for (User user : Server.getInGameUsers()) {
                if (((MobDefUser) user).isAlive() && ((MobDefUser) user).getKit().equals(MobDefKit.KNIGHT)) {
                    for (Player player : user.getWorld().getNearbyPlayers(user.getLocation(), RADIUS)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 1));
                    }
                    user.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 4 * 20, 1));
                }
            }
        }, 0, 20 * 3, GameMobDefence.getPlugin());
    }

    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
        }
    }
}
