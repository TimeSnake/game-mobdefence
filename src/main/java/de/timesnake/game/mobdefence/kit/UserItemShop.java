package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.game.mobdefence.user.MobDefUser;

public class UserItemShop extends ItemShop {

    private MobDefUser user;

    public UserItemShop(MobDefUser user, ItemShop shop) {
        super(shop);
        this.user = user;
    }

    public void setUser(MobDefUser user) {
        this.user = user;
    }

    @Override
    public void onUserInventoryClick(UserInventoryClickEvent event) {
        if (!user.equals(event.getUser())) {
            return;
        }

        super.onUserInventoryClick(event);
    }
}
