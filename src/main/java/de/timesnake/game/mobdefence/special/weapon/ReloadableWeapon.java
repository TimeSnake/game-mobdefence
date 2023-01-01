/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.ReloadableItem;
import de.timesnake.basic.bukkit.util.user.ReloadableItem.Builder;
import de.timesnake.game.mobdefence.user.MobDefUser;
import java.util.HashMap;

public abstract class ReloadableWeapon extends CooldownWeapon {

    protected final HashMap<MobDefUser, ReloadableItem> reloadableItemByUser = new HashMap<>();

    public ReloadableWeapon(ExItemStack item) {
        super(item);
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        ReloadableItem.Builder builder = new Builder()
                .user(user)
                .item(item)
                .showActionBarText()
                .damageItem()
                .delay(this.getCooldown(item));

        ReloadableItem reloadableItem = new ReloadableItem(builder) {
            @Override
            public void reloaded() {
                super.reloaded();
                ReloadableWeapon.this.reloaded(((MobDefUser) this.user), this.item);
                ReloadableWeapon.this.reloadableItemByUser.remove(((MobDefUser) this.user));
            }

            @Override
            public void update() {
                super.update();
                ReloadableWeapon.this.update((MobDefUser) this.user, this.item,
                        this.currentDelaySec);
            }
        };

        reloadableItem.reload();

        this.reloadableItemByUser.put(user, reloadableItem);
    }

    public void update(MobDefUser user, ExItemStack item, int delay) {

    }

    public void reloaded(MobDefUser user, ExItemStack item) {

    }

    @Override
    public void cancelAll() {
        super.cancelAll();
        this.reloadableItemByUser.values().forEach(ReloadableItem::cancel);
        this.reloadableItemByUser.clear();
    }

}
