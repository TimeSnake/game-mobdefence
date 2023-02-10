/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;
import java.util.List;
import java.util.function.Function;

public abstract class Level<V> {

    protected final int level;
    protected final Price price;
    protected final String description;
    protected final V value;
    protected int unlockWave = 0;

    protected Level(int level, int unlockWave, Price price, String description, V value) {
        this(level, price, description, value);
        this.unlockWave = unlockWave;
    }

    protected Level(int level, Price price, String description, V value) {
        this.level = level;
        this.price = price;
        this.description = description;
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    public Price getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public V getValue() {
        return value;
    }

    public int getUnlockWave() {
        return unlockWave;
    }

    public abstract void run(MobDefUser user);

    protected static class ItemLevel extends Level<Function<ExItemStack, ExItemStack>> {

        private final ExItemStack item;

        protected ItemLevel(ExItemStack item, int level, Price price, String description,
                Function<ExItemStack, ExItemStack> value) {
            super(level, price, description, value);
            this.item = item;
        }

        protected ItemLevel(ExItemStack item, int level, int unlockWave, Price price,
                String description,
                Function<ExItemStack, ExItemStack> value) {
            super(level, unlockWave, price, description, value);
            this.item = item;
        }

        @Override
        public void run(MobDefUser user) {
            ExItemStack userItem = user.getItem(this.item);
            if (userItem == null) {
                return;
            }
            ExItemStack leveledItem = this.apply(userItem);
            if (leveledItem == null) {
                return;
            }
            user.replaceExItemStack(userItem, leveledItem);
        }

        public ExItemStack apply(ExItemStack item) {
            return this.value.apply(item);
        }
    }

    protected static class LoreNumberLevel<T extends Number> extends Level<T> {

        private final ExItemStack item;
        private final String name;
        private final String unit;
        private final String loreName;
        private final int loreLine;
        private final int multiplier;

        public LoreNumberLevel(ExItemStack item, String name, int loreLine, int decimal,
                String unit, int level, Price price,
                String description, T value) {
            super(level, price, description, value);
            this.item = item;
            this.name = name;
            this.unit = unit;
            this.loreName = "§7" + this.name + ": §9";
            this.loreLine = loreLine;
            this.multiplier = (int) Math.pow(10, decimal);
        }

        @Override
        public void run(MobDefUser user) {
            ExItemStack userItem = user.getItem(this.item);
            if (userItem == null) {
                return;
            }
            this.apply(userItem);
            user.replaceExItemStack(userItem, userItem);
        }

        public ExItemStack apply(ExItemStack item) {
            return item.replaceLoreLine(this.loreLine, this.getLoreText(this.value));
        }

        public <V extends Number> String getLoreText(V value) {
            if (this.multiplier == 1) {
                return this.loreName + value.intValue() + " " + this.unit;
            }
            return this.loreName + value.doubleValue() + " " + this.unit;
        }

        public String getName() {
            return name;
        }

        public String getLoreName() {
            return this.loreName;
        }

        public int getLoreLine() {
            return loreLine;
        }

        public String getValueFromLore(List<String> lore) {
            if (lore.size() <= loreLine) {
                return null;
            }
            String text = lore.get(this.loreLine);
            if (text == null) {
                return null;
            }
            return text.replaceAll(this.loreName, "").replaceAll(" " + this.unit, "");
        }
    }
}
