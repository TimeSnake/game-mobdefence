/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.library.extension.util.chat.Code;
import de.timesnake.library.extension.util.chat.Plugin;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;

import java.util.List;

public class DebugCmd implements CommandListener {

    private Code.Permission perm;

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
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
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return null;
    }

    @Override
    public void loadCodes(Plugin plugin) {
        this.perm = plugin.createPermssionCode("mob", "game.mobdef.debug");
    }
}
