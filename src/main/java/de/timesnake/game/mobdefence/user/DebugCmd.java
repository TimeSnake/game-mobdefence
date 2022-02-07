package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.chat.Argument;
import de.timesnake.basic.bukkit.util.chat.CommandListener;
import de.timesnake.basic.bukkit.util.chat.Sender;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.library.extension.util.cmd.Arguments;
import de.timesnake.library.extension.util.cmd.ExCommand;

import java.util.List;

public class DebugCmd implements CommandListener {

    @Override
    public void onCommand(Sender sender, ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        if (!sender.hasPermission("game.mobdef.debug", 2411)) {
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
                user.addItem(new ShopPrice(64, ShopCurrency.BRONZE).asItem(), new ShopPrice(64, ShopCurrency.SILVER).asItem(), new ShopPrice(64, ShopCurrency.GOLD).asItem(), new ShopPrice(64, ShopCurrency.EMERALD).asItem());
            }
        }
    }

    @Override
    public List<String> getTabCompletion(ExCommand<Sender, Argument> cmd, Arguments<Argument> args) {
        return null;
    }
}
