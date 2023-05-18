/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;
import java.util.List;

public class MobDefCmd implements CommandListener {

  private Code perm;

  @Override
  public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd,
      Arguments<Argument> args) {
    if (!sender.hasPermission(this.perm)) {
      return;
    }

    MobDefServer.initNextWave();
  }

  @Override
  public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd,
      Arguments<Argument> args) {
    if (args.getLength() == 1) {
      return List.of("nextwave");
    }
    return List.of();
  }

  @Override
  public void loadCodes(Plugin plugin) {
    this.perm = plugin.createPermssionCode("game.mobdef.nextwave");
  }
}
