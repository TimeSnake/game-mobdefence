/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;

public class PotionGenerator implements Listener {


  public static final ExItemStack HEALING = ExItemStack.getPotion(Material.SPLASH_POTION,
      PotionType.HEALING,
      false, false).setSlot(1).asQuantity(2);

  private BukkitTask task;

  public PotionGenerator() {
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  public void run() {
    this.task = Server.runTaskTimerAsynchrony(() -> {
      for (User user : Server.getInGameUsers()) {
        if (user instanceof MobDefUser && ((MobDefUser) user).isAlive()
            && ((MobDefUser) user).getKit().equals(MobDefKit.WIZARD)) {
          this.fillItem(((MobDefUser) user), HEALING, 8);
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

    ItemStack baseSlotItem = user.getInventory().getItem(HEALING.getSlot());

    if (baseSlotItem != null && new ExItemStack(baseSlotItem).equals(HEALING)) {
      user.setItem(HEALING.getSlot(),
          HEALING.cloneWithId().asQuantity(
              baseSlotItem.getAmount() + Math.min(max - baseSlotItem.getAmount(),
                  HEALING.getAmount())));
      user.updateInventory();
      return;
    }

    for (int slot = 0; slot < user.getInventory().getSize(); slot++) {
      ItemStack slotItem = user.getInventory().getItem(slot);

      if (slotItem != null && new ExItemStack(slotItem).equals(HEALING)) {
        user.setItem(slot,
            HEALING.cloneWithId().asQuantity(
                slotItem.getAmount() + Math.min(max - slotItem.getAmount(),
                    HEALING.getAmount())));
        user.updateInventory();
        return;
      }
    }

    user.addItem(HEALING.cloneWithId().asQuantity(HEALING.getAmount()));
  }

}
