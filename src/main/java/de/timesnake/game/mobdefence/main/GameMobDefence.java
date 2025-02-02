/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.main;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.ServerManager;
import de.timesnake.game.mobdefence.server.MobDefServerManager;
import de.timesnake.game.mobdefence.user.DebugCmd;
import de.timesnake.game.mobdefence.user.MobDefCmd;
import de.timesnake.library.chat.Plugin;
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

    Server.getCommandManager().addCommand(this, "mobdebug", new DebugCmd(), Plugin.GAME);
    Server.getCommandManager().addCommand(this, "mobdef", new MobDefCmd(), Plugin.GAME);
  }

  public static GameMobDefence getPlugin() {
    return plugin;
  }
}
