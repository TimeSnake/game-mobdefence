/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.util.LinkedList;
import java.util.List;

public class UpgradeableItem extends Upgradeable {

    private final Price buyPrice;
    private final boolean rebuyable;
    private final int unlockedAtWave;
    protected ExItemStack item;
    private boolean bought = false;

    protected UpgradeableItem(Builder builder) {
        super(builder);
        this.item = builder.baseItem.cloneWithId();
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


    }

    @Override
    public void loadBaseForUser(MobDefUser user) {
        for (LevelType levelType : this.levelType.values()) {
            for (Level<?> level : levelType.getBaseLevels()) {
                if (level instanceof Level.ItemLevel itemLevel) {
                    this.item = itemLevel.apply(this.item);
                } else if (level instanceof Level.LoreNumberLevel<?> loreLevel) {
                    this.item = loreLevel.apply(this.item);
                } else {
                    level.run(user);
                }
            }
        }

        if (!this.isBuyable()) {
            user.setItem(this.item);
        }
    }

    public ExItemStack getItem() {
        return item;
    }

    @Override
    public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
        LevelType levelType = this.levelType.get1(item);

        if (this.isBuyable()) {
            if (levelType == null && this.getDisplayItem().equals(item)) {

                if (MobDefServer.getWaveNumber() < this.unlockedAtWave) {
                    user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("This item is locked until wave ", ExTextColor.WARNING)
                            .append(Component.text(this.unlockedAtWave, ExTextColor.VALUE))
                            .append(Component.text(" is completed", ExTextColor.WARNING)));
                    return;
                }

                if (this.bought && this.rebuyable) {
                    user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("You already bought this item", ExTextColor.WARNING));
                    return;
                }

                if (!user.containsAtLeast(this.buyPrice.asItem())) {
                    user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Not enough money", ExTextColor.WARNING));
                    user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
                    return;
                }

                user.removeCertainItemStack(this.buyPrice.asItem());

                user.addItem(this.item);
                user.playNote(Instrument.STICKS, Note.natural(1, Note.Tone.C));
                this.bought = true;

                return;
            }

            if (!this.bought) {
                user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("You must buy this item before", ExTextColor.WARNING));
                user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
                return;
            }
        }

        if (levelType == null) {
            return;
        }

        LevelType conflictingType = this.isConflicting(levelType);

        if (conflictingType != null) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Conflicting with ", ExTextColor.WARNING)
                    .append(Component.text(conflictingType.getName(), ExTextColor.VALUE)));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return;
        }

        levelType.tryLevelUp(user);

        inv.setItemStack(levelType.getDisplayItem());
    }

    public boolean isBuyable() {
        return this.buyPrice != null;
    }

    public static class Builder extends Upgradeable.Builder {

        private ExItemStack baseItem;
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
        public Builder addLvlType(LevelType.Builder levelTypeBuilder) {
            return (Builder) super.addLvlType(levelTypeBuilder);
        }

        @Override
        protected void checkBuild() {
            super.checkBuild();
            if (this.baseItem == null) {
                throw new BuilderNotFullyInstantiatedException("base item is null");
            }
        }

        @Override
        public Builder addConflictToLvlType(LevelType.Builder levelTypeBuilder1, LevelType.Builder levelTypeBuilder2) {
            return (Builder) super.addConflictToLvlType(levelTypeBuilder1, levelTypeBuilder2);
        }

        @Override
        public UpgradeableItem build() {
            this.checkBuild();
            return new UpgradeableItem(this);
        }

        public Builder baseItem(ExItemStack item) {
            this.baseItem = item;
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

        public ExItemStack getBaseItem() {
            return this.baseItem;
        }
    }

}
