/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.user.MobDefUser;
import java.util.HashSet;
import java.util.Set;

public abstract class CooldownWeapon extends InteractWeapon {

    private final Set<MobDefUser> cooldownUsers = new HashSet<>();

    public CooldownWeapon(ExItemStack item) {
        super(item);
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        MobDefUser user = ((MobDefUser) event.getUser());

        if (this.cooldownUsers.contains(user)) {
            event.setCancelled(true);
            return;
        }

        this.cooldownUsers.add(user);

        Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user),
                this.getCooldown(event.getClickedItem()),
                GameMobDefence.getPlugin());

        super.onUserInventoryInteract(event);
    }

    public abstract int getCooldown(ExItemStack item);

    public void cancelAll() {
        this.cooldownUsers.clear();
    }
}
