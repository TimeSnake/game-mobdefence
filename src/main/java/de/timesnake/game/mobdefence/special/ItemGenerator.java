/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ItemGenerator {

  private BukkitTask task;

  private final MobDefKit kit;
  private final int rate;
  private final ItemStack item;
  private final int maxItems;

  public ItemGenerator(MobDefKit kit, int rate, ItemStack item, int maxItems) {
    this.kit = kit;
    this.rate = rate;
    this.item = item;
    this.maxItems = maxItems;
  }

  public void run() {
    this.task = Server.runTaskTimerAsynchrony(() -> {
      if (MobDefServer.isGameRunning()) {
        for (User user : Server.getGameNotServiceUsers()) {
          if (user instanceof MobDefUser && ((MobDefUser) user).isAlive()) {
            if (((MobDefUser) user).getKit().equals(this.kit)) {
              for (int i = 0; i < this.item.getAmount(); i++) {
                if (user.containsAtLeast(this.item, this.maxItems, true) >= 0) {
                  break;
                }

                user.getInventory().addItem(this.item.asOne());
              }
              user.updateInventory();
            }
          }
        }
      }
    }, 0, rate * 20, GameMobDefence.getPlugin());
  }

  public void cancel() {
    if (this.task != null) {
      this.task.cancel();
    }
  }
}
