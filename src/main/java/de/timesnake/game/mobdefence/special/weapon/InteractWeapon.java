/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.user.MobDefUser;

public abstract class InteractWeapon extends SpecialWeapon implements UserInventoryInteractListener {

    public InteractWeapon(ExItemStack item) {
        super(item);
        Server.getInventoryEventManager().addInteractListener(this, item);
    }

    public abstract void onInteract(ExItemStack item, MobDefUser user);

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        event.setCancelled(true);
        this.onInteract(event.getClickedItem(), ((MobDefUser) event.getUser()));
    }
}
