/*
 * workspace.game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryClickListener;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import de.timesnake.library.basic.util.chat.ExTextColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class Shop implements UserInventoryClickListener, InventoryHolder {

    protected final int slot;
    protected final ExItemStack displayItem;
    protected final HashMap<Integer, Upgradeable> upgradeableBySlot = new HashMap<>();
    protected final Map<ExItemStack, Trade> tradesByDisplayItem = new HashMap<>();
    protected String name;
    protected ExInventory inv;

    protected Shop(Builder builder) {
        this.name = builder.name;
        this.slot = builder.slot;
        this.displayItem = builder.displayItem.cloneWithId();
        this.displayItem.setDisplayName("§6" + this.name);

        builder.tradeBuilders.stream()
                .map(Trade.Builder::build)
                .forEach(t -> this.tradesByDisplayItem.put(t.getDisplayItem(), t));

        List<Upgradeable> upgradeables = builder.upgradeableBuilders.stream().map(Upgradeable.Builder::build).toList();

        if (this.getTrades().isEmpty()) {
            this.inv = new ExInventory(upgradeables.size() * 9 + 18, Component.text(this.name), this);

            int itemSlot = 10;
            for (Upgradeable levelItem : upgradeables) {
                this.upgradeableBySlot.put(itemSlot, levelItem);
                levelItem.fillInventoryRow(this.inv, itemSlot);
                itemSlot += 9;
            }
        } else if (upgradeables.isEmpty()) {
            this.inv = new ExInventory(this.getTrades().stream()
                    .max(Comparator.comparingInt(Trade::getSlot)).get().getSlot() + 9, Component.text(this.name), this);

            for (Trade trade : this.getTrades()) {
                this.inv.setItemStack(trade.getSlot(), trade.getDisplayItem());
            }
        } else {
            this.inv = new ExInventory(54, Component.text(this.name), this);

            int itemSlot = upgradeables.size() < 3 ? 10 : 1;

            for (Upgradeable levelItem : upgradeables) {
                this.upgradeableBySlot.put(itemSlot, levelItem);
                levelItem.fillInventoryRow(this.inv, itemSlot);
                itemSlot += 9;
            }

            itemSlot = itemSlot < 4 * 9 ? itemSlot + 18 : itemSlot + 9;

            for (Trade trade : this.getTrades()) {
                this.inv.setItemStack(itemSlot, trade.getDisplayItem());
                itemSlot++;
            }
        }

        Server.getInventoryEventManager().addClickListener(this, this);
    }

    public Collection<Trade> getTrades() {
        return tradesByDisplayItem.values();
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

        Upgradeable upgradeable = this.upgradeableBySlot.get(baseSlot);

        if (upgradeable != null) {
            upgradeable.onLevelClick(user, this.inv, clickedItem);
            user.updateInventory();
            return;
        }

        Trade trade = this.tradesByDisplayItem.get(clickedItem);

        event.setCancelled(true);

        if (trade == null) {
            return;
        }

        if (!trade.isRebuyable() && trade.isBought()) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("You already bought this item", ExTextColor.WARNING));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return;
        }

        if (!user.containsAtLeast(trade.getPrice().asItem())) {
            user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Not enough money", ExTextColor.WARNING));
            user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
            return;
        }

        user.removeCertainItemStack(trade.getPrice().asItem());

        trade.sellTo(user, this.inv);
    }

    public Collection<Upgradeable> getUpgradeables() {
        return upgradeableBySlot.values();
    }

    public static class Builder implements Supplier<Shop> {

        protected Type type;
        protected Integer slot;
        protected String name;
        protected ExItemStack displayItem;
        protected List<Upgradeable.Builder> upgradeableBuilders = new LinkedList<>();
        protected List<Trade.Builder> tradeBuilders = new LinkedList<>();

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

        public Builder addUpgradeable(Upgradeable.Builder... builders) {
            this.upgradeableBuilders.addAll(List.of(builders));
            return this;
        }

        public Builder addTrade(Trade.Builder... builders) {
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
                case USER -> new UserShop(null, this);
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
