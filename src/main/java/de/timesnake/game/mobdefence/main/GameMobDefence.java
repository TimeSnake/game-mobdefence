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

package de.timesnake.game.mobdefence.main;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.ServerManager;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServerManager;
import de.timesnake.game.mobdefence.user.DebugCmd;
import de.timesnake.game.mobdefence.user.MobDefCmd;
import org.bukkit.plugin.java.JavaPlugin;

public class GameMobDefence extends JavaPlugin {

    private static GameMobDefence plugin;

    @Override
    public void onLoad() {
        ServerManager.setInstance(new MobDefServerManager());
    }

    @Override
    public void onEnable() {
        plugin = this;

        MobDefServerManager.getInstance().onMobGameEnable();

        Server.getCommandManager().addCommand(this, "mobdebug", new DebugCmd(), Plugin.MOB_DEFENCE);
        Server.getCommandManager().addCommand(this, "mobdef", new MobDefCmd(), Plugin.MOB_DEFENCE);
    }

    public static GameMobDefence getPlugin() {
        return plugin;
    }
}
