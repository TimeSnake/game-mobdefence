/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.util.LinkedList;
import java.util.List;

public class UpgradeableGoodItem extends UpgradeableGood {

  private final Price buyPrice;
  private final boolean rebuyable;
  private final int unlockedAtWave;
  private boolean bought = false;

  protected ExItemStack startItem;

  protected UpgradeableGoodItem(Builder builder) {
    super(builder);
    this.startItem = builder.startItem.cloneWithId().immutable();
    this.buyPrice = builder.buyPrice;
    this.rebuyable = builder.rebuyable;
    this.unlockedAtWave = builder.unlockedAtWave;

    List<String> lore = new LinkedList<>();

    if (this.buyPrice != null) {
      lore.addAll(List.of("§7Buy the item", "", "§7Price:        §2" + this.buyPrice));
    }
    if (this.unlockedAtWave > 0) {
      lore.addAll(List.of("", "§cLocked until wave " + this.unlockedAtWave));
    }

    this.displayItem.setLore(lore);

    if (this.startItem != null) {
      for (LevelableProperty property : this.levelType) {
        for (Level level : property.getLevels()) {
          if (level instanceof ItemLevel itemLevel) {
            itemLevel.targetItem = this.startItem;
          }
        }
      }
    }
  }

  @Override
  public void loadBaseForUser(MobDefUser user) {
    if (!this.isBuyable()) {
      user.setItem(this.startItem.cloneWithId());
      super.loadBaseForUser(user);
    }
  }

  @Override
  public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
    LevelableProperty levelableProperty = this.levelType.get1(item);

    if (this.isBuyable()) {
      if (levelableProperty == null && this.getDisplayItem().equals(item)) {

        if (MobDefServer.getWaveNumber() < this.unlockedAtWave) {
          user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§wThis item is locked until wave §v" + this.unlockedAtWave);
          return;
        }

        if (this.bought && !this.rebuyable) {
          user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§wYou already bought this item");
          return;
        }

        if (!user.containsAtLeast(this.buyPrice.asItem())) {
          user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§wNot enough money");
          user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
          return;
        }

        user.removeCertainItemStack(this.buyPrice.asItem());
        user.setItem(this.startItem.cloneWithId());
        super.loadBaseForUser(user);
        user.playNote(Instrument.STICKS, Note.natural(1, Note.Tone.C));
        this.bought = true;
        return;
      }

      if (!this.bought) {
        user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§wYou must buy this item before");
        user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
        return;
      }
    }

    if (levelableProperty == null) {
      return;
    }

    LevelableProperty conflictingType = this.isConflicting(levelableProperty);

    if (conflictingType != null) {
      user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§wConflicting with §v" + conflictingType.getName());
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return;
    }

    levelableProperty.tryLevelUp(user);

    inv.setItemStack(levelableProperty.getDisplayItem());
  }

  public boolean isBuyable() {
    return this.buyPrice != null;
  }

  public static class Builder extends UpgradeableGood.Builder {

    private ExItemStack startItem;
    private Price buyPrice;
    private boolean rebuyable = false;
    private int unlockedAtWave = 0;

    public Builder() {

    }

    @Override
    public Builder name(String name) {
      return (Builder) super.name(name);
    }

    @Override
    public Builder display(ExItemStack item) {
      return (Builder) super.display(item);
    }

    @Override
    public Builder addLevelableProperty(LevelableProperty.Builder levelTypeBuilder) {
      return (Builder) super.addLevelableProperty(levelTypeBuilder);
    }

    @Override
    protected void checkBuild() {
      super.checkBuild();
      if (this.startItem == null) {
        throw new BuilderNotFullyInstantiatedException("base item is null");
      }
    }

    @Override
    public Builder addConflictToLvlType(LevelableProperty.Builder levelTypeBuilder1,
                                        LevelableProperty.Builder levelTypeBuilder2) {
      return (Builder) super.addConflictToLvlType(levelTypeBuilder1, levelTypeBuilder2);
    }

    @Override
    public UpgradeableGoodItem build() {
      this.checkBuild();
      return new UpgradeableGoodItem(this);
    }

    public Builder startItem(ExItemStack item) {
      this.startItem = item;
      return this;
    }

    public Builder price(Price price) {
      this.buyPrice = price;
      return this;
    }

    public Builder rebuyable() {
      this.rebuyable = true;
      return this;
    }

    public Builder unlockedAtWave(int wave) {
      this.unlockedAtWave = wave;
      return this;
    }

    public ExItemStack getStartItem() {
      return this.startItem;
    }
  }

}
