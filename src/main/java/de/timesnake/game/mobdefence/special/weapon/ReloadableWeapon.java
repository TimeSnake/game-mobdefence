/*
 * workspace.game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
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

        this.reloadableItemByUser.put(user, new ReloadableItem(builder) {
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
        });
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
