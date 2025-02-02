/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.chat.cmd.Argument;
import de.timesnake.basic.bukkit.util.chat.cmd.CommandListener;
import de.timesnake.basic.bukkit.util.chat.cmd.Completion;
import de.timesnake.basic.bukkit.util.chat.cmd.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.mob.map.HeightMapVisualizer;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.library.chat.Code;
import de.timesnake.library.chat.Plugin;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;

import java.util.Arrays;

public class DebugCmd implements CommandListener {

  private final Code perm = Plugin.GAME.createPermssionCode("game.mobdef.debug");

  private final HeightMapVisualizer visualizer = new HeightMapVisualizer();

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    sender.hasPermissionElseExit(this.perm);
    sender.isPlayerElseExit(true);

    User user = sender.getUser();

    if (args.isLengthEquals(0, false)) {

      return;
    }

    if (args.isLengthHigherEquals(1, false)) {
      if (args.get(0).equalsIgnoreCase("coins")) {
        user.addItem(new Price(64, Currency.BRONZE).asItem(),
            new Price(64, Currency.SILVER).asItem(),
            new Price(64, Currency.GOLD).asItem(),
            new Price(32, Currency.EMERALD).asItem());
      } else if (args.get(0).equalsIgnoreCase("heightMap")) {
        args.isLengthEqualsElseExit(2, true);

        HeightMapManager.MapType type;
        try {
          type = HeightMapManager.MapType.valueOf(args.getString(1).toUpperCase());
        } catch (IllegalArgumentException e) {
          sender.sendPluginTDMessage("§wHeight map type §v" + args.getString(1) + "§w does not exist");
          return;
        }

        sender.sendPluginTDMessage("§sVisualizing...");
        this.visualizer.clear();
        this.visualizer.visualize(MobDefServer.getMap().getHeightMapManager().getMap(type));
        sender.sendPluginTDMessage("§sDone");
      } else if (args.get(0).equalsIgnoreCase("path_tool")) {
        args.isLengthEqualsElseExit(2, true);

        HeightMapManager.MapType type;
        try {
          type = HeightMapManager.MapType.valueOf(args.getString(1).toUpperCase());
        } catch (IllegalArgumentException e) {
          sender.sendPluginTDMessage("§wHeight map type §v" + args.getString(1) + "§w does not exist");
          return;
        }

        user.addItem(UserManager.DEBUG_TOOL.cloneWithId().asQuantity(type.ordinal() + 1));
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion("coins"))
        .addArgument(new Completion("heightMap", "path_tool")
            .addArgument(new Completion(Arrays.stream(HeightMapManager.MapType.values()).map(HeightMapManager.MapType::name))));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
