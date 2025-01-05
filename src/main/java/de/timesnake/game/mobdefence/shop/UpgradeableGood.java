/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.shop;

import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.library.basic.util.BuilderNotFullyInstantiatedException;
import de.timesnake.library.basic.util.LinkedMultiKeyMap;

import java.util.*;

public abstract class UpgradeableGood {

  protected final String name;
  protected final ExItemStack displayItem;

  protected final LinkedMultiKeyMap<ExItemStack, String, LevelableProperty> levelType = new LinkedMultiKeyMap<>();
  protected final Map<String, Collection<String>> conflictingTypes;

  protected UpgradeableGood(Builder builder) {
    this.name = builder.name;
    this.displayItem = builder.displayItem.cloneWithId();
    this.displayItem.setDisplayName("§c" + this.name);

    for (LevelableProperty levelType :
        builder.levelTypeBuilders.stream().map(LevelableProperty.Builder::build).toList()) {
      levelType.setConflictingTypes(builder.conflictingTypes.get(levelType.getName()));
      this.levelType.put(levelType.getDisplayItem(), levelType.getName(), levelType);
    }

    this.conflictingTypes = builder.conflictingTypes;
  }

  protected LevelableProperty isConflicting(LevelableProperty levelType) {
    return this.conflictingTypes.getOrDefault(levelType.getName(), List.of()).stream()
        .map(this.levelType::get2)
        .filter(conflict -> conflict != null && conflict.getLevel() > 0)
        .findFirst().orElse(null);
  }

  public void fillInventoryRow(ExInventory inv, int startSlot) {
    inv.setItemStack(startSlot, this.getDisplayItem());
    startSlot += 2;

    for (Iterator<LevelableProperty> iterator = this.levelType.values().iterator(); startSlot % 9 != 8 && iterator.hasNext(); startSlot++) {
      LevelableProperty levelType = iterator.next();
      levelType.getDisplayItem().setSlot(startSlot);
      inv.setItemStack(startSlot, levelType.getDisplayItem());
    }
  }

  public void loadBaseForUser(MobDefUser user) {
    for (LevelableProperty levelType : this.levelType.values()) {
      if (levelType.getBaseLevels() != null) {
        for (Level level : levelType.getBaseLevels()) {
          level.run(user);
        }
      }
    }
  }

  public abstract void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item);

  public ExItemStack getDisplayItem() {
    return displayItem;
  }

  public static abstract class Builder {

    protected final LinkedList<LevelableProperty.Builder> levelTypeBuilders = new LinkedList<>();
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

    public Builder addLevelableProperty(LevelableProperty.Builder levelTypeBuilder) {
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

    public Builder addConflictToLvlType(LevelableProperty.Builder levelTypeBuilder1,
                                        LevelableProperty.Builder levelTypeBuilder2) {
      this.conflictingTypes.computeIfAbsent(levelTypeBuilder1.name, k -> new HashSet<>())
          .add(levelTypeBuilder2.name);
      this.conflictingTypes.computeIfAbsent(levelTypeBuilder2.name, k -> new HashSet<>())
          .add(levelTypeBuilder1.name);
      return this;
    }

    public abstract UpgradeableGood build();

  }
}
