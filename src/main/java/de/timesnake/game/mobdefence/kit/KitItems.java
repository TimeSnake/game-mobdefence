/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.*;
import de.timesnake.game.mobdefence.special.entity.*;
import de.timesnake.game.mobdefence.special.weapon.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.function.Function;

import static de.timesnake.game.mobdefence.shop.Currency.*;

public interface KitItems {

  String[] BLOCK_INFO = {"§7Can be destroyed by attackers",
      "§cCan only be placed 3 block up in the air"};

  ExItemStack OAK_FENCE_ITEM = new ExItemStack(Material.OAK_FENCE, 3)
      .setDisplayName("§6Fence")
      .setLore(BLOCK_INFO);
  SimpleGood.Builder OAK_FENCE = new SimpleGood.Builder()
      .giveItems(OAK_FENCE_ITEM)
      .price(new Price(2, BRONZE), 1, 8)
      .description(BLOCK_INFO)
      .slot(0);

  ExItemStack OAK_FENCE_GATE_ITEM = new ExItemStack(Material.OAK_FENCE_GATE, 3)
      .setDisplayName("§6Fence Gate")
      .setLore(BLOCK_INFO);
  SimpleGood.Builder OAK_FENCE_GATE = new SimpleGood.Builder()
      .giveItems(OAK_FENCE_GATE_ITEM)
      .price(new Price(2, BRONZE), 1, 6)
      .description(BLOCK_INFO)
      .slot(1);

  ExItemStack OAK_PLANKS_ITEM = new ExItemStack(Material.OAK_PLANKS, 4)
      .setDisplayName("§6Plank")
      .setLore(BLOCK_INFO);
  SimpleGood.Builder OAK_PLANKS = new SimpleGood.Builder()
      .giveItems(OAK_PLANKS_ITEM)
      .price(new Price(1, SILVER), 1, 10)
      .description(BLOCK_INFO)
      .slot(9);

  ExItemStack OAK_SLAB_ITEM = new ExItemStack(Material.OAK_SLAB, 4)
      .setDisplayName("§6Slab")
      .setLore(BLOCK_INFO);
  SimpleGood.Builder OAK_SLAB = new SimpleGood.Builder()
      .giveItems(OAK_SLAB_ITEM)
      .price(new Price(1, SILVER), 1, 10)
      .description(BLOCK_INFO)
      .slot(10);

  ExItemStack IRON_BARS_ITEM = new ExItemStack(Material.IRON_BARS, 3)
      .setDisplayName("§6Iron Bars")
      .setLore(BLOCK_INFO);
  SimpleGood.Builder IRON_BARS = new SimpleGood.Builder()
      .giveItems(IRON_BARS_ITEM)
      .price(new Price(2, SILVER), 1, 8)
      .description(BLOCK_INFO)
      .slot(18);

  ExItemStack COBBLESTONE_WALL_ITEM = new ExItemStack(Material.COBBLESTONE_WALL, 2)
      .setDisplayName("§6Wall")
      .setLore(BLOCK_INFO);
  SimpleGood.Builder COBBLESTONE_WALL = new SimpleGood.Builder()
      .giveItems(COBBLESTONE_WALL_ITEM)
      .price(new Price(2, SILVER), 1, 8)
      .description(BLOCK_INFO)
      .slot(19);

  SimpleGood.Builder STONE_AXE = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.STONE_AXE)
          .setDisplayName("§6Axe")
          .unbreakable())
      .price(new Price(4, BRONZE))
      .notRebuyable()
      .slot(3);

  SimpleGood.Builder IRON_PICKAXE = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.IRON_PICKAXE)
          .setDisplayName("§6Pickaxe")
          .unbreakable())
      .price(new Price(8, BRONZE))
      .notRebuyable()
      .slot(4);

  SimpleGood.Builder APPLE = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.APPLE, 6).setDisplayName("§6Apple"))
      .price(new Price(1, SILVER))
      .slot(3);

  SimpleGood.Builder PUMPKIN_PIE = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.PUMPKIN_PIE, 4).setDisplayName("§6Pumpkin Pie"))
      .price(new Price(2, SILVER))
      .slot(4);

  ExItemStack BEEF = new ExItemStack(Material.COOKED_BEEF, 12)
      .setSlot(7)
      .setDisplayName("§6Cooked Beef");
  SimpleGood.Builder COOKED_BEEF = new SimpleGood.Builder()
      .giveItems(BEEF.cloneWithId().asQuantity(12))
      .price(new Price(4, BRONZE))
      .slot(5);

  ExItemStack KELP = new ExItemStack(Material.DRIED_KELP, 8).setDisplayName("§6Dried Kelp")
      .setLore("§7Healthy Fast Food")
      .onInteract(e -> {
        User user = e.getUser();

        e.setCancelled(true);

        if (user.getPlayer().getFoodLevel() == 20) {
          return;
        }

        user.removeCertainItemStack(MobDefKit.KELP.cloneWithId().asOne());
        double food = user.getPlayer().getFoodLevel();
        user.getPlayer().setFoodLevel(food <= 19 ? ((int) (food + 1)) : 20);
        user.getPlayer().setSaturation(3);
      });

  SimpleGood.Builder DRIED_KELP = new SimpleGood.Builder()
      .giveItems(KELP)
      .price(new Price(1, BRONZE))
      .slot(12);

  SimpleGood.Builder GOLDEN_APPLE = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.GOLDEN_APPLE, 1).setDisplayName("§6Golden Apple"))
      .price(new Price(2, SILVER), 1, 6)
      .slot(13);

  SimpleGood.Builder GOLDEN_CARROT = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.GOLDEN_CARROT, 6).setDisplayName("§6Golden Carrot"))
      .price(new Price(1, GOLD))
      .slot(14);

  SimpleGood.Builder MILK = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.MILK_BUCKET).setDisplayName("§6Milk"))
      .price(new Price(2, BRONZE))
      .slot(21);

  SimpleGood.Builder ENDER_PEARL = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.ENDER_PEARL).setDisplayName("§6Ender Pearl"))
      .price(new Price(1, GOLD))
      .slot(22);

  SimpleGood.Builder SPEED = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.SWIFTNESS).setDisplayName("§6Speed"))
      .price(new Price(1, SILVER))
      .slot(7);

  SimpleGood.Builder INSTANT_HEAL = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.STRONG_HEALING).setDisplayName("§6Instant " +
                                                                                                         "Heal"))
      .price(new Price(6, BRONZE), 1, 32)
      .slot(8);

  SimpleGood.Builder IRON_GOLEM = new SimpleGood.Builder()
      .giveItems(MobDefIronGolem.ITEM.cloneWithId())
      .price(new Price(12, SILVER), 1, 4)
      .slot(46);

  SimpleGood.Builder REGEN = new SimpleGood.Builder()
      .display(new ExItemStack(Material.BEACON).setDisplayName("§cVillager Regeneration"))
      .give(u -> MobDefServer.getMobDefUserManager().getCoreRegeneration().run(u))
      .price(new Price(4, Currency.EMERALD), 1, 8)
      .notRebuyable()
      .slot(47);

  ExItemStack WEAPONS = new ExItemStack(Material.IRON_SWORD).hideAll();
  ExItemStack ARMOR = new ExItemStack(Material.CHAINMAIL_CHESTPLATE).hideAll();

  ExItemStack ARMOR_HELMET = new ExItemStack(Material.CHAINMAIL_HELMET);
  ExItemStack ARMOR_CHESTPLATE = new ExItemStack(Material.CHAINMAIL_CHESTPLATE);
  ExItemStack ARMOR_LEGGINGS = new ExItemStack(Material.CHAINMAIL_LEGGINGS);
  ExItemStack ARMOR_BOOTS = new ExItemStack(Material.CHAINMAIL_BOOTS);

  LevelableProperty.Builder PROTECTION = new LevelableProperty.Builder()
      .name("Protection")
      .display(new ExItemStack(Material.TURTLE_HELMET))
      .defaultLevel(0)
      .levelDescription("+1 Protection")
      .levelEnchantment(Enchantment.PROTECTION);

  Function<LevelableProperty.Builder, LevelableProperty.Builder> PROTECTION_LEVEL = b ->
      b.addEnchantLevel(new Price(12, BRONZE), 1)
          .addEnchantLevel(new Price(10, SILVER), 2)
          .addEnchantLevel(new Price(6, GOLD), 3)
          .addEnchantLevel(new Price(20, SILVER), 4)
          .addEnchantLevel(new Price(12, GOLD), 5)
          .addEnchantLevel(new Price(46, BRONZE), 6)
          .addEnchantLevel(new Price(30, SILVER), 7)
          .addEnchantLevel(new Price(64, BRONZE), 8)
          .addEnchantLevel(new Price(40, SILVER), 9)
          .addEnchantLevel(new Price(31, GOLD), 10);

  LevelableProperty.Builder PROJECTILE_PROTECTION = new LevelableProperty.Builder()
      .name("Projectile Protection")
      .display(new ExItemStack(Material.ARROW))
      .defaultLevel(0)
      .levelDescription("+1 Projectile Protection")
      .levelEnchantment(Enchantment.PROJECTILE_PROTECTION);

  Function<LevelableProperty.Builder, LevelableProperty.Builder> PROJECTILE_PROTECTION_LEVEL = b ->
      b.addEnchantLevel(new Price(3, SILVER), 1)
          .addEnchantLevel(new Price(8, BRONZE), 2)
          .addEnchantLevel(new Price(16, BRONZE), 3)
          .addEnchantLevel(new Price(5, GOLD), 4)
          .addEnchantLevel(new Price(13, SILVER), 5)
          .addEnchantLevel(new Price(32, BRONZE), 6)
          .addEnchantLevel(new Price(18, SILVER), 7)
          .addEnchantLevel(new Price(24, SILVER), 8)
          .addEnchantLevel(new Price(16, GOLD), 9)
          .addEnchantLevel(new Price(64, BRONZE), 10);

  ExItemStack MELEE_BASE_ARMOR_HELMET = new ExItemStack(Material.LEATHER_HELMET)
      .setSlot(EquipmentSlot.HEAD)
      .unbreakable()
      .immutable();
  ExItemStack MELEE_BASE_ARMOR_CHESTPLATE = new ExItemStack(Material.LEATHER_CHESTPLATE)
      .setSlot(EquipmentSlot.CHEST)
      .unbreakable()
      .immutable();
  ExItemStack MELEE_BASE_ARMOR_LEGGINGS = new ExItemStack(Material.LEATHER_LEGGINGS)
      .setSlot(EquipmentSlot.LEGS)
      .unbreakable()
      .immutable();
  ExItemStack MELEE_BASE_ARMOR_BOOTS = new ExItemStack(Material.LEATHER_BOOTS)
      .setSlot(EquipmentSlot.FEET)
      .unbreakable()
      .immutable();

  LevelableProperty.Builder MELEE_ARMOR_HELMET_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(MELEE_BASE_ARMOR_HELMET)
      .addMaterialLevel(null, null, Material.LEATHER_HELMET)
      .addMaterialLevel(new Price(4, BRONZE), "Chainmail Helmet", Material.CHAINMAIL_HELMET)
      .addMaterialLevel(new Price(8, SILVER), "Iron Helmet", Material.IRON_HELMET)
      .addMaterialLevel(new Price(6, GOLD), "Diamond Helmet", Material.DIAMOND_HELMET)
      .addMaterialLevel(new Price(17, GOLD), "Netherite Helmet", Material.NETHERITE_HELMET);
  UpgradeableGoodItem.Builder MELEE_ARMOR_HELMET = new UpgradeableGoodItem.Builder()
      .name("Helmet")
      .display(ARMOR_HELMET)
      .startItem(MELEE_BASE_ARMOR_HELMET)
      .addLevelableProperty(MELEE_ARMOR_HELMET_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_HELMET)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_HELMET)
          .apply(PROJECTILE_PROTECTION_LEVEL));
  LevelableProperty.Builder MELEE_ARMOR_CHESTPLATE_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(MELEE_BASE_ARMOR_CHESTPLATE)
      .addMaterialLevel(null, null, Material.LEATHER_CHESTPLATE)
      .addMaterialLevel(new Price(6, BRONZE), "Chainmail Chestplate",
          Material.CHAINMAIL_CHESTPLATE)
      .addMaterialLevel(new Price(11, SILVER), "Iron Chestplate", Material.IRON_CHESTPLATE)
      .addMaterialLevel(new Price(8, GOLD), "Diamond Chestplate", Material.DIAMOND_CHESTPLATE)
      .addMaterialLevel(new Price(22, GOLD), "Netherite Chestplate",
          Material.NETHERITE_CHESTPLATE);
  UpgradeableGoodItem.Builder MELEE_ARMOR_CHESTPLATE = new UpgradeableGoodItem.Builder()
      .name("Chestplate")
      .display(ARMOR_CHESTPLATE)
      .startItem(MELEE_BASE_ARMOR_CHESTPLATE)
      .addLevelableProperty(MELEE_ARMOR_CHESTPLATE_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_CHESTPLATE)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_CHESTPLATE)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelableProperty.Builder MELEE_ARMOR_LEGGINGS_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(MELEE_BASE_ARMOR_LEGGINGS)
      .addMaterialLevel(null, null, Material.LEATHER_LEGGINGS)
      .addMaterialLevel(new Price(5, BRONZE), "Chainmail Leggings", Material.CHAINMAIL_LEGGINGS)
      .addMaterialLevel(new Price(10, SILVER), "Iron Leggings", Material.IRON_LEGGINGS)
      .addMaterialLevel(new Price(7, GOLD), "Diamond Leggings", Material.DIAMOND_LEGGINGS)
      .addMaterialLevel(new Price(19, GOLD), "Netherite Leggings", Material.NETHERITE_LEGGINGS);
  UpgradeableGoodItem.Builder MELEE_ARMOR_LEGGINGS = new UpgradeableGoodItem.Builder()
      .name("Leggings")
      .display(ARMOR_LEGGINGS)
      .startItem(MELEE_BASE_ARMOR_LEGGINGS)
      .addLevelableProperty(MELEE_ARMOR_LEGGINGS_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_LEGGINGS)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_LEGGINGS)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelableProperty.Builder MELEE_ARMOR_BOOTS_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(MELEE_BASE_ARMOR_BOOTS)
      .addMaterialLevel(null, null, Material.LEATHER_BOOTS)
      .addMaterialLevel(new Price(1, BRONZE), "Chainmail Boots", Material.CHAINMAIL_BOOTS)
      .addMaterialLevel(new Price(30, SILVER), "Iron Boots", Material.IRON_BOOTS)
      .addMaterialLevel(new Price(18, GOLD), "Diamond Boots", Material.DIAMOND_BOOTS)
      .addMaterialLevel(new Price(16, GOLD), "Netherite Boots", Material.NETHERITE_BOOTS);
  UpgradeableGoodItem.Builder MELEE_ARMOR_BOOTS = new UpgradeableGoodItem.Builder()
      .name("Boots")
      .display(ARMOR_BOOTS)
      .startItem(MELEE_BASE_ARMOR_BOOTS)
      .addLevelableProperty(MELEE_ARMOR_BOOTS_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_BOOTS)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_BOOTS)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  Shop.Builder LUMBERJACK_ARMOR = new Shop.Builder()
      .name("Armor")
      .slot(1)
      .display(ARMOR)
      .addUpgradeable(MELEE_ARMOR_HELMET, MELEE_ARMOR_CHESTPLATE, MELEE_ARMOR_LEGGINGS,
          MELEE_ARMOR_BOOTS)
      .type(Shop.Builder.Type.USER);
  ExItemStack RANGED_BASE_ARMOR_HELMET = new ExItemStack(Material.LEATHER_HELMET).setSlot(
      EquipmentSlot.HEAD).unbreakable();
  ExItemStack RANGED_BASE_ARMOR_CHESTPLATE = new ExItemStack(Material.LEATHER_CHESTPLATE).setSlot(
      EquipmentSlot.CHEST).unbreakable();
  ExItemStack RANGED_BASE_ARMOR_LEGGINGS = new ExItemStack(Material.LEATHER_LEGGINGS).setSlot(
      EquipmentSlot.LEGS).unbreakable();
  ExItemStack RANGED_BASE_ARMOR_BOOTS = new ExItemStack(Material.LEATHER_BOOTS).setSlot(
      EquipmentSlot.FEET).unbreakable();
  LevelableProperty.Builder RANGED_ARMOR_HELMET_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(RANGED_BASE_ARMOR_HELMET)
      .addMaterialLevel(null, null, Material.LEATHER_HELMET)
      .addMaterialLevel(new Price(7, SILVER), "Golden Helmet", Material.GOLDEN_HELMET)
      .addMaterialLevel(new Price(7, GOLD), "Chainmail Helmet", Material.CHAINMAIL_HELMET)
      .addMaterialLevel(new Price(11, SILVER), "Iron Helmet", Material.IRON_HELMET);
  UpgradeableGoodItem.Builder RANGED_ARMOR_HELMET = new UpgradeableGoodItem.Builder()
      .name("Helmet")
      .price(new Price(4, BRONZE))
      .startItem(RANGED_BASE_ARMOR_HELMET)
      .display(ARMOR_HELMET)
      .addLevelableProperty(RANGED_ARMOR_HELMET_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_HELMET)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_HELMET)
          .apply(PROJECTILE_PROTECTION_LEVEL));
  LevelableProperty.Builder RANGED_ARMOR_CHESTPLATE_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(RANGED_BASE_ARMOR_CHESTPLATE)
      .addMaterialLevel(null, null, Material.LEATHER_CHESTPLATE)
      .addMaterialLevel(new Price(6, SILVER), "Golden Chestplate", Material.GOLDEN_CHESTPLATE)
      .addMaterialLevel(new Price(24, BRONZE), "Chainmail Chestplate",
          Material.CHAINMAIL_CHESTPLATE)
      .addMaterialLevel(new Price(12, SILVER), "Iron Chestplate", Material.IRON_CHESTPLATE)
      .addMaterialLevel(new Price(18, GOLD), "Diamond Chestplate", Material.DIAMOND_CHESTPLATE);
  UpgradeableGoodItem.Builder RANGED_ARMOR_CHESTPLATE = new UpgradeableGoodItem.Builder()
      .name("Chestplate")
      .price(new Price(7, BRONZE))
      .startItem(RANGED_BASE_ARMOR_CHESTPLATE)
      .display(ARMOR_CHESTPLATE)
      .addLevelableProperty(RANGED_ARMOR_CHESTPLATE_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_CHESTPLATE)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_CHESTPLATE)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelableProperty.Builder RANGED_ARMOR_LEGGINGS_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(RANGED_BASE_ARMOR_LEGGINGS)
      .addMaterialLevel(null, null, Material.LEATHER_LEGGINGS)
      .addMaterialLevel(new Price(5, SILVER), "Golden Leggings", Material.GOLDEN_LEGGINGS)
      .addMaterialLevel(new Price(10, SILVER), "Chainmail Leggings",
          Material.CHAINMAIL_LEGGINGS)
      .addMaterialLevel(new Price(16, GOLD), "Iron Leggings", Material.IRON_LEGGINGS);
  UpgradeableGoodItem.Builder RANGED_ARMOR_LEGGINGS = new UpgradeableGoodItem.Builder()
      .name("Leggings")
      .price(new Price(4, BRONZE))
      .startItem(RANGED_BASE_ARMOR_LEGGINGS)
      .display(ARMOR_LEGGINGS)
      .addLevelableProperty(RANGED_ARMOR_LEGGINGS_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_LEGGINGS)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_LEGGINGS)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelableProperty.Builder RANGED_ARMOR_BOOTS_TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(RANGED_BASE_ARMOR_BOOTS)
      .addMaterialLevel(null, null, Material.LEATHER_BOOTS)
      .addMaterialLevel(new Price(1, GOLD), "Golden Leggings", Material.GOLDEN_BOOTS)
      .addMaterialLevel(new Price(6, SILVER), "Chainmail Leggings", Material.CHAINMAIL_BOOTS)
      .addMaterialLevel(new Price(8, GOLD), "Iron Leggings", Material.IRON_BOOTS)
      .addMaterialLevel(new Price(15, SILVER), "Diamond Boots", Material.DIAMOND_BOOTS);
  UpgradeableGoodItem.Builder RANGED_ARMOR_BOOTS = new UpgradeableGoodItem.Builder()
      .name("Boots")
      .price(new Price(4, BRONZE))
      .startItem(RANGED_BASE_ARMOR_BOOTS)
      .display(ARMOR_BOOTS)
      .addLevelableProperty(RANGED_ARMOR_BOOTS_TYPE)
      .addLevelableProperty(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_BOOTS)
          .apply(PROTECTION_LEVEL))
      .addLevelableProperty(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_BOOTS)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  Shop.Builder RANGED_ARMOR = new Shop.Builder()
      .name("Armor")
      .display(ARMOR)
      .slot(1)
      .addUpgradeable(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS,
          RANGED_ARMOR_BOOTS)
      .type(Shop.Builder.Type.USER);
  SimpleGood.Builder FIRE_GOLD_SWORD = new SimpleGood.Builder()
      .giveItems(new ExItemStack(Material.GOLDEN_SWORD)
          .setDisplayName("§6Fire Sword")
          .addExEnchantment(Enchantment.FIRE_ASPECT, 2))
      .price(new Price(2, Currency.SILVER))
      .slot(46);
  SimpleGood.Builder ALCHEMIST_SPEED = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(Material.POTION, 1, "§6Speed", PotionType.STRONG_SWIFTNESS
      ))
      .price(new Price(4, BRONZE))
      .slot(46);
  SimpleGood.Builder WIZARD_REGEN = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration",
          PotionEffectType.REGENERATION, 10 * 20, 2))
      .price(new Price(4, BRONZE))
      .slot(46);
  SimpleGood.Builder LUMBER_SPEED = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(Material.POTION, 1, "§6Speed", PotionType.STRONG_SWIFTNESS
      ))
      .price(new Price(4, BRONZE))
      .slot(46);
  SimpleGood.Builder LUMBER_STRENGTH = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(Material.POTION, 1, "§6Strength", PotionType.STRENGTH
      ))
      .price(new Price(3, SILVER))
      .slot(47);
  SimpleGood.Builder LUMBER_REGENERATION = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration",
          PotionEffectType.REGENERATION, 15, 3))
      .price(new Price(3, SILVER))
      .slot(48);
  SimpleGood.Builder IRON_SKIN = new SimpleGood.Builder()
      .giveItems(ExItemStack.getPotion(ExItemStack.PotionMaterial.DRINK, 1, "Iron Skin",
          PotionEffectType.RESISTANCE, 60 * 20, 3))
      .price(new Price(8, SILVER))
      .slot(47);
  Shop.Builder KNIGHT_WEAPONS = new Shop.Builder()
      .name("Weapons")
      .slot(0)
      .display(WEAPONS)
      .addUpgradeable(Sword.SWORD, SwingSword.SWORD)
      .addTrade(FIRE_GOLD_SWORD)
      .type(Shop.Builder.Type.USER);
  Shop.Builder LUMBERJACK_WEAPONS = new Shop.Builder()
      .name("Weapons")
      .slot(0)
      .display(WEAPONS)
      .addUpgradeable(LumberAxe.AXE, BoomerangAxe.BOOMERANG_AXE, SheepSpawner.LEVEL_ITEM,
          DogSpawner.LEVEL_ITEM)
      .addTrade(LUMBER_SPEED, LUMBER_STRENGTH, LUMBER_REGENERATION)
      .type(Shop.Builder.Type.USER);
  Shop.Builder WIZARD_WEAPONS = new Shop.Builder()
      .name("Weapons")
      .slot(0)
      .display(WEAPONS)
      .addUpgradeable(Wand.WAND)
      .addUpgradeable(SafeSphere.SAFE_SPHERE)
      .addTrade(WIZARD_REGEN, IRON_SKIN)
      .type(Shop.Builder.Type.USER);

  Shop.Builder KNIGHT_ARMOR = new Shop.Builder()
      .name("Armor")
      .slot(1)
      .display(ARMOR)
      .addUpgradeable(MELEE_ARMOR_HELMET, MELEE_ARMOR_CHESTPLATE, MELEE_ARMOR_LEGGINGS,
          MELEE_ARMOR_BOOTS)
      .addTrade(IRON_SKIN)
      .type(Shop.Builder.Type.USER);
  Shop.Builder ALCHEMIST_WEAPONS = new Shop.Builder()
      .name("Weapons")
      .slot(0)
      .display(WEAPONS)
      .addUpgradeable(FireHoe.FIRE_HOE, FireStaff.FIRE_STAFF, Iceball.ICEBALL)
      .addTrade(ALCHEMIST_SPEED, Snowman.SNOWMAN, MobDefBlaze.BLAZE)
      .type(Shop.Builder.Type.USER);
}
