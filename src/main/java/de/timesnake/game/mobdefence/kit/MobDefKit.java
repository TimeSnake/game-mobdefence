/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.loungebridge.util.user.Kit;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Shop;
import de.timesnake.game.mobdefence.special.PotionGenerator;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.MobTracker;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class MobDefKit extends Kit implements KitItems {

  public static final MobDefKit KNIGHT = new Builder()
      .id(1)
      .name("Knight")
      .material(Material.IRON_SWORD)
      .addDescription("§fWeapons: §7Sword, Axe", "§fArmor: §7Strong", "", "§7Resistance aura")
      .addItems(new ExItemStack(Material.SHIELD).unbreakable().setSlot(EquipmentSlot.OFF_HAND),
          MobDefKit.BEEF, MobTracker.TRACKER)
      .addShopSuppliers(KNIGHT_WEAPONS, KNIGHT_ARMOR,
          () -> MobDefServer.getBaseShops().getBasicShop(),
          () -> MobDefServer.getBaseShops().getBlockShop(),
          () -> MobDefServer.getBaseShops().getTeamShop())
      .build();

  public static final MobDefKit LUMBERJACK = new Builder()
      .id(5)
      .name("Lumberjack")
      .material(Material.IRON_AXE)
      .addDescription("§fWeapon: §7Axe", "§fArmor: §7Strong", "",
          "§7Speed and Strength Potions", "§7Regeneration effect after mob kill",
          "§7Sheep as distraction", "§7Dogs as companions")
      .addItems(MobDefKit.BEEF, MobTracker.TRACKER)
      .addShopSuppliers(LUMBERJACK_WEAPONS, LUMBERJACK_ARMOR,
          () -> MobDefServer.getBaseShops().getBasicShop(),
          () -> MobDefServer.getBaseShops().getBlockShop(),
          () -> MobDefServer.getBaseShops().getTeamShop())
      .build();

  public static final MobDefKit ALCHEMIST = new Builder()
      .id(3)
      .name("Alchemist")
      .material(Material.BLAZE_POWDER)
      .addDescription("§fWeapons: §7Fire Hoe, Fire Staff, Iceball", "§fArmor: §7Weak",
          "", "§7Resistant against fire", "§7Defence Snowmen, Blazes")
      .addItems(MobDefKit.BEEF, MobTracker.TRACKER)
      .addShopSuppliers(ALCHEMIST_WEAPONS, RANGED_ARMOR,
          () -> MobDefServer.getBaseShops().getBasicShop(),
          () -> MobDefServer.getBaseShops().getBlockShop(),
          () -> MobDefServer.getBaseShops().getTeamShop())
      .build();

  public static final MobDefKit WIZARD = new Builder()
      .id(4)
      .name("Wizard")
      .material(ExItemStack.getPotion(Material.POTION, PotionType.HEALING
      ).getType())
      .addDescription("§fWeapon: §7Wand", "§fArmor: §7Weak", "", "§7Instant Heal Potions")
      .addItems(MobDefKit.BEEF, PotionGenerator.HEALING, MobTracker.TRACKER)
      .addShopSuppliers(WIZARD_WEAPONS, RANGED_ARMOR,
          () -> MobDefServer.getBaseShops().getBasicShop(),
          () -> MobDefServer.getBaseShops().getBlockShop(),
          () -> MobDefServer.getBaseShops().getTeamShop())
      .build();

  public final List<Supplier<Shop>> shopSuppliers;

  public MobDefKit(Builder builder) {
    super(builder);
    this.shopSuppliers = builder.shopSuppliers;
  }

  public List<Supplier<Shop>> getShopSuppliers() {
    return this.shopSuppliers;
  }

  public KitShops getShop(MobDefUser user, boolean loadItemBase) {
    return new KitShops(user, loadItemBase);
  }

  public static class Builder extends Kit.Builder<Builder> {

    private final LinkedList<Supplier<Shop>> shopSuppliers = new LinkedList<>();

    @SafeVarargs
    public final Builder addShopSuppliers(Supplier<Shop>... suppliers) {
      for (Supplier<Shop> supplier : suppliers) {
        this.shopSuppliers.addLast(supplier);
      }
      return this;
    }

    @Override
    public MobDefKit build() {
      this.checkBuild();
      return new MobDefKit(this);
    }
  }

}
