/*
 * game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
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
