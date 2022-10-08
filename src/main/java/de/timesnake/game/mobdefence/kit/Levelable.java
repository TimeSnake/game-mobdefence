/*
 * game-mobdefence.main
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

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Levelable<T extends LevelType<?>> {

    protected final String name;
    protected final ExItemStack displayItem;

    protected final LinkedHashMap<Integer, T> levelTypeByItemId = new LinkedHashMap<>();

    protected Levelable(String name, ExItemStack displayItem, List<T> levelTypes) {
        this.name = name;
        this.displayItem = displayItem;

        for (T levelType : levelTypes) {
            this.levelTypeByItemId.put(levelType.getDisplayItem().getId(), levelType);
        }
    }

    protected Levelable(Levelable<T> levelable) {
        this.name = levelable.name;
        this.displayItem = levelable.getDisplayItem().cloneWithId();
        this.displayItem.setDisplayName("§6" + this.name);

        this.initLevelTypeClone(levelable);

        for (T levelType : levelable.levelTypeByItemId.values()) {
            T clonedLevelType = this.cloneLevelType(levelType);
            this.levelTypeByItemId.put(clonedLevelType.getDisplayItem().getId(), clonedLevelType);
        }

        for (LevelType<?> levelType : levelable.levelTypeByItemId.values()) {
            List<LevelType<?>> conflictingTypes = new ArrayList<>();

            for (LevelType<?> conflictingType : levelType.getConflictingTypes()) {
                LevelType<?> clonedConflictingLevelType =
                        this.levelTypeByItemId.get(conflictingType.getDisplayItem().getId());
                conflictingTypes.add(clonedConflictingLevelType);
            }

            LevelType<?> clonedLevelType = this.levelTypeByItemId.get(levelType.getDisplayItem().getId());
            clonedLevelType.setConflictingTypes(conflictingTypes);
            clonedLevelType.updateDescription();
        }
    }

    public void initLevelTypeClone(Levelable<T> levelable) {

    }

    public abstract T cloneLevelType(T levelType);

    public abstract Levelable<T> clone();

    public void fillInventoryRow(ExInventory inv, int slot) {
        inv.setItemStack(slot, this.getDisplayItem());
        slot += 2;
        for (LevelType<?> levelType : this.levelTypeByItemId.values()) {
            inv.setItemStack(slot, levelType.getDisplayItem());
            levelType.getDisplayItem().setSlot(slot);
            slot++;
        }
    }

    public abstract void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item);

    public ExItemStack getDisplayItem() {
        return displayItem;
    }
}
