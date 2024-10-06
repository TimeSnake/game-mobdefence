/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;

public abstract class ItemTagLevel<T extends Number> extends ItemLevel {

  protected final String name;
  protected final String unit;
  protected final String loreName;
  protected final int loreLine;
  protected final T value;

  protected final NamespacedKey key;
  protected final PersistentDataType<T, T> dataType;
  protected final DecimalFormat decimalFormat;

  public ItemTagLevel(ExItemStack targetItem, String name, int loreLine, int decimal, String unit, int level,
                      Price price,
                      String description, T value) {
    super(targetItem, level, price, description);
    this.name = name;
    this.unit = unit;
    this.loreName = "§7" + this.name + ": §9";
    this.loreLine = loreLine;
    this.value = value;

    this.key = NamespacedKey.fromString(this.name.toLowerCase().replaceAll(" ", "_"));
    this.dataType = this.loadDataType();
    this.decimalFormat = new DecimalFormat("#" + (decimal > 0 ? "." : "") + "0".repeat(decimal));
  }

  @Override
  public ExItemStack apply(ExItemStack item) {
    item.editMeta(m -> m.getPersistentDataContainer().set(this.key, this.dataType, this.value));
    item.replaceLoreLine(this.loreLine, this.loreName + this.decimalFormat.format(this.value) + " " + this.unit);
    return item;
  }

  public T getValueFromItem(ExItemStack item) {
    return this.parseValueFromDataContainer(item.getItemMeta().getPersistentDataContainer());
  }

  protected abstract PersistentDataType<T, T> loadDataType();

  protected abstract T parseValueFromDataContainer(PersistentDataContainer value);

  public static class ItemIntegerTagLevel extends ItemTagLevel<Integer> {

    public ItemIntegerTagLevel(ExItemStack targetItem, String name, int loreLine, int decimal, String unit, int level,
                               Price price, String description, Integer value) {
      super(targetItem, name, loreLine, decimal, unit, level, price, description, value);
    }

    @Override
    protected PersistentDataType<Integer, Integer> loadDataType() {
      return PersistentDataType.INTEGER;
    }

    @Override
    protected Integer parseValueFromDataContainer(PersistentDataContainer value) {
      return value.get(this.key, PersistentDataType.INTEGER);
    }
  }

  public static class ItemFloatTagLevel extends ItemTagLevel<Float> {

    public ItemFloatTagLevel(ExItemStack item, String name, int loreLine, int decimal, String unit, int level,
                             Price price, String description, Float value) {
      super(item, name, loreLine, decimal, unit, level, price, description, value);
    }

    @Override
    protected PersistentDataType<Float, Float> loadDataType() {
      return PersistentDataType.FLOAT;
    }

    @Override
    protected Float parseValueFromDataContainer(PersistentDataContainer value) {
      return value.get(this.key, PersistentDataType.FLOAT);
    }
  }
}
