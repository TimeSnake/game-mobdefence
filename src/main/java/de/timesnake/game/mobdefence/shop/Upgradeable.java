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

import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import de.timesnake.library.basic.util.MultiKeyMap;
import org.bukkit.Material;

import java.util.*;

public abstract class Upgradeable {

    public static final ExItemStack PLACEHOLDER = new ExItemStack(Material.GRAY_STAINED_GLASS_PANE, "")
            .setMoveable(false).setDropable(false).immutable();

    protected final String name;
    protected final ExItemStack displayItem;

    protected final MultiKeyMap<ExItemStack, String, LevelType> levelType = new MultiKeyMap<>();
    protected final Map<String, Collection<String>> conflictingTypes;

    protected Upgradeable(Builder builder) {
        this.name = builder.name;
        this.displayItem = builder.displayItem.cloneWithId();
        this.displayItem.setDisplayName("§c" + this.name);

        for (LevelType levelType : builder.levelTypeBuilders.stream().map(LevelType.Builder::build).toList()) {
            levelType.setConflictingTypes(builder.conflictingTypes.get(levelType.getName()));
            this.levelType.put(levelType.getDisplayItem(), levelType.getName(), levelType);
        }

        this.conflictingTypes = builder.conflictingTypes;
    }

    protected LevelType isConflicting(LevelType levelType) {
        return this.conflictingTypes.getOrDefault(levelType.getName(), List.of()).stream()
                .map(this.levelType::get2)
                .filter(conflict -> conflict != null && conflict.getLevel() > 0)
                .findFirst().orElse(null);
    }

    public void fillInventoryRow(ExInventory inv, int slot) {
        inv.setItemStack(slot++, this.getDisplayItem());
        inv.setItemStack(slot++, PLACEHOLDER);

        for (Iterator<LevelType> iterator = this.levelType.values().iterator(); slot % 9 != 8; slot++) {
            if (iterator.hasNext()) {
                LevelType levelType = iterator.next();
                inv.setItemStack(slot, levelType.getDisplayItem());
                levelType.getDisplayItem().setSlot(slot);
            }
            inv.setItemStack(slot, PLACEHOLDER);

        }
    }

    public void loadBaseForUser(MobDefUser user) {
        for (LevelType levelType : this.levelType.values()) {
            for (Level<?> level : levelType.getBaseLevels()) {
                level.run(user);
            }
        }
    }

    public abstract void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item);

    public ExItemStack getDisplayItem() {
        return displayItem;
    }

    public static abstract class Builder {

        protected final LinkedList<LevelType.Builder> levelTypeBuilders = new LinkedList<>();
        protected String name;
        protected ExItemStack displayItem;
        protected Map<String, Collection<String>> conflictingTypes = new HashMap<>();

        public Builder() {

        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder display(ExItemStack item) {
            this.displayItem = item;
            return this;
        }

        public Builder addLvlType(LevelType.Builder levelTypeBuilder) {
            this.levelTypeBuilders.addLast(levelTypeBuilder);
            return this;
        }

        protected void checkBuild() {
            if (this.name == null) {
                throw new BuilderNotFullyInstantiatedException("name is null");
            }
            if (this.levelTypeBuilders.isEmpty()) {
                throw new BuilderNotFullyInstantiatedException("level type builder list is empty");
            }
            if (this.displayItem == null) {
                throw new BuilderNotFullyInstantiatedException("display item is null");
            }
        }

        public Builder addConflictToLvlType(LevelType.Builder levelTypeBuilder1, LevelType.Builder levelTypeBuilder2) {
            this.conflictingTypes.computeIfAbsent(levelTypeBuilder1.name, k -> new HashSet<>()).add(levelTypeBuilder2.name);
            this.conflictingTypes.computeIfAbsent(levelTypeBuilder2.name, k -> new HashSet<>()).add(levelTypeBuilder1.name);
            return this;
        }

        public abstract Upgradeable build();

    }
}