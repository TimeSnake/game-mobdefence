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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static de.timesnake.game.mobdefence.shop.Currency.*;

public interface KitItems {

  String[] BLOCK_INFO = {"§7Can be destroyed by attackers",
      "§cCan only be placed 3 block up in the air"};

  Map<Material, ExItemStack> BLOCK_ITEM_BY_TYPE = new HashMap<>();

  ExItemStack OAK_FENCE_ITEM = new ExItemStack(Material.OAK_FENCE, 3)
      .setDisplayName("§6Fence")
      .setLore(BLOCK_INFO);
  Trade.Builder OAK_FENCE = new Trade.Builder()
      .giveItems(OAK_FENCE_ITEM)
      .price(new Price(2, BRONZE), 1, 8)
      .description(BLOCK_INFO)
      .slot(0);

  ExItemStack OAK_FENCE_GATE_ITEM = new ExItemStack(Material.OAK_FENCE_GATE, 3)
      .setDisplayName("§6Fence Gate")
      .setLore(BLOCK_INFO);
  Trade.Builder OAK_FENCE_GATE = new Trade.Builder()
      .giveItems(OAK_FENCE_GATE_ITEM)
      .price(new Price(2, BRONZE), 1, 6)
      .description(BLOCK_INFO)
      .slot(1);

  ExItemStack OAK_PLANKS_ITEM = new ExItemStack(Material.OAK_PLANKS, 4)
      .setDisplayName("§6Plank")
      .setLore(BLOCK_INFO);
  Trade.Builder OAK_PLANKS = new Trade.Builder()
      .giveItems(OAK_PLANKS_ITEM)
      .price(new Price(1, SILVER), 1, 10)
      .description(BLOCK_INFO)
      .slot(9);

  ExItemStack OAK_SLAB_ITEM = new ExItemStack(Material.OAK_SLAB, 4)
      .setDisplayName("§6Slab")
      .setLore(BLOCK_INFO);
  Trade.Builder OAK_SLAB = new Trade.Builder()
      .giveItems(OAK_SLAB_ITEM)
      .price(new Price(1, SILVER), 1, 10)
      .description(BLOCK_INFO)
      .slot(10);

  ExItemStack IRON_BARS_ITEM = new ExItemStack(Material.IRON_BARS, 3)
      .setDisplayName("§6Iron Bars")
      .setLore(BLOCK_INFO);
  Trade.Builder IRON_BARS = new Trade.Builder()
      .giveItems(IRON_BARS_ITEM)
      .price(new Price(2, SILVER), 1, 8)
      .description(BLOCK_INFO)
      .slot(18);

  ExItemStack COBBLESTONE_WALL_ITEM = new ExItemStack(Material.COBBLESTONE_WALL, 2)
      .setDisplayName("§6Wall")
      .setLore(BLOCK_INFO);
  Trade.Builder COBBLESTONE_WALL = new Trade.Builder()
      .giveItems(COBBLESTONE_WALL_ITEM)
      .price(new Price(2, SILVER), 1, 8)
      .description(BLOCK_INFO)
      .slot(19);

  Trade.Builder STONE_AXE = new Trade.Builder()
      .giveItems(new ExItemStack(Material.STONE_AXE)
          .setDisplayName("§6Axe")
          .unbreakable())
      .price(new Price(4, BRONZE))
      .notRebuyable()
      .slot(3);

  Trade.Builder IRON_PICKAXE = new Trade.Builder()
      .giveItems(new ExItemStack(Material.IRON_PICKAXE)
          .setDisplayName("§6Pickaxe")
          .unbreakable())
      .price(new Price(8, BRONZE))
      .notRebuyable()
      .slot(4);

  Trade.Builder APPLE = new Trade.Builder()
      .giveItems(new ExItemStack(Material.APPLE, 6, "§6Apple"))
      .price(new Price(1, SILVER))
      .slot(3);

  Trade.Builder PUMPKIN_PIE = new Trade.Builder()
      .giveItems(new ExItemStack(Material.PUMPKIN_PIE, 4, "§6Pumpkin Pie"))
      .price(new Price(2, SILVER))
      .slot(4);

  ExItemStack BEEF = new ExItemStack(Material.COOKED_BEEF, 12)
      .setSlot(7)
      .setDisplayName("§6Cooked Beef");
  Trade.Builder COOKED_BEEF = new Trade.Builder()
      .giveItems(BEEF.cloneWithId().asQuantity(12))
      .price(new Price(4, BRONZE))
      .slot(5);

  ExItemStack KELP = new ExItemStack(Material.DRIED_KELP, 8, "§6Dried Kelp")
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

  Trade.Builder DRIED_KELP = new Trade.Builder()
      .giveItems(KELP)
      .price(new Price(1, BRONZE))
      .slot(12);

  Trade.Builder GOLDEN_APPLE = new Trade.Builder()
      .giveItems(new ExItemStack(Material.GOLDEN_APPLE, 1, "§6Golden Apple"))
      .price(new Price(2, SILVER), 1, 6)
      .slot(13);

  Trade.Builder GOLDEN_CARROT = new Trade.Builder()
      .giveItems(new ExItemStack(Material.GOLDEN_CARROT, 6, "§6Golden Carrot"))
      .price(new Price(1, GOLD))
      .slot(14);

  Trade.Builder MILK = new Trade.Builder()
      .giveItems(new ExItemStack(Material.MILK_BUCKET, "§6Milk"))
      .price(new Price(2, BRONZE))
      .slot(21);

  Trade.Builder ENDER_PEARL = new Trade.Builder()
      .giveItems(new ExItemStack(Material.ENDER_PEARL, "§6Ender Pearl"))
      .price(new Price(1, GOLD))
      .slot(22);

  Trade.Builder SPEED = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.SWIFTNESS)
          .setDisplayName("§6Speed"))
      .price(new Price(1, SILVER))
      .slot(7);

  Trade.Builder INSTANT_HEAL = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.STRONG_HEALING
          )
          .setDisplayName("§6Instant Heal"))
      .price(new Price(6, BRONZE), 1, 32)
      .slot(8);

  Trade.Builder IRON_GOLEM = new Trade.Builder()
      .giveItems(MobDefIronGolem.ITEM.cloneWithId())
      .price(new Price(12, SILVER), 1, 4)
      .slot(46);

  Trade.Builder REGEN = new Trade.Builder()
      .display(new ExItemStack(Material.BEACON, "§cVillager Regeneration"))
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

  LevelType.Builder PROTECTION = new LevelType.Builder()
      .name("Protection")
      .display(new ExItemStack(Material.TURTLE_HELMET))
      .baseLevel(0)
      .levelDescription("+1 Protection")
      .levelEnchantment(Enchantment.PROTECTION);

  Function<LevelType.Builder, LevelType.Builder> PROTECTION_LEVEL = b ->
      b.addEnchantmentLvl(new Price(12, BRONZE), 1)
          .addEnchantmentLvl(new Price(10, SILVER), 2)
          .addEnchantmentLvl(new Price(6, GOLD), 3)
          .addEnchantmentLvl(new Price(20, SILVER), 4)
          .addEnchantmentLvl(new Price(12, GOLD), 5)
          .addEnchantmentLvl(new Price(46, BRONZE), 6)
          .addEnchantmentLvl(new Price(30, SILVER), 7)
          .addEnchantmentLvl(new Price(64, BRONZE), 8)
          .addEnchantmentLvl(new Price(40, SILVER), 9)
          .addEnchantmentLvl(new Price(31, GOLD), 10);

  LevelType.Builder PROJECTILE_PROTECTION = new LevelType.Builder()
      .name("Projectile Protection")
      .display(new ExItemStack(Material.ARROW))
      .baseLevel(0)
      .levelDescription("+1 Projectile Protection")
      .levelEnchantment(Enchantment.PROJECTILE_PROTECTION);

  Function<LevelType.Builder, LevelType.Builder> PROJECTILE_PROTECTION_LEVEL = b ->
      b.addEnchantmentLvl(new Price(3, SILVER), 1)
          .addEnchantmentLvl(new Price(8, BRONZE), 2)
          .addEnchantmentLvl(new Price(16, BRONZE), 3)
          .addEnchantmentLvl(new Price(5, GOLD), 4)
          .addEnchantmentLvl(new Price(13, SILVER), 5)
          .addEnchantmentLvl(new Price(32, BRONZE), 6)
          .addEnchantmentLvl(new Price(18, SILVER), 7)
          .addEnchantmentLvl(new Price(24, SILVER), 8)
          .addEnchantmentLvl(new Price(16, GOLD), 9)
          .addEnchantmentLvl(new Price(64, BRONZE), 10);

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

  LevelType.Builder MELEE_ARMOR_HELMET_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(MELEE_BASE_ARMOR_HELMET)
      .addMaterialLvl(null, null, Material.LEATHER_HELMET)
      .addMaterialLvl(new Price(4, BRONZE), "Chainmail Helmet", Material.CHAINMAIL_HELMET)
      .addMaterialLvl(new Price(8, SILVER), "Iron Helmet", Material.IRON_HELMET)
      .addMaterialLvl(new Price(6, GOLD), "Diamond Helmet", Material.DIAMOND_HELMET)
      .addMaterialLvl(new Price(17, GOLD), "Netherite Helmet", Material.NETHERITE_HELMET);
  UpgradeableItem.Builder MELEE_ARMOR_HELMET = new UpgradeableItem.Builder()
      .name("Helmet")
      .display(ARMOR_HELMET)
      .baseItem(MELEE_BASE_ARMOR_HELMET)
      .addLvlType(MELEE_ARMOR_HELMET_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_HELMET)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_HELMET)
          .apply(PROJECTILE_PROTECTION_LEVEL));
  LevelType.Builder MELEE_ARMOR_CHESTPLATE_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(MELEE_BASE_ARMOR_CHESTPLATE)
      .addMaterialLvl(null, null, Material.LEATHER_CHESTPLATE)
      .addMaterialLvl(new Price(6, BRONZE), "Chainmail Chestplate",
          Material.CHAINMAIL_CHESTPLATE)
      .addMaterialLvl(new Price(11, SILVER), "Iron Chestplate", Material.IRON_CHESTPLATE)
      .addMaterialLvl(new Price(8, GOLD), "Diamond Chestplate", Material.DIAMOND_CHESTPLATE)
      .addMaterialLvl(new Price(22, GOLD), "Netherite Chestplate",
          Material.NETHERITE_CHESTPLATE);
  UpgradeableItem.Builder MELEE_ARMOR_CHESTPLATE = new UpgradeableItem.Builder()
      .name("Chestplate")
      .display(ARMOR_CHESTPLATE)
      .baseItem(MELEE_BASE_ARMOR_CHESTPLATE)
      .addLvlType(MELEE_ARMOR_CHESTPLATE_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_CHESTPLATE)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_CHESTPLATE)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelType.Builder MELEE_ARMOR_LEGGINGS_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(MELEE_BASE_ARMOR_LEGGINGS)
      .addMaterialLvl(null, null, Material.LEATHER_LEGGINGS)
      .addMaterialLvl(new Price(5, BRONZE), "Chainmail Leggings", Material.CHAINMAIL_LEGGINGS)
      .addMaterialLvl(new Price(10, SILVER), "Iron Leggings", Material.IRON_LEGGINGS)
      .addMaterialLvl(new Price(7, GOLD), "Diamond Leggings", Material.DIAMOND_LEGGINGS)
      .addMaterialLvl(new Price(19, GOLD), "Netherite Leggings", Material.NETHERITE_LEGGINGS);
  UpgradeableItem.Builder MELEE_ARMOR_LEGGINGS = new UpgradeableItem.Builder()
      .name("Leggings")
      .display(ARMOR_LEGGINGS)
      .baseItem(MELEE_BASE_ARMOR_LEGGINGS)
      .addLvlType(MELEE_ARMOR_LEGGINGS_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_LEGGINGS)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_LEGGINGS)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelType.Builder MELEE_ARMOR_BOOTS_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(MELEE_BASE_ARMOR_BOOTS)
      .addMaterialLvl(null, null, Material.LEATHER_BOOTS)
      .addMaterialLvl(new Price(1, BRONZE), "Chainmail Boots", Material.CHAINMAIL_BOOTS)
      .addMaterialLvl(new Price(30, SILVER), "Iron Boots", Material.IRON_BOOTS)
      .addMaterialLvl(new Price(18, GOLD), "Diamond Boots", Material.DIAMOND_BOOTS)
      .addMaterialLvl(new Price(16, GOLD), "Netherite Boots", Material.NETHERITE_BOOTS);
  UpgradeableItem.Builder MELEE_ARMOR_BOOTS = new UpgradeableItem.Builder()
      .name("Boots")
      .display(ARMOR_BOOTS)
      .baseItem(MELEE_BASE_ARMOR_BOOTS)
      .addLvlType(MELEE_ARMOR_BOOTS_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(MELEE_BASE_ARMOR_BOOTS)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
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
  LevelType.Builder RANGED_ARMOR_HELMET_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(RANGED_BASE_ARMOR_HELMET)
      .addMaterialLvl(null, null, Material.LEATHER_HELMET)
      .addMaterialLvl(new Price(7, SILVER), "Golden Helmet", Material.GOLDEN_HELMET)
      .addMaterialLvl(new Price(7, GOLD), "Chainmail Helmet", Material.CHAINMAIL_HELMET)
      .addMaterialLvl(new Price(11, SILVER), "Iron Helmet", Material.IRON_HELMET);
  UpgradeableItem.Builder RANGED_ARMOR_HELMET = new UpgradeableItem.Builder()
      .name("Helmet")
      .price(new Price(4, BRONZE))
      .baseItem(RANGED_BASE_ARMOR_HELMET)
      .display(ARMOR_HELMET)
      .addLvlType(RANGED_ARMOR_HELMET_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_HELMET)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_HELMET)
          .apply(PROJECTILE_PROTECTION_LEVEL));
  LevelType.Builder RANGED_ARMOR_CHESTPLATE_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(RANGED_BASE_ARMOR_CHESTPLATE)
      .addMaterialLvl(null, null, Material.LEATHER_CHESTPLATE)
      .addMaterialLvl(new Price(6, SILVER), "Golden Chestplate", Material.GOLDEN_CHESTPLATE)
      .addMaterialLvl(new Price(24, BRONZE), "Chainmail Chestplate",
          Material.CHAINMAIL_CHESTPLATE)
      .addMaterialLvl(new Price(12, SILVER), "Iron Chestplate", Material.IRON_CHESTPLATE)
      .addMaterialLvl(new Price(18, GOLD), "Diamond Chestplate", Material.DIAMOND_CHESTPLATE);
  UpgradeableItem.Builder RANGED_ARMOR_CHESTPLATE = new UpgradeableItem.Builder()
      .name("Chestplate")
      .price(new Price(7, BRONZE))
      .baseItem(RANGED_BASE_ARMOR_CHESTPLATE)
      .display(ARMOR_CHESTPLATE)
      .addLvlType(RANGED_ARMOR_CHESTPLATE_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_CHESTPLATE)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_CHESTPLATE)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelType.Builder RANGED_ARMOR_LEGGINGS_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(RANGED_BASE_ARMOR_LEGGINGS)
      .addMaterialLvl(null, null, Material.LEATHER_LEGGINGS)
      .addMaterialLvl(new Price(5, SILVER), "Golden Leggings", Material.GOLDEN_LEGGINGS)
      .addMaterialLvl(new Price(10, SILVER), "Chainmail Leggings",
          Material.CHAINMAIL_LEGGINGS)
      .addMaterialLvl(new Price(16, GOLD), "Iron Leggings", Material.IRON_LEGGINGS);
  UpgradeableItem.Builder RANGED_ARMOR_LEGGINGS = new UpgradeableItem.Builder()
      .name("Leggings")
      .price(new Price(4, BRONZE))
      .baseItem(RANGED_BASE_ARMOR_LEGGINGS)
      .display(ARMOR_LEGGINGS)
      .addLvlType(RANGED_ARMOR_LEGGINGS_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_LEGGINGS)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_LEGGINGS)
          .apply(PROJECTILE_PROTECTION_LEVEL))
      .addConflictToLvlType(PROTECTION, PROJECTILE_PROTECTION);
  LevelType.Builder RANGED_ARMOR_BOOTS_TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(RANGED_BASE_ARMOR_BOOTS)
      .addMaterialLvl(null, null, Material.LEATHER_BOOTS)
      .addMaterialLvl(new Price(1, GOLD), "Golden Leggings", Material.GOLDEN_BOOTS)
      .addMaterialLvl(new Price(6, SILVER), "Chainmail Leggings", Material.CHAINMAIL_BOOTS)
      .addMaterialLvl(new Price(8, GOLD), "Iron Leggings", Material.IRON_BOOTS)
      .addMaterialLvl(new Price(15, SILVER), "Diamond Boots", Material.DIAMOND_BOOTS);
  UpgradeableItem.Builder RANGED_ARMOR_BOOTS = new UpgradeableItem.Builder()
      .name("Boots")
      .price(new Price(4, BRONZE))
      .baseItem(RANGED_BASE_ARMOR_BOOTS)
      .display(ARMOR_BOOTS)
      .addLvlType(RANGED_ARMOR_BOOTS_TYPE)
      .addLvlType(PROTECTION.clone()
          .levelItem(RANGED_BASE_ARMOR_BOOTS)
          .apply(PROTECTION_LEVEL))
      .addLvlType(PROJECTILE_PROTECTION.clone()
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
  Trade.Builder FIRE_GOLD_SWORD = new Trade.Builder()
      .giveItems(new ExItemStack(Material.GOLDEN_SWORD)
          .setDisplayName("§6Fire Sword")
          .addExEnchantment(Enchantment.FIRE_ASPECT, 2))
      .price(new Price(2, Currency.SILVER))
      .slot(46);
  Trade.Builder ALCHEMIST_SPEED = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(Material.POTION, 1, "§6Speed", PotionType.STRONG_SWIFTNESS
      ))
      .price(new Price(4, BRONZE))
      .slot(46);
  Trade.Builder WIZARD_REGEN = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration",
          PotionEffectType.REGENERATION, 10 * 20, 2))
      .price(new Price(4, BRONZE))
      .slot(46);
  Trade.Builder LUMBER_SPEED = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(Material.POTION, 1, "§6Speed", PotionType.STRONG_SWIFTNESS
      ))
      .price(new Price(4, BRONZE))
      .slot(46);
  Trade.Builder LUMBER_STRENGTH = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(Material.POTION, 1, "§6Strength", PotionType.STRENGTH
      ))
      .price(new Price(3, SILVER))
      .slot(47);
  Trade.Builder LUMBER_REGENERATION = new Trade.Builder()
      .giveItems(ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration",
          PotionEffectType.REGENERATION, 15, 3))
      .price(new Price(3, SILVER))
      .slot(48);
  Trade.Builder IRON_SKIN = new Trade.Builder()
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
