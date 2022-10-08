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

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.List;

public class ItemLevelType<L extends ItemLevel<?>> extends LevelType<L> {

    private ExItemStack item;

    public ItemLevelType(String name, ExItemStack displayItem, int baseLevel, int maxLevel, List<L> levels) {
        super(name, displayItem, baseLevel, maxLevel, levels);
    }

    public ItemLevelType(ItemLevelType<L> levelType, ExItemStack item) {
        super(levelType);
        this.item = item;
    }

    public String getBaseLevelLore(Number value) {
        return ((ItemLevel.LoreNumberLevel) this.getFirstLevel()).getLoreText(value);
    }

    public String getValueFromLore(List<String> lore) {
        return ((ItemLevel.LoreNumberLevel<?>) this.getFirstLevel()).getValueFromLore(lore);
    }

    @Override
    protected boolean levelUp(MobDefUser user, L level) {

        ExItemStack leveledItem = level.levelUp(this.item);

        if (leveledItem == null) {
            return false;
        }

        user.replaceExItemStack(this.item, leveledItem);

        this.item = leveledItem;

        return false;
    }

    public ExItemStack getItem() {
        return item;
    }

    public ItemLevelType<L> clone(ExItemStack item) {
        return new ItemLevelType<>(this, item);
    }
}
