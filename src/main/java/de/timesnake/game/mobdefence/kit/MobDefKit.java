/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.basic.loungebridge.util.user.Kit;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Shop;
import de.timesnake.game.mobdefence.special.PotionGenerator;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.MobTracker;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class MobDefKit extends Kit implements KitItems {

    public static final MobDefKit KNIGHT = new Builder()
            .id(1)
            .name("Knight")
            .material(Material.IRON_SWORD)
            .addDescription("§fWeapons: §7Sword, Axe", "§fArmor: §7Strong", "", "§7Resistance aura")
            .addItems(
                    new ExItemStack(Material.SHIELD).unbreakable().setSlot(EquipmentSlot.OFF_HAND),
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
    public static final MobDefKit ARCHER = new Builder()
            .id(2)
            .name("Archer")
            .material(Material.BOW)
            .addDescription("§fWeapons: §7Bows, Crossbow", "§fArmor: §7Weak")
            .addItems(MobDefKit.BEEF, MobTracker.TRACKER, new ItemStack(Material.ARROW))
            .addShopSuppliers(ARCHER_WEAPONS, RANGED_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop())
            .build();
    public static final MobDefKit WIZARD = new Builder()
            .id(4)
            .name("Wizard")
            .material(ExItemStack.getPotion(Material.POTION, PotionType.INSTANT_HEAL,
                    false, false).getType())
            .addDescription("§fWeapon: §7Wand", "§fArmor: §7Weak", "", "§7Instant Heal Potions")
            .addItems(MobDefKit.BEEF, PotionGenerator.INSTANT_HEAL, MobTracker.TRACKER)
            .addShopSuppliers(WIZARD_WEAPONS, RANGED_ARMOR,
                    () -> MobDefServer.getBaseShops().getBasicShop(),
                    () -> MobDefServer.getBaseShops().getBlockShop(),
                    () -> MobDefServer.getBaseShops().getTeamShop())
            .build();

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

    public MobDefKit(Builder builder) {
        super(builder);
        this.shopSuppliers = builder.shopSuppliers;
    }

    public List<Supplier<Shop>> getShopSuppliers() {
        return this.shopSuppliers;
    }

    public KitShop getShop(MobDefUser user) {
        return new KitShop(user);
    }

    public static class Builder extends Kit.Builder {

        private final LinkedList<Supplier<Shop>> shopSuppliers = new LinkedList<>();

        @Override
        public Builder id(int id) {
            return (Builder) super.id(id);
        }

        @Override
        public Builder name(String name) {
            return (Builder) super.name(name);
        }

        @Override
        public Builder addDescription(String... lines) {
            return (Builder) super.addDescription(lines);
        }

        @Override
        public Builder material(Material material) {
            return (Builder) super.material(material);
        }

        @Override
        public Builder addApplier(Consumer<GameUser> applier) {
            return ((Builder) super.addApplier(applier));
        }

        @Override
        public Builder addItems(ItemStack... items) {
            return ((Builder) super.addItems(items));
        }

        @Override
        public Builder addEffect(PotionEffectType effectType, int amplifier) {
            return ((Builder) super.addEffect(effectType, amplifier));
        }

        @SafeVarargs
        public final Builder addShopSuppliers(Supplier<Shop>... suppliers) {
            for (Supplier<Shop> supplier : suppliers) {
                this.shopSuppliers.addLast(supplier);
            }
            return this;
        }

        @Override
        public void checkBuild() {
            super.checkBuild();
        }

        @Override
        public MobDefKit build() {
            this.checkBuild();
            return new MobDefKit(this);
        }
    }

}
