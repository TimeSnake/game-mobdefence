/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.mob.map.HeightMapVisualizer;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.library.chat.Code;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;

public class DebugCmd implements CommandListener {

  private final Code perm = Plugin.MOB_DEFENCE.createPermssionCode("game.mobdef.debug");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    sender.hasPermissionElseExit(this.perm);
    sender.isPlayerElseExit(true);

    User user = sender.getUser();

    if (args.isLengthEquals(0, false)) {
      user.addItem(UserManager.DEBUG_TOOL);
      return;
    }

    if (args.isLengthEquals(1, false)) {
      if (args.get(0).equalsIgnoreCase("coins")) {
        user.addItem(new Price(64, Currency.BRONZE).asItem(),
            new Price(64, Currency.SILVER).asItem(),
            new Price(64, Currency.GOLD).asItem(),
            new Price(32, Currency.EMERALD).asItem());
      } else if (args.get(0).equalsIgnoreCase("heightMap")) {
        sender.sendPluginTDMessage("§sVisualizing...");
        new HeightMapVisualizer(MobDefServer.getMap().getHeightMapManager().getMap(HeightMapManager.MapType.NORMAL))
            .visualize();
        sender.sendPluginTDMessage("§sDone");
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion("coins", "heightMap"));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
