package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.List;

public class TeamItemShop extends ItemShop {

    public TeamItemShop(String name, int shopSlot, ExItemStack displayItem, List<Levelable<?>> levelItems, List<ShopTrade>... trades) {
        super(name, shopSlot, displayItem, levelItems, trades);
    }

    public TeamItemShop(TeamItemShop shop) {
        super(shop);
    }

    @Override
    public TeamItemShop clone(MobDefUser user) {
        return MobDefServer.getBaseShops().getShop(this.name);
    }

    @Override
    public TeamItemShop clone() {
        return new TeamItemShop(this);
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        super.onUserInventoryClick(event);

        for (User user : Server.getInGameUsers()) {
            user.updateInventory();
        }
    }
}
