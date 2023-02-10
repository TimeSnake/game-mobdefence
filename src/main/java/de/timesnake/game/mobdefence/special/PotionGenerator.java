/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;

public class PotionGenerator implements Listener {


    public static final ExItemStack INSTANT_HEAL = ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.INSTANT_HEAL,
            false, false).setSlot(1).asQuantity(2);

    private BukkitTask task;

    public PotionGenerator() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    public void run() {
        this.task = Server.runTaskTimerAsynchrony(() -> {
            for (User user : Server.getInGameUsers()) {
                if (user instanceof MobDefUser && ((MobDefUser) user).isAlive() && ((MobDefUser) user).getKit().equals(MobDefKit.WIZARD)) {
                    this.fillItem(((MobDefUser) user), INSTANT_HEAL, 8);
                }
            }
        }, 0, 5 * 20, GameMobDefence.getPlugin());
    }

    public void cancel() {
        if (this.task != null) {
            this.task.cancel();
        }
    }

    private void fillItem(MobDefUser user, ExItemStack item, int max) {
        if (user.containsAtLeast(item, max, true) >= 0) {
            return;
        }

        ItemStack baseSlotItem = user.getInventory().getItem(INSTANT_HEAL.getSlot());

        if (baseSlotItem != null && new ExItemStack(baseSlotItem).equals(INSTANT_HEAL)) {
            user.setItem(INSTANT_HEAL.getSlot(),
                    INSTANT_HEAL.cloneWithId().asQuantity(baseSlotItem.getAmount() + Math.min(max - baseSlotItem.getAmount(), INSTANT_HEAL.getAmount())));
            user.updateInventory();
            return;
        }

        for (int slot = 0; slot < user.getInventory().getSize(); slot++) {
            ItemStack slotItem = user.getInventory().getItem(slot);

            if (slotItem != null && new ExItemStack(slotItem).equals(INSTANT_HEAL)) {
                user.setItem(slot,
                        INSTANT_HEAL.cloneWithId().asQuantity(slotItem.getAmount() + Math.min(max - slotItem.getAmount(), INSTANT_HEAL.getAmount())));
                user.updateInventory();
                return;
            }
        }

        user.addItem(INSTANT_HEAL.cloneWithId().asQuantity(INSTANT_HEAL.getAmount()));
    }

}
