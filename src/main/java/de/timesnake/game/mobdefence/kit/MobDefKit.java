package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.loungebridge.util.user.Kit;
import de.timesnake.database.util.game.DbKit;
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

import java.util.List;

public class MobDefKit extends Kit {

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

    static {
        PROTECTION.setConflictingTypes(List.of(PROJECTILE_PROTECTION));
        PROJECTILE_PROTECTION.setConflictingTypes(List.of(PROTECTION));
    }

    public static final ItemShop ARCHER_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(Shard.SHARD, SplashBow.BOW, RocketCrossBow.CROSSBOW), List.of(PoisonArrow.TRADE));

    // melee

    public static final ExItemStack MELEE_BASE_ARMOR_HELMET = new ExItemStack(Material.LEATHER_HELMET, true);
    public static final ExItemStack MELEE_BASE_ARMOR_CHESTPLATE = new ExItemStack(Material.LEATHER_CHESTPLATE, true);
    public static final ExItemStack MELEE_BASE_ARMOR_LEGGINGS = new ExItemStack(Material.LEATHER_LEGGINGS, true);
    public static final ExItemStack MELEE_BASE_ARMOR_BOOTS = new ExItemStack(Material.LEATHER_BOOTS, true);
    private static final ItemTrade IRON_SKIN = new ItemTrade(false, new ShopPrice(8, ShopCurrency.SILVER),
            List.of(new ExItemStack(false, "Iron Skin", PotionEffectType.DAMAGE_RESISTANCE, 60 * 20, 3, 1)),
            new ExItemStack("Iron Skin", PotionEffectType.DAMAGE_RESISTANCE, 60 * 20, 3, 1));
    private static final ItemLevelType<?> MELEE_ARMOR_HELMET_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(4, ShopCurrency.BRONZE), new ShopPrice(8, ShopCurrency.SILVER),
                    new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(17, ShopCurrency.GOLD)),
            List.of("Chainmail Helmet", "Iron Helmet", "Diamond Helmet", "Netherite Helmet"),
            List.of(Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET,
                    Material.NETHERITE_HELMET)));
    private static final ItemLevelType<?> MELEE_ARMOR_CHESTPLATE_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(6, ShopCurrency.BRONZE), new ShopPrice(10, ShopCurrency.SILVER),
                    new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(20, ShopCurrency.GOLD)),
            List.of("Chainmail Chestplate", "Iron Chestplate", "Diamond Chestplate", "Netherite Chestplate"),
            List.of(Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
                    Material.NETHERITE_CHESTPLATE)));
    private static final ItemLevelType<?> MELEE_ARMOR_LEGGINGS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(9, ShopCurrency.SILVER),
                    new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(18, ShopCurrency.GOLD)),
            List.of("Chainmail Leggings", "Iron Leggings", "Diamond Leggings", "Netherite Leggings"),
            List.of(Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS,
                    Material.NETHERITE_LEGGINGS)));
    private static final ItemLevelType<?> MELEE_ARMOR_BOOTS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(1, ShopCurrency.GOLD), new ShopPrice(30, ShopCurrency.BRONZE),
                    new ShopPrice(18, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)),
            List.of("Chainmail Boots", "Iron Boots", "Diamond Boots", "Netherite Boots"),
            List.of(Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS)));
    private static final LevelItem MELEE_ARMOR_HELMET = new LevelItem("Helmet", MELEE_BASE_ARMOR_HELMET, ARMOR_HELMET,
            List.of(MELEE_ARMOR_HELMET_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    private static final LevelItem MELEE_ARMOR_CHESTPLATE = new LevelItem("Chestplate", MELEE_BASE_ARMOR_CHESTPLATE,
            ARMOR_CHESTPLATE,
            List.of(MELEE_ARMOR_CHESTPLATE_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    private static final LevelItem MELEE_ARMOR_LEGGINGS = new LevelItem("Leggings", MELEE_BASE_ARMOR_LEGGINGS,
            ARMOR_LEGGINGS,
            List.of(MELEE_ARMOR_LEGGINGS_TYPE, PROTECTION, PROJECTILE_PROTECTION));

    // ranged
    private static final LevelItem MELEE_ARMOR_BOOTS = new LevelItem("Boots", MELEE_BASE_ARMOR_BOOTS, ARMOR_BOOTS,
            List.of(MELEE_ARMOR_BOOTS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    private static final ItemShop KNIGHT_ARMOR = new ItemShop("§6Armor", 14, ARMOR,
            List.of(MELEE_ARMOR_HELMET, MELEE_ARMOR_CHESTPLATE, MELEE_ARMOR_LEGGINGS, MELEE_ARMOR_BOOTS),
            List.of(IRON_SKIN));
    public static final ItemShop LUMBERJACK_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(MELEE_ARMOR_HELMET, MELEE_ARMOR_CHESTPLATE, MELEE_ARMOR_LEGGINGS, MELEE_ARMOR_BOOTS), List.of());
    private static final ItemLevelType<?> RANGED_ARMOR_HELMET_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 4, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(7, ShopCurrency.SILVER), new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(11,
                    ShopCurrency.SILVER)),
            List.of("Golden Helmet", "Chainmail Helmet", "Iron Helmet"),
            List.of(Material.GOLDEN_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET)));
    private static final ItemLevelType<?> RANGED_ARMOR_CHESTPLATE_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2,
            List.of(new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.BRONZE),
                    new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(18, ShopCurrency.GOLD)),
            List.of("Golden Chestplate", "Chainmail Chestplate", "Iron Chestplate", "Diamond Chestplate"),
            List.of(Material.GOLDEN_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
                    Material.DIAMOND_CHESTPLATE)));
    private static final ItemLevelType<?> RANGED_ARMOR_LEGGINGS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 4, ItemLevel.getMaterialLevels(2, List.of(new ShopPrice(5, ShopCurrency.SILVER),
                    new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)),
            List.of("Golden Leggings", "Chainmail Leggings", "Iron Leggings"),
            List.of(Material.GOLDEN_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
                    Material.DIAMOND_LEGGINGS)));
    private static final ItemLevelType<?> RANGED_ARMOR_BOOTS_TYPE = new ItemLevelType<>("Type",
            new ExItemStack(Material.ANVIL),
            1, 5, ItemLevel.getMaterialLevels(2, List.of(new ShopPrice(1, ShopCurrency.GOLD),
                    new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(10,
                            ShopCurrency.SILVER)),
            List.of("Golden Boots", "Chainmail Boots", "Iron Boots", "Diamond Boots"),
            List.of(Material.GOLDEN_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS)));
    private static final LevelItem RANGED_ARMOR_HELMET = new LevelItem("Helmet", true,
            new ShopPrice(4, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_HELMET, ARMOR_HELMET,
            List.of(RANGED_ARMOR_HELMET_TYPE, PROTECTION, PROJECTILE_PROTECTION));

    // knight
    private static final LevelItem RANGED_ARMOR_CHESTPLATE = new LevelItem("Chestplate", true,
            new ShopPrice(7, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_CHESTPLATE, ARMOR_CHESTPLATE,
            List.of(RANGED_ARMOR_CHESTPLATE_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    private static final LevelItem RANGED_ARMOR_LEGGINGS = new LevelItem("Leggings", true,
            new ShopPrice(5, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_LEGGINGS, ARMOR_LEGGINGS,
            List.of(RANGED_ARMOR_LEGGINGS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    private static final LevelItem RANGED_ARMOR_BOOTS = new LevelItem("Boots", true,
            new ShopPrice(4, ShopCurrency.BRONZE), MELEE_BASE_ARMOR_BOOTS, ARMOR_BOOTS,
            List.of(RANGED_ARMOR_BOOTS_TYPE, PROTECTION, PROJECTILE_PROTECTION));
    public static final ItemShop ARCHER_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS, RANGED_ARMOR_BOOTS),
            List.of());
    public static final MobDefKit ARCHER = new MobDefKit(2, "Archer", Material.BOW,
            List.of("§fWeapons: §7Speers, Bows, Crossbow", "§7Armor: §7Weak"),
            List.of(Shard.SHARD.getItem(), ItemTrade.BEEF, MobTracker.TRACKER),
            List.of(ARCHER_WEAPONS, ARCHER_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP, BaseShops.TEAM_SHOP));

    // archer
    public static final ItemShop ALCHEMIST_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS, RANGED_ARMOR_BOOTS),
            List.of());
    public static final ItemShop WIZARD_ARMOR = new ItemShop("Armor", 14, ARMOR,
            List.of(RANGED_ARMOR_HELMET, RANGED_ARMOR_CHESTPLATE, RANGED_ARMOR_LEGGINGS, RANGED_ARMOR_BOOTS),
            List.of());
    private static final ItemTrade FIRE_GOLD_SWORD = new ItemTrade(false, new ShopPrice(2, ShopCurrency.SILVER),
            List.of(new ExItemStack(Material.GOLDEN_SWORD, List.of(Enchantment.FIRE_ASPECT), List.of(2))),
            new ExItemStack(new ExItemStack(Material.GOLDEN_SWORD, false, true)).setDisplayName("§6Fire Sword"));

    // alchemist
    public static final ItemShop KNIGHT_LEVEL_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(Sword.SWORD, SwingSword.SWORD), List.of(FIRE_GOLD_SWORD));
    public static final MobDefKit KNIGHT = new MobDefKit(1, "Knight", Material.IRON_SWORD,
            List.of("§fWeapons: §7Sword, Axe", "§fArmor: §7Strong", "", "§7Resistance aura"),
            List.of(Sword.WOODEN_SWORD, new ExItemStack(Material.SHIELD, true).setSlot(EquipmentSlot.OFF_HAND),
                    MELEE_BASE_ARMOR_HELMET.setSlot(EquipmentSlot.HEAD),
                    MELEE_BASE_ARMOR_CHESTPLATE.setSlot(EquipmentSlot.CHEST),
                    MELEE_BASE_ARMOR_LEGGINGS.setSlot(EquipmentSlot.LEGS),
                    MELEE_BASE_ARMOR_BOOTS.setSlot(EquipmentSlot.FEET),
                    ItemTrade.BEEF, MobTracker.TRACKER),
            List.of(KNIGHT_LEVEL_WEAPONS, KNIGHT_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP,
                    BaseShops.TEAM_SHOP));
    private static final ItemTrade ALCHEMIST_SPEED = new ItemTrade(false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.POTION, PotionType.SPEED, false, true)),
            new ExItemStack(Material.POTION, 1, "Speed", PotionType.SPEED, false, true));


    // wizard
    private static final ItemShop ALCHEMIST_WEAPONS = new ItemShop("§6Weapons", 12,
            new ExItemStack(Material.IRON_SWORD),
            List.of(FireHoe.FIRE_HOE, FireStaff.FIRE_STAFF, Iceball.ITEM), List.of(ALCHEMIST_SPEED, Snowman.SNOWMAN,
            Blaze.BLAZE));
    public static final MobDefKit ALCHEMIST = new MobDefKit(3, "Alchemist", Material.BLAZE_POWDER,
            List.of("§fWeapons: §7Fire Hoe, Fire Staff, Iceball", "§fArmor: §7Weak", "", "§7Resistant against fire",
                    "§7Defence Snowmen, Blazes"),
            List.of(FireHoe.FIRE_HOE.getItem(), Iceball.ICEBALL, ItemTrade.BEEF, MobTracker.TRACKER),
            List.of(ALCHEMIST_WEAPONS, ALCHEMIST_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP,
                    BaseShops.TEAM_SHOP));
    private static final ItemTrade WIZARD_REGEN = new ItemTrade(false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(new ExItemStack(true, "§6Regeneration", PotionEffectType.REGENERATION, 10 * 20, 2, 2)),
            new ExItemStack("Regeneration", PotionEffectType.REGENERATION, 10 * 20, 2, 2));
    public static final ItemShop WIZARD_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(Wand.WAND), List.of(WIZARD_REGEN, IRON_SKIN));

    // lumberjack
    public static final MobDefKit WIZARD = new MobDefKit(4, "Wizard",
            new ExItemStack(Material.POTION, PotionType.INSTANT_HEAL, false, false).getType(),
            List.of("§fWeapon: §7Wand", "§fArmor: §7Weak", "", "§7Instant Heal Potions"),
            List.of(Wand.WAND.getItem(), ItemTrade.BEEF, PotionGenerator.INSTANT_HEAL, MobTracker.TRACKER),
            List.of(WIZARD_WEAPONS, WIZARD_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP, BaseShops.TEAM_SHOP));
    private static final ItemTrade LUMBER_REGENERATION = new ItemTrade(false, new ShopPrice(4, ShopCurrency.SILVER),
            List.of(new ExItemStack("§6Regeneration", PotionEffectType.REGENERATION, 15, 3, 2)),
            new ExItemStack("§6Regeneration", PotionEffectType.REGENERATION, 15, 3, 2));
    private static final ItemTrade LUMBER_SPEED = new ItemTrade(false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.POTION, PotionType.SPEED, false, true)),
            new ExItemStack(Material.POTION, 1, "Speed", PotionType.SPEED, false, true));
    private static final ItemTrade LUMBER_STRENGTH = new ItemTrade(false, new ShopPrice(3, ShopCurrency.SILVER),
            List.of(new ExItemStack(Material.POTION, PotionType.STRENGTH, false, true)),
            new ExItemStack(Material.POTION, 1, "Strength", PotionType.STRENGTH, false, true));
    public static final ItemShop LUMBERJACK_WEAPONS = new ItemShop("Weapons", 12, WEAPONS,
            List.of(LumberAxe.AXE, BoomerangAxe.BOOMERANG_AXE, SheepSpawner.LEVEL_ITEM, DogSpawner.LEVEL_ITEM),
            List.of(LUMBER_SPEED, LUMBER_STRENGTH, LUMBER_REGENERATION));
    public static final MobDefKit LUMBERJACK = new MobDefKit(5, "Lumberjack", Material.IRON_AXE,
            List.of("§fWeapon: §7Axe", "§fArmor: §7Strong", "", "§7Speed and Strength Potions",
                    "§7Regeneration effect after mob kill", "§7Sheep as distraction", "§7Dogs as companions"),
            List.of(ItemTrade.BEEF, LumberAxe.AXE.getItem(), SheepSpawner.LEVEL_ITEM.getItem(),
                    DogSpawner.LEVEL_ITEM.getItem(), MobTracker.TRACKER,
                    MELEE_BASE_ARMOR_HELMET.setSlot(EquipmentSlot.HEAD),
                    MELEE_BASE_ARMOR_CHESTPLATE.setSlot(EquipmentSlot.CHEST),
                    MELEE_BASE_ARMOR_LEGGINGS.setSlot(EquipmentSlot.LEGS),
                    MELEE_BASE_ARMOR_BOOTS.setSlot(EquipmentSlot.FEET)),
            List.of(LUMBERJACK_WEAPONS, LUMBERJACK_ARMOR, BaseShops.BLOCK_SHOP, BaseShops.BASIC_SHOP,
                    BaseShops.TEAM_SHOP));


    public static final MobDefKit[] KITS = new MobDefKit[]{KNIGHT, ARCHER, ALCHEMIST, WIZARD, LUMBERJACK};


    private final List<ItemShop> shopInventories;

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
