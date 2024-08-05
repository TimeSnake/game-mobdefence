/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryClickListener;
import de.timesnake.game.mobdefence.shop.Shop;
import de.timesnake.game.mobdefence.shop.UserShop;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class KitShop implements UserInventoryClickListener, InventoryHolder {

  private final Logger logger = LogManager.getLogger("mob-def.kit.shop");

  private final ExInventory inv;
  private final Map<ExItemStack, Shop> shopsByItem = new HashMap<>();
  private MobDefUser user;

  public KitShop(MobDefUser user) {
    this.user = user;
    MobDefKit kit = ((MobDefKit) user.getKit());

    this.inv = new ExInventory(InventoryType.HOPPER, "§7Shop", this);

    for (Supplier<Shop> shopSupplier : kit.getShopSuppliers()) {
      Shop shop;
      try {
        shop = shopSupplier.get();
      } catch (BuilderNotFullyInstantiatedException e) {
        this.logger.warn("Failed to build shop for '{}': {}", user.getName(), e.getMessage());
        return;
      }
      if (shop instanceof UserShop) {
        ((UserShop) shop).setUser(user);
      }
      this.shopsByItem.put(shop.getDisplayItem(), shop);
      this.inv.setItemStack(shop.getSlot(), shop.getDisplayItem());

      shop.getUpgradeables().forEach(u -> u.loadBaseForUser(user));
    }

    Server.getInventoryEventManager().addClickListener(this, this);
  }

  @Override
  public void onUserInventoryClick(UserInventoryClickEvent event) {

    event.setCancelled(true);

    if (!this.user.equals(event.getUser())) {
      return;
    }

    ExItemStack clickedItem = event.getClickedItem();
    Shop shop = this.shopsByItem.get(clickedItem);

    if (shop == null) {
      return;
    }

    user.openInventory(shop.getInventory());

  }

  @Override
  public @NotNull Inventory getInventory() {
    return this.inv.getInventory();
  }

  public void setUser(MobDefUser user) {
    this.user = user;

    for (Shop shop : this.shopsByItem.values()) {
      if (shop instanceof UserShop) {
        ((UserShop) shop).setUser(user);
      }
    }
  }

}
