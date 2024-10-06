/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGood;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public class TeamHealth {

  private static void updateHealth(MobDefUser user, int health) {
    maxHealth = health * 2;
    for (User gameUser : Server.getInOutGameUsers()) {
      gameUser.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
    }
    MobDefServer.broadcastGameMessage(user.getChatNameComponent()
        .append(Component.text(" leveled up max " + "health", ExTextColor.WARNING)));
  }

  public static int getMaxHealth() {
    return maxHealth;
  }

  private static int maxHealth = 8 * 2;
  public static final UpgradeableGood.Builder HEALTH = new UpgradeableGood.Builder() {
    @Override
    public UpgradeableGood build() {
      this.checkBuild();
      return new UpgradeableGood(this) {
        @Override
        public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
          LevelableProperty levelableProperty = this.levelType.get1(item);
          if (levelableProperty == null) {
            return;
          }
          levelableProperty.tryLevelUp(user);
          inv.setItemStack(levelableProperty.getDisplayItem());
        }
      };
    }
  }
      .name("Health")
      .display(new ExItemStack(Material.FIRE_CORAL_BLOCK))
      .addLevelableProperty(new LevelableProperty.Builder()
          .name("Max Health")
          .display(new ExItemStack(Material.NETHER_WART))
          .defaultLevel(1)
          .levelDescription("+1 ❤")
          .addLevel(null, (MobDefUser u) -> {
            maxHealth = 8 * 2;
            for (User gameUser : Server.getInOutGameUsers()) {
              gameUser.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(8 * 2);
              gameUser.setHealth(8 * 2);
            }
          })
          .addLevel(new Price(1, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 9))
          .addLevel(new Price(2, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 10))
          .addLevel(new Price(3, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 11))
          .addLevel(new Price(4, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 12))
          .addLevel(new Price(5, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 13))
          .addLevel(new Price(6, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 14))
          .addLevel(new Price(7, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 15))
          .addLevel(new Price(8, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 16))
          .addLevel(new Price(9, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 17))
          .addLevel(new Price(10, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 18))
          .addLevel(new Price(11, Currency.EMERALD), (MobDefUser u) -> updateHealth(u, 19))
          .addLevel(new Price(12, Currency.EMERALD),
              (MobDefUser u) -> updateHealth(u, 20)));


}
