/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.loungebridge.util.user.Kit;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Shop;
import de.timesnake.game.mobdefence.special.PotionGenerator;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.MobTracker;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

public class MobDefKit extends Kit implements KitItems {

    public static final MobDefKit KNIGHT = new MobDefKit(1, "Knight", Material.IRON_SWORD,
            List.of("§fWeapons: §7Sword, Axe", "§fArmor: §7Strong", "", "§7Resistance aura"),
            List.of(new ExItemStack(Material.SHIELD).unbreakable().setSlot(EquipmentSlot.OFF_HAND),
                    MobDefKit.BEEF, MobTracker.TRACKER),
            List.of(KNIGHT_WEAPONS, KNIGHT_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop()));
    public static final MobDefKit LUMBERJACK = new MobDefKit(5, "Lumberjack", Material.IRON_AXE,
            List.of("§fWeapon: §7Axe", "§fArmor: §7Strong", "", "§7Speed and Strength Potions",
                    "§7Regeneration effect after mob kill", "§7Sheep as distraction",
                    "§7Dogs as companions"),
            List.of(MobDefKit.BEEF, MobTracker.TRACKER),
            List.of(LUMBERJACK_WEAPONS, LUMBERJACK_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop()));
    public static final MobDefKit ALCHEMIST = new MobDefKit(3, "Alchemist", Material.BLAZE_POWDER,
            List.of("§fWeapons: §7Fire Hoe, Fire Staff, Iceball", "§fArmor: §7Weak", "",
                    "§7Resistant against fire",
                    "§7Defence Snowmen, Blazes"),
            List.of(MobDefKit.BEEF, MobTracker.TRACKER),
            List.of(ALCHEMIST_WEAPONS, RANGED_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop()));
    public static final MobDefKit ARCHER = new MobDefKit(2, "Archer", Material.BOW,
            List.of("§fWeapons: §7Bows, Crossbow", "§fArmor: §7Weak"),
            List.of(MobDefKit.BEEF, MobTracker.TRACKER, new ItemStack(Material.ARROW)),
            List.of(ARCHER_WEAPONS, RANGED_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop()));
    public static final MobDefKit WIZARD = new MobDefKit(4, "Wizard",
            ExItemStack.getPotion(Material.POTION, PotionType.INSTANT_HEAL, false, false).getType(),
            List.of("§fWeapon: §7Wand", "§fArmor: §7Weak", "", "§7Instant Heal Potions"),
            List.of(MobDefKit.BEEF, PotionGenerator.INSTANT_HEAL, MobTracker.TRACKER),
            List.of(WIZARD_WEAPONS, RANGED_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop()));
    public static final MobDefKit[] KITS = new MobDefKit[]{KNIGHT, ARCHER, ALCHEMIST, WIZARD,
            LUMBERJACK};

    static {
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_FENCE, OAK_FENCE_ITEM.cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_FENCE_GATE, OAK_FENCE_GATE_ITEM.cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_PLANKS, OAK_PLANKS_ITEM.cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.OAK_SLAB, OAK_SLAB_ITEM.cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.IRON_BARS, IRON_BARS_ITEM.cloneWithId().asOne());
        BLOCK_ITEM_BY_TYPE.put(Material.COBBLESTONE_WALL,
                COBBLESTONE_WALL_ITEM.cloneWithId().asOne());
    }

    public final List<Supplier<Shop>> shopSuppliers;

    public MobDefKit(Integer id, String name, Material material, List<String> description,
            List<ItemStack> items, List<Supplier<Shop>> shopSuppliers) {
        super(id, name, material, description, items);
        this.shopSuppliers = shopSuppliers;
    }

    public List<Supplier<Shop>> getShopSuppliers() {
        return this.shopSuppliers;
    }

    public KitShop getShop(MobDefUser user) {
        return new KitShop(user);
    }

}
