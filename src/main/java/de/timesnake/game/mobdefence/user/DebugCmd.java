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
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.library.commands.PluginCommand;
import de.timesnake.library.commands.simple.Arguments;
import de.timesnake.library.extension.util.chat.Code;

public class DebugCmd implements CommandListener {

  private final Code perm = Plugin.MOB_DEFENCE.createPermssionCode("game.mobdef.debug");

  @Override
  public void onCommand(Sender sender, PluginCommand cmd, Arguments<Argument> args) {
    if (!sender.hasPermission(this.perm)) {
      return;
    }

    if (!sender.isPlayer(true)) {
      return;
    }

    User user = sender.getUser();

    if (args.isLengthEquals(0, false)) {
      user.addItem(UserManager.DEBUG_TOOL);
    }

    if (args.isLengthEquals(1, false)) {
      if ("coins".equalsIgnoreCase(args.getString(0))) {
        user.addItem(new Price(64, Currency.BRONZE).asItem(),
            new Price(64, Currency.SILVER).asItem(),
            new Price(64, Currency.GOLD).asItem(),
            new Price(64, Currency.EMERALD).asItem());
      }
    }
  }

  @Override
  public Completion getTabCompletion() {
    return new Completion(this.perm)
        .addArgument(new Completion("coins"));
  }

  @Override
  public String getPermission() {
    return this.perm.getPermission();
  }
}
