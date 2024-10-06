/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.inventory.UserInventoryClickListener;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import de.timesnake.library.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class Shop implements UserInventoryClickListener, InventoryHolder {

  protected final int slot;
  protected final ExItemStack displayItem;
  protected final HashMap<Integer, UpgradeableGood> upgradeableGoodBySlot = new HashMap<>();
  protected final Map<ExItemStack, SimpleGood> simpleGoodByDisplayItem = new HashMap<>();
  protected String name;
  protected ExInventory inv;

  protected Shop(Builder builder) {
    this.name = builder.name;
    this.slot = builder.slot;
    this.displayItem = builder.displayItem.cloneWithId();
    this.displayItem.setDisplayName("§6" + this.name);

    builder.tradeBuilders.stream()
        .map(SimpleGood.Builder::build)
        .forEach(t -> this.simpleGoodByDisplayItem.put(t.getDisplayItem(), t));

    List<UpgradeableGood> upgradeableGoods =
        builder.upgradeableBuilders.stream().map(UpgradeableGood.Builder::build).toList();

    if (this.getSimpleGoods().isEmpty()) {
      this.inv = new ExInventory(upgradeableGoods.size() * 9 + 18, Component.text(this.name),
          this);

      int itemSlot = 1;
      for (UpgradeableGood levelItem : upgradeableGoods) {
        this.upgradeableGoodBySlot.put(itemSlot, levelItem);
        levelItem.fillInventoryRow(this.inv, itemSlot);
        itemSlot += 9;
      }
    } else if (upgradeableGoods.isEmpty()) {
      this.inv = new ExInventory(InventoryType.SHULKER_BOX, this.name, this);

      for (SimpleGood trade : this.getSimpleGoods()) {
        this.inv.setItemStack(trade.getSlot(), trade.getDisplayItem());
      }
    } else {
      this.inv = new ExInventory(54, this.name, this);

      int itemSlot = 1;

      for (UpgradeableGood levelItem : upgradeableGoods) {
        this.upgradeableGoodBySlot.put(itemSlot, levelItem);
        levelItem.fillInventoryRow(this.inv, itemSlot);
        itemSlot += 9;
      }

      for (SimpleGood simpleGood : this.getSimpleGoods()) {
        this.inv.setItemStack(simpleGood.getSlot(), simpleGood.getDisplayItem());
      }
    }

    Server.getInventoryEventManager().addClickListener(this, this);
  }

  public Collection<SimpleGood> getSimpleGoods() {
    return simpleGoodByDisplayItem.values();
  }

  public Collection<UpgradeableGood> getUpgradeableGoods() {
    return upgradeableGoodBySlot.values();
  }

  public String getName() {
    return name;
  }

  public int getSlot() {
    return this.slot;
  }

  public ExItemStack getDisplayItem() {
    return this.displayItem;
  }

  @Override
  public @NotNull Inventory getInventory() {
    return this.inv.getInventory();
  }

  @Override
  public void onUserInventoryClick(UserInventoryClickEvent event) {
    event.setCancelled(true);

    MobDefUser user = (MobDefUser) event.getUser();
    ExItemStack clickedItem = event.getClickedItem();

    int baseSlot = event.getSlot() - (event.getSlot() % 9) + 1;

    UpgradeableGood upgradeableGood = this.upgradeableGoodBySlot.get(baseSlot);

    if (upgradeableGood != null) {
      upgradeableGood.onLevelClick(user, this.inv, clickedItem);
      user.updateInventory();
      return;
    }

    SimpleGood simpleGood = this.simpleGoodByDisplayItem.get(clickedItem);

    event.setCancelled(true);

    if (simpleGood == null) {
      return;
    }

    if (!simpleGood.isRebuyable() && simpleGood.isBought()) {
      user.sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("You already bought this item", ExTextColor.WARNING));
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return;
    }

    if (!user.containsAtLeast(simpleGood.getPrice().asItem())) {
      user.sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("Not enough money", ExTextColor.WARNING));
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return;
    }

    user.removeCertainItemStack(simpleGood.getPrice().asItem());

    simpleGood.sellTo(user, this.inv);
  }

  public static class Builder implements Supplier<Shop> {

    protected Type type;
    protected Integer slot;
    protected String name;
    protected ExItemStack displayItem;
    protected List<UpgradeableGood.Builder> upgradeableBuilders = new LinkedList<>();
    protected List<SimpleGood.Builder> tradeBuilders = new LinkedList<>();

    public Builder() {

    }

    public Builder type(Type type) {
      this.type = type;
      return this;
    }

    public Builder slot(int slot) {
      this.slot = slot;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder display(ExItemStack displayItem) {
      this.displayItem = displayItem;
      return this;
    }

    public Builder addUpgradeable(UpgradeableGood.Builder... builders) {
      this.upgradeableBuilders.addAll(List.of(builders));
      return this;
    }

    public Builder addTrade(SimpleGood.Builder... builders) {
      this.tradeBuilders.addAll(List.of(builders));
      return this;
    }

    @Override
    public Shop get() {
      return this.build();
    }

    public Shop build() {
      this.checkBuild();
      return switch (this.type) {
        case DEFAULT -> new Shop(this);
        case TEAM -> new TeamShop(this);
        case USER -> new UserShop(this);
      };
    }

    protected void checkBuild() {
      if (this.type == null) {
        throw new BuilderNotFullyInstantiatedException("type is null");
      }

      if (this.slot == null) {
        throw new BuilderNotFullyInstantiatedException("slot is null");
      }
      if (this.name == null) {
        throw new BuilderNotFullyInstantiatedException("name is null");
      }
      if (this.displayItem == null) {
        throw new BuilderNotFullyInstantiatedException("display item is null");
      }
      if (this.upgradeableBuilders.isEmpty() && this.tradeBuilders.isEmpty()) {
        throw new BuilderNotFullyInstantiatedException("upgradables and trades are empty");
      }
    }

    public enum Type {
      USER,
      TEAM,
      DEFAULT
    }
  }
}
