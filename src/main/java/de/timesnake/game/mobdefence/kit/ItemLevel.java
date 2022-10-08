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
import de.timesnake.library.basic.util.Tuple;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ItemLevel<V> extends Level<V> {

    public ItemLevel(int level, ShopPrice price, String description, V value) {
        super(level, price, description, value);
    }

    public ItemLevel(int level, int unlockWave, ShopPrice price, String description, V value) {
        super(level, unlockWave, price, description, value);
    }

    public static List<MaterialLevel> getMaterialLevels(int startLevel, List<ShopPrice> prices,
                                                        List<String> levelDescriptions, List<Material> materials) {

        List<MaterialLevel> levels = new ArrayList<>();

        Iterator<ShopPrice> itemIt = prices.listIterator();
        Iterator<String> descriptionIt = levelDescriptions.listIterator();
        Iterator<Material> materialIt = materials.listIterator();

        for (int level = startLevel; itemIt.hasNext() && descriptionIt.hasNext() && materialIt.hasNext(); level++) {
            levels.add(new MaterialLevel(level, itemIt.next(), descriptionIt.next(), materialIt.next()));
        }
        return levels;
    }

    public static List<EnchantmentLevel> getEnchantmentLevels(int startLevel, List<ShopPrice> prices,
                                                              List<String> levelDescriptions, Enchantment enchantment
            , List<Integer> enchantmentLevels) {

        List<EnchantmentLevel> levels = new ArrayList<>();

        Iterator<ShopPrice> priceIt = prices.listIterator();
        Iterator<String> descriptionIt = levelDescriptions.listIterator();
        Iterator<Integer> enchantLevelIt = enchantmentLevels.listIterator();

        for (int level = startLevel; priceIt.hasNext() && descriptionIt.hasNext() && enchantLevelIt.hasNext(); level++) {
            levels.add(new EnchantmentLevel(level, priceIt.next(), descriptionIt.next(), enchantment,
                    enchantLevelIt.next()));
        }
        return levels;
    }

    public static List<EnchantmentLevel> getEnchantmentLevels(int startLevel, List<ShopPrice> prices,
                                                              String description, Enchantment enchantment,
                                                              List<Integer> enchantmentLevels) {

        List<String> descriptions = new ArrayList<>();
        for (int level = startLevel; level < Math.min(prices.size(), enchantmentLevels.size()) + startLevel; level++) {
            descriptions.add(description);
        }
        return getEnchantmentLevels(startLevel, prices, descriptions, enchantment, enchantmentLevels);
    }

    public static <V extends Number> List<LoreNumberLevel<V>> getLoreNumberLevels(String name, int loreLine,
                                                                                  int decimal, String unit,
                                                                                  int startLevel,
                                                                                  List<ShopPrice> prices,
                                                                                  List<String> levelDescriptions,
                                                                                  List<V> values) {
        List<LoreNumberLevel<V>> levels = new ArrayList<>();

        Iterator<ShopPrice> priceIt = prices.listIterator();
        Iterator<String> descriptionIt = levelDescriptions.listIterator();
        Iterator<V> valueIt = values.listIterator();

        for (int level = startLevel; priceIt.hasNext() && descriptionIt.hasNext() && valueIt.hasNext(); level++) {
            levels.add(new LoreNumberLevel<>(name, loreLine, decimal, unit, level, priceIt.next(),
                    descriptionIt.next(), valueIt.next()));
        }
        return levels;
    }

    public static <V extends Number> List<LoreNumberLevel<V>> getLoreNumberLevels(String name, int loreLine,
                                                                                  int decimal, String unit,
                                                                                  int startLevel,
                                                                                  List<ShopPrice> prices,
                                                                                  String description, List<V> values) {

        List<String> descriptions = new ArrayList<>();
        for (int level = startLevel; level < Math.min(prices.size(), values.size()) + startLevel; level++) {
            descriptions.add(description);
        }
        return getLoreNumberLevels(name, loreLine, decimal, unit, startLevel, prices, descriptions, values);
    }

    public abstract ExItemStack levelUp(ExItemStack item);

    static class MaterialLevel extends ItemLevel<Material> {

        public MaterialLevel(int level, ShopPrice price, String description, Material material) {
            super(level, price, description, material);
        }

        @Override
        public ExItemStack levelUp(ExItemStack item) {
            return item.setExType(this.value);
        }
    }

    static class EnchantmentLevel extends ItemLevel<Tuple<Enchantment, Integer>> {

        public EnchantmentLevel(int level, ShopPrice price, String description, Enchantment enchantment,
                                int enchantmentLevel) {
            super(level, price, description, new Tuple<>(enchantment, enchantmentLevel));
        }

        @Override
        public ExItemStack levelUp(ExItemStack item) {
            return item.addExEnchantment(this.value.getA(), this.value.getB());
        }
    }

    public static class LoreNumberLevel<T extends Number> extends ItemLevel<T> {

        private final String name;
        private final String unit;
        private final String loreName;
        private final int loreLine;
        private final int multiplier;

        public LoreNumberLevel(String name, int loreLine, int decimal, String unit, int level, ShopPrice price,
                               String description, T value) {
            super(level, price, description, value);
            this.name = name;
            this.unit = unit;
            this.loreName = "§7" + this.name + ": §9";
            this.loreLine = loreLine;
            this.multiplier = (int) Math.pow(10, decimal);
        }

        @Override
        public ExItemStack levelUp(ExItemStack item) {
            return item.replaceLoreLine(this.loreLine, this.getLoreText(this.value));
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

        public String getLoreText(T value) {
            if (this.multiplier == 1) {
                return this.loreName + value.intValue() + " " + this.unit;
            }
            return this.loreName + value.doubleValue() + " " + this.unit;
        }

        public String getValueFromLore(List<String> lore) {
            if (lore.size() <= loreLine) return null;
            String text = lore.get(this.loreLine);
            if (text == null) return null;
            return text.replaceAll(this.loreName, "").replaceAll(" " + this.unit, "");
        }
    }
}
