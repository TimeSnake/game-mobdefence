/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import de.timesnake.library.extension.util.chat.Code;

public class MobDefCmd implements CommandListener {

  private final Code perm = Plugin.MOB_DEFENCE.createPermssionCode("game.mobdef.nextwave");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    sender.hasPermissionElseExit(this.perm);

    MobDefServer.initNextWave();
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion("nextwave"));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
