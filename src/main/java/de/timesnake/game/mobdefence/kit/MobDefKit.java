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
import de.timesnake.basic.loungebridge.util.user.Kit;
import de.timesnake.database.util.game.DbKit;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.CoreRegeneration;
import de.timesnake.game.mobdefence.special.DogSpawner;
import de.timesnake.game.mobdefence.special.PotionGenerator;
import de.timesnake.game.mobdefence.special.SheepSpawner;
import de.timesnake.game.mobdefence.special.weapon.*;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.MobTracker;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobDefKit extends Kit {

    // blocks
    public static final IncreasingItemTrade OAK_FENCE = new IncreasingItemTrade(10, new ShopPrice(2,
            ShopCurrency.BRONZE),
            1, 8, List.of(new ExItemStack(Material.OAK_FENCE, 3).setLore("§fCan be destroyed by attackers", "§cCan " +
            "only be placed 3 block up in the air")),
            new ExItemStack(Material.OAK_FENCE, 3, "§6Oak Fence"));

    public static final IncreasingItemTrade OAK_FENCE_GATE = new IncreasingItemTrade(11, new ShopPrice(2,
            ShopCurrency.BRONZE),
            1, 6, List.of(new ExItemStack(Material.OAK_FENCE_GATE, 2).setLore("§fCan be destroyed by attackers",
            "§cCan only be placed 3 block up in the air")),
            new ExItemStack(Material.OAK_FENCE_GATE, 2, "§6Oak Fence Gate"));

    public static final IncreasingItemTrade OAK_PLANKS = new IncreasingItemTrade(19, new ShopPrice(1,
            ShopCurrency.SILVER),
            1, 10, List.of(new ExItemStack(Material.OAK_PLANKS, 4).setLore("§fCan be destroyed by attackers", "§cCan " +
            "only be placed 3 block up in the air")),
            new ExItemStack(Material.OAK_PLANKS, 4, "§6Oak Plank"));

    public static final IncreasingItemTrade OAK_SLABS = new IncreasingItemTrade(20, new ShopPrice(1,
            ShopCurrency.SILVER),
            1, 10, List.of(new ExItemStack(Material.OAK_SLAB, 4).setLore("§fCan be destroyed by attackers", "§cCan " +
            "only be placed 3 block up in the air")),
            new ExItemStack(Material.OAK_SLAB, 4, "§6Oak Slab"));

    public static final IncreasingItemTrade IRON_BARS = new IncreasingItemTrade(28, new ShopPrice(2,
            ShopCurrency.SILVER),
            1, 8, List.of(new ExItemStack(Material.IRON_BARS, 3).setLore("§fCan be destroyed by attackers", "§cCan " +
            "only be placed 3 block up in the air")),
            new ExItemStack(Material.IRON_BARS, 3, "§6Iron Bars"));

    public static final IncreasingItemTrade COBBLESTONE_WALL = new IncreasingItemTrade(29, new ShopPrice(2,
            ShopCurrency.SILVER),
            1, 8, List.of(new ExItemStack(Material.COBBLESTONE_WALL, 2).setLore("§fCan be destroyed by attackers",
            "§cCan only be placed 3 block up in the air")),
            new ExItemStack(Material.COBBLESTONE_WALL, 2, "§6Cobblestone Wall"));

    public static final ItemTrade STONE_AXE = new ItemTrade(13, false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.STONE_AXE).unbreakable()),
            new ExItemStack(Material.STONE_AXE, 1, "§6Stone Axe"));

    public static final ItemTrade IRON_PICKAXE = new ItemTrade(14, false, new ShopPrice(8, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.STONE_PICKAXE).unbreakable()),
            new ExItemStack(Material.STONE_PICKAXE, 1, "§6Stone Pickaxe"));

    public static final Map<Material, ExItemStack> BLOCK_ITEM_BY_TYPE = new HashMap<>();
    // basics
    public static final ItemTrade APPLE = new ItemTrade(10, false, new ShopPrice(1, ShopCurrency.SILVER),
            List.of(new ExItemStack(Material.APPLE, 6)),
            new ExItemStack(Material.APPLE, 6, "§6Apple"));
    public static final ExItemStack BEEF = new ExItemStack(Material.COOKED_BEEF, 12).setSlot(7);
    public static final ItemTrade COOKED_BEEF = new ItemTrade(19, false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(BEEF), new ExItemStack(Material.COOKED_BEEF, 12, "§6Cooked Beef"));
    public static final ItemTrade PUMPKIN_PIE = new ItemTrade(11, false, new ShopPrice(2, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.PUMPKIN_PIE, 4)),
            new ExItemStack(Material.PUMPKIN_PIE, 4, "§6Pumpkin Pie"));
    public static final ExItemStack KELP = new ExItemStack(Material.DRIED_KELP, 8, "§6Dried Kelp").setLore("§7Healthy" +
            " Fast Food");
    public static final ItemTrade DRIED_KELP = new ItemTrade(20, false, new ShopPrice(1, ShopCurrency.BRONZE),
            List.of(KELP), KELP, "Healthy Fast Food");
    public static final ItemTrade GOLDEN_APPLE = new IncreasingItemTrade(12, new ShopPrice(2, ShopCurrency.SILVER),
            1, 6, List.of(new ExItemStack(Material.GOLDEN_APPLE, 1)),
            new ExItemStack(Material.GOLDEN_APPLE, 1, "§6Golden Apple"));
    public static final ItemTrade GOLDEN_CARROT = new ItemTrade(21, false, new ShopPrice(1, ShopCurrency.GOLD),
            List.of(new ExItemStack(Material.GOLDEN_CARROT, 6)),
            new ExItemStack(Material.GOLDEN_CARROT, 6, "§6Golden Carrot"));
    public static final ItemTrade MILK = new ItemTrade(14, false, new ShopPrice(2, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.MILK_BUCKET)), new ExItemStack(Material.MILK_BUCKET, "§6Milk"));
    public static final ItemTrade ENDER_PEARL = new ItemTrade(15, false, new ShopPrice(1, ShopCurrency.GOLD),
            List.of(new ExItemStack(Material.ENDER_PEARL, "§6Ender Pearl")),
            new ExItemStack(Material.ENDER_PEARL, "§6Ender Pearl"));
    public static final ItemTrade SPEED = new ItemTrade(37, false, new ShopPrice(1, ShopCurrency.SILVER),
            List.of(ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.SPEED, false, false)),
            ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.SPEED, false, false).setDisplayName("§6Speed"));
    public static final ItemTrade INSTANT_HEAL = new IncreasingItemTrade(38, new ShopPrice(6, ShopCurrency.BRONZE),
            1, 32,
            List.of(ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.INSTANT_HEAL, false, true).setDisplayName(
                    "§6Instant Heal")),
            ExItemStack.getPotion(Material.SPLASH_POTION, PotionType.INSTANT_HEAL, false, true).setDisplayName("§6Instant " +
                    "Heal"));
    public static final ItemTrade IRON_GOLEM = new IncreasingItemTrade(37, new ShopPrice(12, ShopCurrency.SILVER),
            2, 4, List.of(IronGolem.ITEM), IronGolem.ITEM);
    public static final ShopTrade REGEN = new ShopTrade(true, new ShopPrice(4, ShopCurrency.EMERALD),
            CoreRegeneration.ITEM) {
        @Override
        public ShopTrade clone() {
            return this;
        }

        @Override
        public void sell(MobDefUser user) {
            MobDefServer.getMobDefUserManager().getCoreRegeneration().run(user);
        }
    };

    // armor

    public static final ExItemStack WEAPONS = new ExItemStack(Material.IRON_SWORD).hideAll();
    public static final ExItemStack ARMOR = new ExItemStack(Material.CHAINMAIL_CHESTPLATE).hideAll();

    public static final ExItemStack ARMOR_HELMET = new ExItemStack(Material.CHAINMAIL_HELMET);
    public static final ExItemStack ARMOR_CHESTPLATE = new ExItemStack(Material.CHAINMAIL_CHESTPLATE);
    public static final ExItemStack ARMOR_LEGGINGS = new ExItemStack(Material.CHAINMAIL_LEGGINGS);
    public static final ExItemStack ARMOR_BOOTS = new ExItemStack(Material.CHAINMAIL_BOOTS);

    public static final ItemLevelType<?> PROTECTION = new ItemLevelType<>("Protection",
            new ExItemStack(Material.TURTLE_HELMET), 0, 10,
            ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(12, ShopCurrency.BRONZE),
                            new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(6, ShopCurrency.GOLD),
                            new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(20, ShopCurrency.SILVER),
                            new ShopPrice(12, ShopCurrency.GOLD), new ShopPrice(46, ShopCurrency.BRONZE),
                            new ShopPrice(30, ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.BRONZE),
                            new ShopPrice(31, ShopCurrency.GOLD)),
                    "+1 Protection", Enchantment.PROTECTION_ENVIRONMENTAL, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));

    public static final ItemLevelType<?> PROJECTILE_PROTECTION = new ItemLevelType<>("Projectile Protection",
            new ExItemStack(Material.ARROW), 0, 10, ItemLevel.getEnchantmentLevels(1,
            List.of(new ShopPrice(3, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.BRONZE),
                    new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.GOLD),
                    new ShopPrice(13, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.BRONZE),
                    new ShopPrice(18, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.SILVER),
                    new ShopPrice(16, ShopCurrency.GOLD), new ShopPrice(64, ShopCurrency.BRONZE)),
            "+1 Projectile Protection", Enchantment.PROTECTION_ENVIRONMENTAL, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
    public static final ExItemStack MELEE_BASE_ARMOR_HELMET = new ExItemStack(Material.LEATHER_HELMET).unbreakable();

    // melee
    public static final ExItemStack MELEE_BASE_ARMOR_CHESTPLATE = new ExItemStack(Material.LEATHER_CHESTPLATE).unbreakable();
    public static final ExItemStack MELEE_BASE_ARMOR_LEGGINGS = new ExItemStack(Material.LEATHER_LEGGINGS).unbreakable();
    public static final ExItemStack MELEE_BASE_ARMOR_BOOTS = new ExItemStack(Material.LEATHER_BOOTS).unbreakable();
    public static final ItemTrade IRON_SKIN = new ItemTrade(false, new ShopPrice(8, ShopCurrency.SILVER),
            List.of(ExItemStack.getPotion(ExItemStack.PotionMaterial.DRINK, 1, "Iron Skin", PotionEffectType.DAMAGE_RESISTANCE, 60 * 20, 3)),
            ExItemStack.getPotion(ExItemStack.PotionMaterial.DRINK, 1, "Iron Skin", PotionEffectType.DAMAGE_RESISTANCE, 60 * 20, 3));
    public static final ItemLevelType<?> MELEE_ARMOR_HELMET_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(4, ShopCurrency.BRONZE), new ShopPrice(8, ShopCurrency.SILVER),
                    new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(17, ShopCurrency.GOLD)),
            List.of("Chainmail Helmet", "Iron Helmet", "Diamond Helmet", "Netherite Helmet"),
            List.of(Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET,
                    Material.NETHERITE_HELMET)));
    public static final ItemLevelType<?> MELEE_ARMOR_CHESTPLATE_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(6, ShopCurrency.BRONZE), new ShopPrice(10, ShopCurrency.SILVER),
                    new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(20, ShopCurrency.GOLD)),
            List.of("Chainmail Chestplate", "Iron Chestplate", "Diamond Chestplate", "Netherite Chestplate"),
            List.of(Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
                    Material.NETHERITE_CHESTPLATE)));
    public static final ItemLevelType<?> MELEE_ARMOR_LEGGINGS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(9, ShopCurrency.SILVER),
                    new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(18, ShopCurrency.GOLD)),
            List.of("Chainmail Leggings", "Iron Leggings", "Diamond Leggings", "Netherite Leggings"),
            List.of(Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS,
                    Material.NETHERITE_LEGGINGS)));
    public static final ItemLevelType<?> MELEE_ARMOR_BOOTS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(1, ShopCurrency.GOLD), new ShopPrice(30, ShopCurrency.BRONZE),
                    new ShopPrice(18, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)),
            List.of("Chainmail Boots", "Iron Boots", "Diamond Boots", "Netherite Boots"),
            List.of(Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS)));
    public static final LevelItem MELEE_ARMOR_HELMET = new LevelItem("Helmet", MELEE_BASE_ARMOR_HELMET, ARMOR_HELMET,
            List.of(MELEE_ARMOR_HELMET_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final LevelItem MELEE_ARMOR_CHESTPLATE = new LevelItem("Chestplate", MELEE_BASE_ARMOR_CHESTPLATE,
            ARMOR_CHESTPLATE,
            List.of(MELEE_ARMOR_CHESTPLATE_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final LevelItem MELEE_ARMOR_LEGGINGS = new LevelItem("Leggings", MELEE_BASE_ARMOR_LEGGINGS,
            ARMOR_LEGGINGS,
            List.of(MELEE_ARMOR_LEGGINGS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    // ranged
    public static final LevelItem MELEE_ARMOR_BOOTS = new LevelItem("Boots", MELEE_BASE_ARMOR_BOOTS, ARMOR_BOOTS,
            List.of(MELEE_ARMOR_BOOTS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final ItemShop KNIGHT_ARMOR = new ItemShop("§6Armor", 14, ARMOR,
            List.of(MELEE_ARMOR_HELMET, MELEE_ARMOR_CHESTPLATE, MELEE_ARMOR_LEGGINGS, MELEE_ARMOR_BOOTS),
            List.of(IRON_SKIN));
    public static final ItemShop LUMBERJACK_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(MELEE_ARMOR_HELMET, MELEE_ARMOR_CHESTPLATE, MELEE_ARMOR_LEGGINGS, MELEE_ARMOR_BOOTS), List.of());
    public static final ItemLevelType<?> RANGED_ARMOR_HELMET_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 4, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(7, ShopCurrency.SILVER), new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(11,
                    ShopCurrency.SILVER)),
            List.of("Golden Helmet", "Chainmail Helmet", "Iron Helmet"),
            List.of(Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET)));

    public static final ItemLevelType<?> RANGED_ARMOR_CHESTPLATE_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.BRONZE),
                    new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(18, ShopCurrency.GOLD)),
            List.of("Golden Chestplate", "Chainmail Chestplate", "Iron Chestplate", "Diamond Chestplate"),
            List.of(Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
                    Material.DIAMOND_CHESTPLATE)));

    public static final ItemLevelType<?> RANGED_ARMOR_LEGGINGS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 4, ItemLevel.getMaterialLevels(2, List.of(new ShopPrice(5, ShopCurrency.SILVER),
                    new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)),
            List.of("Golden Leggings", "Chainmail Leggings", "Iron Leggings"),
            List.of(Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
                    Material.DIAMOND_LEGGINGS)));

    public static final ItemLevelType<?> RANGED_ARMOR_BOOTS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2, List.of(new ShopPrice(1, ShopCurrency.GOLD),
                    new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(10,
                            ShopCurrency.SILVER)),
            List.of("Golden Boots", "Chainmail Boots", "Iron Boots", "Diamond Boots"),
            List.of(Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS)));
    public static final LevelItem RANGED_ARMOR_HELMET = new LevelItem("Helmet", true,
            new ShopPrice(4, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_HELMET, ARMOR_HELMET,
            List.of(RANGED_ARMOR_HELMET_TYPE, PROTECTION, PROJECTILE_PROTECTION));

    public static final LevelItem RANGED_ARMOR_CHESTPLATE = new LevelItem("Chestplate", true,
            new ShopPrice(7, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_CHESTPLATE, ARMOR_CHESTPLATE,
            List.of(RANGED_ARMOR_CHESTPLATE_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final LevelItem RANGED_ARMOR_LEGGINGS = new LevelItem("Leggings", true,
            new ShopPrice(5, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_LEGGINGS, ARMOR_LEGGINGS,
            List.of(RANGED_ARMOR_LEGGINGS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final LevelItem RANGED_ARMOR_BOOTS = new LevelItem("Boots", true,
            new ShopPrice(4, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_BOOTS, ARMOR_BOOTS,
            List.of(RANGED_ARMOR_BOOTS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final ItemShop ALCHEMIST_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS, RANGED_ARMOR_BOOTS),
            List.of());
    public static final ItemShop ARCHER_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS, RANGED_ARMOR_BOOTS),
            List.of());
    public static final ItemShop WIZARD_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS, RANGED_ARMOR_BOOTS),
            List.of());
    // knight
    public static final ItemTrade FIRE_GOLD_SWORD = new ItemTrade(false, new ShopPrice(2, ShopCurrency.SILVER),
            List.of(new ExItemStack(Material.GOLDEN_SWORD).addExEnchantment(Enchantment.FIRE_ASPECT, 2)),
            new ExItemStack(new ExItemStack(Material.GOLDEN_SWORD).enchant()).setDisplayName("§6Fire Sword"));
    public static final ItemShop KNIGHT_LEVEL_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(Sword.SWORD, SwingSword.SWORD), List.of(FIRE_GOLD_SWORD));
    public static final MobDefKit KNIGHT = new MobDefKit(1, "Knight", Material.IRON_SWORD,
            List.of("§fWeapons: §7Sword, Axe", "§fArmor: §7Strong", "", "§7Resistance aura"),
            List.of(Sword.WOODEN_SWORD, new ExItemStack(Material.SHIELD).unbreakable().setSlot(EquipmentSlot.OFF_HAND),
                    MELEE_BASE_ARMOR_HELMET.setSlot(EquipmentSlot.HEAD),
                    MELEE_BASE_ARMOR_CHESTPLATE.setSlot(EquipmentSlot.CHEST),
                    MELEE_BASE_ARMOR_LEGGINGS.setSlot(EquipmentSlot.LEGS),
                    MELEE_BASE_ARMOR_BOOTS.setSlot(EquipmentSlot.FEET),
                    MobDefKit.BEEF, MobTracker.TRACKER),
            List.of(KNIGHT_LEVEL_WEAPONS, KNIGHT_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP,
                    BaseShops.TEAM_SHOP));
    // alchemist
    public static final ItemTrade ALCHEMIST_SPEED = new ItemTrade(false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(ExItemStack.getPotion(Material.POTION, PotionType.SPEED, false, true)),
            ExItemStack.getPotion(Material.POTION, 1, "Speed", PotionType.SPEED, false, true));

    // archer
    public static final ItemShop ALCHEMIST_WEAPONS = new ItemShop("§6Weapons", 12,
            new ExItemStack(Material.IRON_SWORD),
            List.of(FireHoe.FIRE_HOE, FireStaff.FIRE_STAFF, Iceball.ITEM), List.of(ALCHEMIST_SPEED, Snowman.SNOWMAN,
            Blaze.BLAZE));
    public static final MobDefKit ALCHEMIST = new MobDefKit(3, "Alchemist", Material.BLAZE_POWDER,
            List.of("§fWeapons: §7Fire Hoe, Fire Staff, Iceball", "§fArmor: §7Weak", "", "§7Resistant against fire",
                    "§7Defence Snowmen, Blazes"),
            List.of(FireHoe.FIRE_HOE.getItem(), Iceball.ICEBALL, MobDefKit.BEEF, MobTracker.TRACKER),
            List.of(ALCHEMIST_WEAPONS, ALCHEMIST_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP,
                    BaseShops.TEAM_SHOP));
    public static final ItemShop ARCHER_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(Bow.BOW, SplashBow.BOW, RocketCrossBow.CROSSBOW), List.of(PoisonArrow.TRADE));
    public static final MobDefKit ARCHER = new MobDefKit(2, "Archer", Material.BOW,
            List.of("§fWeapons: §7Bows, Crossbow", "§fArmor: §7Weak"),
            List.of(Bow.BOW.getItem(), MobDefKit.BEEF, MobTracker.TRACKER, new ItemStack(Material.ARROW)),
            List.of(ARCHER_WEAPONS, ARCHER_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP, BaseShops.TEAM_SHOP));
    // wizard
    public static final ItemTrade WIZARD_REGEN = new ItemTrade(false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration", PotionEffectType.REGENERATION, 10 * 20, 2)),
            ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "Regeneration", PotionEffectType.REGENERATION, 10 * 20, 2));
    public static final ItemShop WIZARD_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(Wand.WAND), List.of(WIZARD_REGEN, IRON_SKIN));
    public static final MobDefKit WIZARD = new MobDefKit(4, "Wizard",
            ExItemStack.getPotion(Material.POTION, PotionType.INSTANT_HEAL, false, false).getType(),
            List.of("§fWeapon: §7Wand", "§fArmor: §7Weak", "", "§7Instant Heal Potions"),
            List.of(Wand.WAND.getItem(), MobDefKit.BEEF, PotionGenerator.INSTANT_HEAL, MobTracker.TRACKER),
            List.of(WIZARD_WEAPONS, WIZARD_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP, BaseShops.TEAM_SHOP));


    // lumberjack

    public static final ItemTrade LUMBER_SPEED = new ItemTrade(false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(ExItemStack.getPotion(Material.POTION, PotionType.SPEED, false, true)),
            ExItemStack.getPotion(Material.POTION, 1, "Speed", PotionType.SPEED, false, true));

    public static final ItemTrade LUMBER_STRENGTH = new ItemTrade(false, new ShopPrice(3, ShopCurrency.SILVER),
            List.of(ExItemStack.getPotion(Material.POTION, PotionType.STRENGTH, false, true)),
            ExItemStack.getPotion(Material.POTION, 1, "Strength", PotionType.STRENGTH, false, true));

    public static final ItemTrade LUMBER_REGENERATION = new ItemTrade(false, new ShopPrice(4, ShopCurrency.SILVER),
            List.of(ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration", PotionEffectType.REGENERATION, 15, 3)),
            ExItemStack.getPotion(ExItemStack.PotionMaterial.SPLASH, 2, "§6Regeneration", PotionEffectType.REGENERATION, 15, 3));

    public static final ItemShop LUMBERJACK_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(LumberAxe.AXE, BoomerangAxe.BOOMERANG_AXE, SheepSpawner.LEVEL_ITEM, DogSpawner.LEVEL_ITEM),
            List.of(LUMBER_SPEED, LUMBER_STRENGTH, LUMBER_REGENERATION));
    public static final MobDefKit LUMBERJACK = new MobDefKit(5, "Lumberjack", Material.IRON_AXE,
            List.of("§fWeapon: §7Axe", "§fArmor: §7Strong", "", "§7Speed and Strength Potions",
                    "§7Regeneration effect after mob kill", "§7Sheep as distraction", "§7Dogs as companions"),
            List.of(MobDefKit.BEEF, LumberAxe.AXE.getItem(), SheepSpawner.LEVEL_ITEM.getItem(),
                    DogSpawner.LEVEL_ITEM.getItem(), MobTracker.TRACKER,
                    MELEE_BASE_ARMOR_HELMET.setSlot(EquipmentSlot.HEAD),
                    MELEE_BASE_ARMOR_CHESTPLATE.setSlot(EquipmentSlot.CHEST),
                    MELEE_BASE_ARMOR_LEGGINGS.setSlot(EquipmentSlot.LEGS),
                    MELEE_BASE_ARMOR_BOOTS.setSlot(EquipmentSlot.FEET)),
            List.of(LUMBERJACK_WEAPONS, LUMBERJACK_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP,
                    BaseShops.TEAM_SHOP));
    public static final MobDefKit[] KITS = new MobDefKit[]{KNIGHT, ARCHER, ALCHEMIST, WIZARD, LUMBERJACK};

    static {
        PROTECTION.setConflictingTypes(List.of(PROJECTILE_PROTECTION));
        PROJECTILE_PROTECTION.setConflictingTypes(List.of(PROTECTION));
    }

    static {
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_FENCE, OAK_FENCE.getSellingItems().get(0).cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_FENCE_GATE, OAK_FENCE_GATE.getSellingItems().get(0).cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_PLANKS, OAK_PLANKS.getSellingItems().get(0).cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_SLAB, OAK_SLABS.getSellingItems().get(0).cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.IRON_BARS, IRON_BARS.getSellingItems().get(0).cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.COBBLESTONE_WALL,
                COBBLESTONE_WALL.getSellingItems().get(0).cloneWithId().asOne());
    }

    public final List<ItemShop> shopInventories;

    public MobDefKit(Integer id, String name, Material material, List<String> description, List<ItemStack> items,
                     List<ItemShop> shopInventories) {
        super(id, name, material, description, items);
        this.shopInventories = shopInventories;
    }

    public MobDefKit(DbKit kit, List<ItemStack> items, List<ItemShop> shopInventories) {
        super(kit, items);
        this.shopInventories = shopInventories;
    }

    public List<ItemShop> getShopInventories() {
        return this.shopInventories;
    }

    public KitShop getShop(MobDefUser user) {
        return new KitShop(user);
    }

}
