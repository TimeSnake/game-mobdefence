/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;

import java.util.*;
import java.util.function.Consumer;

public class SimpleGood {

  protected final int slot;

  protected final ExItemStack displayItem;
  protected final boolean rebuyable;
  private final Iterator<Price> priceIt;
  protected List<String> description;
  protected boolean bought = false;
  protected Consumer<MobDefUser> giveConsumer;
  private Price currentPrice;

  protected SimpleGood(Builder builder) {
    this.slot = builder.slot;
    this.rebuyable = builder.rebuyable;

    this.priceIt = builder.prices.iterator();
    this.currentPrice = priceIt.next();

    this.displayItem = builder.displayItem.cloneWithId();

    this.displayItem.setDisplayName("§6" + this.displayItem.getItemMeta().getDisplayName());

    this.description = Arrays.asList(builder.description != null ? builder.description : new String[0]);

    this.giveConsumer = builder.giveConsumer;

    this.updateDisplayItemDescription();
  }

  protected void updateDisplayItemDescription() {
    List<String> lore = new ArrayList<>();

    lore.add("");
    lore.add("§9Price: §2" + this.getPrice().toString());

    if (!this.description.isEmpty()) {
      lore.add("");
      lore.addAll(this.description);
    }

    if (this.priceIt.hasNext()) {
      lore.add("");
      lore.add("§cPrice increases per buy");
    }

    this.displayItem.setExLore(lore);
  }

  public ExItemStack getDisplayItem() {
    return displayItem;
  }

  public Price getPrice() {
    return this.currentPrice;
  }

  public void sellTo(MobDefUser user, ExInventory inventory) {
    this.give(user);
    this.bought = true;

    if (this.priceIt.hasNext()) {
      this.currentPrice = priceIt.next();
      this.updateDisplayItemDescription();
      inventory.update();
      // TODO check
      // inventory.setItemStack(this.getSlot(), this.getDisplayItem());
    }
  }

  public void give(MobDefUser user) {
    this.giveConsumer.accept(user);
  }

  public boolean isRebuyable() {
    return rebuyable;
  }

  public boolean isBought() {
    return bought;
  }

  public int getSlot() {
    return slot;
  }


  public static class Builder {

    protected Integer slot;
    protected ExItemStack displayItem;
    protected boolean rebuyable = true;
    protected List<Price> prices = new LinkedList<>();
    protected String[] description;
    protected Consumer<MobDefUser> giveConsumer;

    public Builder() {

    }

    public Builder slot(int slot) {
      this.slot = slot;
      return this;
    }

    public Builder display(ExItemStack item) {
      this.displayItem = item;
      return this;
    }

    public Builder notRebuyable() {
      this.rebuyable = false;
      return this;
    }

    public Builder price(Price... price) {
      this.prices.addAll(List.of(price));
      return this;
    }

    public Builder price(Price basePrice, int increase, int increaseMultiplier) {
      for (int i = basePrice.getAmount(); i < 64; i += increase) {
        for (int j = 0; j < increaseMultiplier; j++) {
          this.prices.add(new Price(i, basePrice.getCurrency()));
        }
      }
      return this;
    }

    public Builder description(String... description) {
      this.description = description;
      return this;
    }

    public Builder give(Consumer<MobDefUser> giveConsumer) {
      if (this.giveConsumer != null) {
        this.giveConsumer = u -> {
          this.giveConsumer.accept(u);
          giveConsumer.accept(u);
        };
      } else {
        this.giveConsumer = giveConsumer;
      }
      return this;
    }

    public Builder giveItems(ExItemStack... items) {
      return this.giveItems(List.of(items));
    }

    public Builder giveItems(List<ExItemStack> items) {
      if (items.isEmpty()) {
        return this;
      }
      if (this.giveConsumer != null) {
        this.giveConsumer = u -> {
          this.giveConsumer.accept(u);
          items.forEach(i -> u.addItem(i.cloneWithId()));
        };
      } else {
        this.giveConsumer = u -> items.forEach(i -> u.addItem(i.cloneWithId()));
      }
      if (this.displayItem == null) {
        this.displayItem = items.get(0).cloneWithId();
      }
      return this;
    }

    public SimpleGood build() {
      this.checkBuild();
      return new SimpleGood(this);
    }

    protected void checkBuild() {
      if (this.displayItem == null) {
        throw new BuilderNotFullyInstantiatedException("display item is null");
      }
      if (this.prices == null) {
        throw new BuilderNotFullyInstantiatedException("price list is null");
      }
      if (this.giveConsumer == null) {
        throw new BuilderNotFullyInstantiatedException("give consumer is null");
      }
      if (this.slot == null) {
        throw new BuilderNotFullyInstantiatedException("slot is null");
      }
    }
  }
}
