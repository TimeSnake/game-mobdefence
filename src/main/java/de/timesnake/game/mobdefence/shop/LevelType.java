/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import de.timesnake.library.chat.ExTextColor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.Nullable;

public class LevelType {

  private final ExItemStack displayItem;
  private final String name;
  private final int baseLevel;
  private final int maxLevel;
  private final HashMap<Integer, Level<?>> levels;
  private int level;
  private Collection<String> conflictingTypes = new LinkedList<>();

  protected LevelType(Builder builder) {
    this.displayItem = builder.displayItem.cloneWithId();
    this.name = builder.name;
    this.level = builder.baseLevel;
    this.baseLevel = builder.baseLevel;
    this.maxLevel = this.baseLevel + builder.levels.size();

    this.displayItem.setDisplayName("§6" + name);

    this.levels = new HashMap<>();

    for (Level<?> level : builder.levels) {
      this.levels.put(level.getLevel(), level);
    }

    this.updateDescription();
  }

  public void updateDescription() {
    this.displayItem.setAmount(this.level == 0 ? 1 : this.level);

    Level<?> nextLevel = this.levels.get(this.level + 1);

    StringBuilder sb = new StringBuilder();
    if (this.conflictingTypes.size() > 0) {
      sb.append("§7Conflicts: §c");
      for (String conflictName : this.conflictingTypes) {
        sb.append(conflictName);
        sb.append(", ");
      }
      sb.delete(sb.length() - 2, sb.length());
    }

    if (nextLevel == null) {
      this.displayItem.setLore("§fLevel: §2" + this.level, "",
          "§7Next Level: §cmax level reached", "");
    } else {
      if (this.conflictingTypes.size() > 0) {
        this.displayItem.setLore("§7Level: §2" + this.level, "",
            "§7Next Level: §9" + nextLevel.getDescription(), "",
            "§7Price:        §2" + nextLevel.getPrice().toString(), "", sb.toString());
      } else {
        this.displayItem.setLore("§7Level: §2" + this.level, "",
            "§7Next Level: §9" + nextLevel.getDescription(), "",
            "§7Price:        §2" + nextLevel.getPrice().toString());
      }

    }
  }

  public ExItemStack getDisplayItem() {
    return displayItem;
  }

  public String getName() {
    return name;
  }

  public Collection<Level<?>> getLevels() {
    return this.levels.values();
  }

  public Level<?> getLevel(int level) {
    return this.levels.get(level);
  }

  @Nullable
  public List<Level<?>> getBaseLevels() {
    return this.levels.values().stream().filter(l -> l.getLevel() <= this.baseLevel).toList();
  }

  public Level<?> getFirstLevel() {
    Level<?> level = null;

    for (int i = this.baseLevel; level == null && i <= this.maxLevel; i++) {
      level = this.levels.get(i);
    }

    return level;
  }

  public Level<?> getLastLevel() {
    return this.levels.get(this.maxLevel);
  }

  public boolean tryLevelUp(MobDefUser user) {
    if (user == null) {
      return false;
    }

    if (this.level == this.maxLevel) {
      user.sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("Max level reached", ExTextColor.WARNING));
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return false;
    }

    Level<?> nextLevel = this.levels.get(this.level + 1);

    if (nextLevel == null) {
      user.sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("Nothing to level up", ExTextColor.WARNING));
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return false;
    }

    if (MobDefServer.getWaveNumber() < nextLevel.getUnlockWave()) {
      user.sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("This level is locked until wave ", ExTextColor.WARNING)
              .append(Component.text(nextLevel.getUnlockWave(), ExTextColor.VALUE)));
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return false;
    }

    if (!user.containsAtLeast(nextLevel.getPrice().asItem())) {
      user.sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("Not enough money", ExTextColor.WARNING));
      user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
      return false;
    }

    user.removeCertainItemStack(nextLevel.getPrice().asItem());

    nextLevel.run(user);

    this.level++;

    user.playNote(Instrument.STICKS, Note.natural(1, Note.Tone.C));

    this.updateDescription();
    user.updateInventory();

    return true;
  }

  public void setConflictingTypes(Collection<String> conflictingTypes) {
    this.conflictingTypes = Objects.requireNonNullElseGet(conflictingTypes, List::of);
    this.updateDescription();
  }

  public int getLevel() {
    return level;
  }

  public void resetLevel() {
    this.level = this.baseLevel;
  }

  public static class Builder {

    private final LinkedList<Level<?>> levels = new LinkedList<>();
    String name;
    private ExItemStack displayItem;
    private int baseLevel;
    private int levelCounter = 0;
    private ExItemStack currentLevelItemStack;
    private String currentDescription = "";
    private Enchantment currentEnchantment;
    private String currentLoreName = "";
    private int currentLoreLine = 1;
    private int currentDecimalDigits = 0;
    private String currentUnit = "";

    public Builder() {
    }

    public Builder baseLevel(int baseLevel) {
      this.baseLevel = baseLevel;
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

    public Builder levelDescription(String description) {
      this.currentDescription = description;
      return this;
    }

    public Builder levelEnchantment(Enchantment enchantment) {
      this.currentEnchantment = enchantment;
      return this;
    }

    public Builder levelLoreName(String loreName) {
      this.currentLoreName = loreName;
      return this;
    }

    public Builder levelLoreLine(int loreLine) {
      this.currentLoreLine = loreLine;
      return this;
    }

    public Builder levelDecimalDigit(int decimalDigits) {
      this.currentDecimalDigits = decimalDigits;
      return this;
    }

    public Builder levelUnit(String unit) {
      this.currentUnit = unit;
      return this;
    }

    public Builder levelItem(ExItemStack levelItem) {
      this.currentLevelItemStack = levelItem;
      return this;
    }

    public Builder addLvl(Price price, Consumer<MobDefUser> consumer) {
      return this.addLvl(price, this.currentDescription, consumer);
    }

    public Builder addLvl(Price price, String description, Consumer<MobDefUser> consumer) {
      this.levels.addLast(new Level<>(++this.levelCounter, price, description, consumer) {
        @Override
        public void run(MobDefUser user) {
          consumer.accept(user);
        }
      });
      return this;
    }

    public Builder addEnchantmentLvl(Price price, Enchantment enchantment, int level) {
      return this.addLvl(price, (ExItemStack i) -> i.addExEnchantment(enchantment, level));
    }

    public Builder addLvl(Price price, Function<ExItemStack, ExItemStack> function) {
      return this.addLvl(price, this.currentDescription, function);
    }

    public Builder addLvl(Price price, String description,
        Function<ExItemStack, ExItemStack> function) {
      this.levels.addLast(new Level.ItemLevel(this.currentLevelItemStack, ++this.levelCounter,
          price, description, function));
      return this;
    }

    public Builder addEnchantmentLvl(Price price, int level) {
      if (this.currentEnchantment == null) {
        throw new BuilderNotFullyInstantiatedException("enchantment type is not set");
      }
      return this.addLvl(price,
          (ExItemStack i) -> i.addExEnchantment(this.currentEnchantment, level));
    }

    public Builder addMaterialLvl(Price price, String description, Material material) {
      return this.addLvl(price, description, (ExItemStack i) -> i.setExType(material));
    }

    public <N extends Number> Builder addLoreLvl(Price price, N number) {
      return this.addLoreLvl(price, this.currentDescription, this.currentLoreName,
          this.currentLoreLine,
          this.currentDecimalDigits, this.currentUnit, number);
    }

    public <N extends Number> Builder addLoreLvl(Price price, String description,
        String loreName,
        int loreLine, int decimalDigits, String unit, N number) {
      this.levels.addLast(
          new Level.LoreNumberLevel<>(this.currentLevelItemStack, loreName, loreLine,
              decimalDigits, unit, ++this.levelCounter, price, description, number));
      return this;
    }

    public <N extends Number> Builder addLoreLvl(Price price, N number,
        Consumer<MobDefUser> consumer) {
      this.levels.addLast(
          new Level.LoreNumberLevel<>(this.currentLevelItemStack, this.currentLoreName,
              this.currentLoreLine, this.currentDecimalDigits, this.currentUnit,
              ++this.levelCounter, price,
              this.currentDescription, number) {
            @Override
            public void run(MobDefUser user) {
              super.run(user);
              consumer.accept(user);
            }
          });
      return this;
    }

    public Builder apply(Function<Builder, Builder> function) {
      return function.apply(this);
    }

    public LevelType build() {
      this.checkBuild();
      return new LevelType(this);
    }

    protected void checkBuild() {
      if (this.name == null) {
        throw new BuilderNotFullyInstantiatedException("name is null");
      }
      if (this.displayItem == null) {
        throw new BuilderNotFullyInstantiatedException("display item is null");
      }
      if (this.levels.isEmpty()) {
        throw new BuilderNotFullyInstantiatedException("level list is empty");
      }
    }

    @Override
    public Builder clone() {
      Builder cloned = new Builder();
      cloned.levels.addAll(levels);
      cloned.name = name;
      cloned.displayItem = displayItem != null ? displayItem.cloneWithId() : null;
      cloned.baseLevel = baseLevel;
      cloned.levelCounter = levelCounter;
      cloned.currentLevelItemStack =
          currentLevelItemStack != null ? currentLevelItemStack.cloneWithId() : null;
      cloned.currentDescription = currentDescription;
      cloned.currentEnchantment = currentEnchantment;
      cloned.currentLoreName = currentLoreName;
      cloned.currentLoreLine = currentLoreLine;
      cloned.currentDecimalDigits = currentDecimalDigits;
      cloned.currentUnit = currentUnit;
      return cloned;
    }

    public <V extends Number> V getNumberFromLore(ExItemStack item,
        Function<String, V> valueParser) {
      return valueParser.apply(this.getNumberFromLore(item));
    }

    public <T extends Number> String getNumberFromLore(ExItemStack item) {
      Iterator<Level<?>> it = this.levels.listIterator();
      Level<?> level;

      do {
        if (!it.hasNext()) {
          return "0";
        }
        level = it.next();
      }
      while (!(level instanceof Level.LoreNumberLevel<?>));
      return ((Level.LoreNumberLevel) level).getValueFromLore(item.getLore());
    }

  }
}
