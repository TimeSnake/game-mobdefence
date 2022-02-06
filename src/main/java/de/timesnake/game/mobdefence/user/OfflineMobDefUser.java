package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.basic.loungebridge.util.user.OfflineUser;
import de.timesnake.game.mobdefence.kit.KitShop;

public class OfflineMobDefUser extends OfflineUser {

    private final boolean alive;
    private final KitShop shop;

    public OfflineMobDefUser(MobDefUser user) {
        super(user);
        this.alive = user.isAlive();
        this.shop = user.getShop();
    }

    @Override
    public void loadInto(GameUser user) {
        super.loadInto(user);
        ((MobDefUser) user).loadKit();

        ((MobDefUser) user).setShop(this.shop);
        ((MobDefUser) user).setAlive(this.alive);

        ((MobDefUser) user).loadGameSideboard();
    }
}
