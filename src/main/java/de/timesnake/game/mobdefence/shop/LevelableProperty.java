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
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class LevelableProperty {

  private static final Logger logger = LogManager.getLogger("mob-def.shop.property");

  private final ExItemStack displayItem;
  private final String name;
  private final int baseLevel;
  private final int maxLevel;
  private final HashMap<Integer, Level> levels;
  private int level;
  private Collection<String> conflictingTypes = new LinkedList<>();

  protected LevelableProperty(Builder builder) {
    this.displayItem = builder.displayItem.cloneWithId();
    this.name = builder.name;
    this.level = builder.defaultLevel;
    this.baseLevel = builder.defaultLevel;
    this.maxLevel = this.baseLevel + builder.levels.size();

    this.displayItem.setDisplayName("§6" + name);

    this.levels = new HashMap<>();

    for (Level level : builder.levels) {
      this.levels.put(level.getLevel(), level);
    }

    this.updateDescription();
  }

  public void updateDescription() {
    this.displayItem.setAmount(this.level == 0 ? 1 : this.level);

    Level nextLevel = this.levels.get(this.level + 1);

    StringBuilder sb = new StringBuilder();
    if (!this.conflictingTypes.isEmpty()) {
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
      if (!this.conflictingTypes.isEmpty()) {
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

  public Collection<Level> getLevels() {
    return this.levels.values();
  }

  public Level getLevel(int level) {
    return this.levels.get(level);
  }

  public List<Level> getBaseLevels() {
    return this.levels.values().stream().filter(l -> l.getLevel() <= this.baseLevel).toList();
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

    Level nextLevel = this.levels.get(this.level + 1);

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

  public static class Builder {

    private final LinkedList<Level> levels = new LinkedList<>();

    String name;
    private ExItemStack displayItem;
    private int defaultLevel;
    private int levelCounter = 0;
    private String currentDescription = "";
    private Enchantment currentEnchantment;
    private String currentLoreName = "";
    private int currentLoreLine = 1;
    private int currentDecimalDigits = 0;
    private String currentUnit = "";

    public Builder() {
    }

    public Builder defaultLevel(int defaultLevel) {
      this.defaultLevel = defaultLevel;
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

    public Builder addLevel(Price price, Consumer<MobDefUser> consumer) {
      return this.addLevel(price, this.currentDescription, consumer);
    }

    public Builder addLevel(Price price, String description, Consumer<MobDefUser> consumer) {
      this.levels.addLast(new Level(++this.levelCounter, price, description) {
        @Override
        public void run(MobDefUser user) {
          consumer.accept(user);
        }
      });
      return this;
    }

    public Builder addEnchantLevel(Price price, Enchantment enchantment, int level) {
      return this.addLevel(price, (ExItemStack i) -> i.addExEnchantment(enchantment, level));
    }

    public Builder addLevel(Price price, Function<ExItemStack, ExItemStack> function) {
      return this.addLevel(price, this.currentDescription, function);
    }

    public Builder addLevel(Price price, String description, Function<ExItemStack, ExItemStack> function) {
      this.levels.addLast(new ItemLevel(++this.levelCounter, price, description) {
        @Override
        public ExItemStack apply(ExItemStack exItemStack) {
          return function.apply(exItemStack);
        }
      });
      return this;
    }

    public Builder addEnchantLevel(Price price, int level) {
      if (this.currentEnchantment == null) {
        throw new BuilderNotFullyInstantiatedException("enchantment type is not set");
      }
      return this.addLevel(price, (ExItemStack i) -> i.addExEnchantment(this.currentEnchantment, level));
    }

    public Builder addMaterialLevel(Price price, String description, Material material) {
      return this.addLevel(price, description, (ExItemStack i) -> i.withType(material));
    }

    public <N extends Number> Builder addTagLevel(Price price, N number) {
      return this.addTagLevel(price, this.currentDescription, this.currentLoreName,
          this.currentLoreLine, this.currentDecimalDigits, this.currentUnit, number);
    }

    public Builder addTagLevel(Price price, String description, String loreName, int loreLine,
                               int decimalDigits, String unit, Number number) {
      if (number instanceof Integer) {
        this.levels.addLast(new ItemTagLevel.ItemIntegerTagLevel(loreName, loreLine,
            decimalDigits, unit, ++this.levelCounter, price, description, (Integer) number));
      } else {
        this.levels.addLast(new ItemTagLevel.ItemFloatTagLevel(loreName, loreLine,
            decimalDigits, unit, ++this.levelCounter, price, description, (Float) number));
      }

      return this;
    }

    public Builder addTagLevel(Price price, Number number, Consumer<MobDefUser> consumer) {
      if (number instanceof Integer) {
        this.levels.addLast(
            new ItemTagLevel.ItemIntegerTagLevel(this.currentLoreName,
                this.currentLoreLine, this.currentDecimalDigits, this.currentUnit, ++this.levelCounter, price,
                this.currentDescription, (Integer) number) {
              @Override
              public void run(MobDefUser user) {
                super.run(user);
                consumer.accept(user);
              }
            });
      } else {
        this.levels.addLast(new ItemTagLevel.ItemFloatTagLevel(this.currentLoreName,
            this.currentLoreLine, this.currentDecimalDigits, this.currentUnit, ++this.levelCounter, price,
            this.currentDescription, (Float) number) {
          @Override
          public void run(MobDefUser user) {
            super.run(user);
            consumer.accept(user);
          }
        });
      }
      return this;
    }

    public Builder apply(Function<Builder, Builder> function) {
      return function.apply(this);
    }

    public LevelableProperty build() {
      this.checkBuild();
      return new LevelableProperty(this);
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
      cloned.defaultLevel = defaultLevel;
      cloned.levelCounter = levelCounter;
      cloned.currentDescription = currentDescription;
      cloned.currentEnchantment = currentEnchantment;
      cloned.currentLoreName = currentLoreName;
      cloned.currentLoreLine = currentLoreLine;
      cloned.currentDecimalDigits = currentDecimalDigits;
      cloned.currentUnit = currentUnit;
      return cloned;
    }

    public <T extends Number> T getValueFromItem(ExItemStack item) {
      Iterator<Level> it = this.levels.listIterator();
      Level level;

      do {
        if (!it.hasNext()) {
          return null;
        }
        level = it.next();
      }
      while (!(level instanceof ItemTagLevel<?>));
      return (T) ((ItemTagLevel) level).getValueFromItem(item);
    }

  }
}
