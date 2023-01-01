/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffectType;

public class LumberAxe extends SpecialWeapon implements Listener {

    private static final ExItemStack ITEM = new ExItemStack(Material.IRON_AXE).unbreakable().immutable();

    private static final LevelType.Builder TYPE = new LevelType.Builder()
            .name("Type")
            .display(new ExItemStack(Material.ANVIL))
            .baseLevel(1)
            .addMaterialLvl(null, "Diamond Axe", Material.IRON_AXE)
            .addMaterialLvl(new Price(6, Currency.SILVER), "Diamond Axe", Material.DIAMOND_AXE)
            .addMaterialLvl(new Price(32, Currency.BRONZE), "Netherite Axe", Material.NETHERITE_AXE);

    private static final LevelType.Builder SHARPNESS = new LevelType.Builder()
            .name("Sharpness")
            .display(new ExItemStack(Material.RED_DYE))
            .baseLevel(0)
            .levelEnchantment(Enchantment.DAMAGE_ALL)
            .levelDescription("+1 Sharpness")
            .levelItem(ITEM)
            .addEnchantmentLvl(new Price(4, Currency.SILVER), 1)
            .addEnchantmentLvl(new Price(12, Currency.BRONZE), 2)
            .addEnchantmentLvl(new Price(4, Currency.GOLD), 3)
            .addEnchantmentLvl(new Price(19, Currency.BRONZE), 4)
            .addEnchantmentLvl(new Price(12, Currency.SILVER), 5)
            .addEnchantmentLvl(new Price(8, Currency.GOLD), 6)
            .addEnchantmentLvl(new Price(27, Currency.BRONZE), 7)
            .addEnchantmentLvl(new Price(16, Currency.SILVER), 8)
            .levelDescription("+2 Sharpness")
            .addEnchantmentLvl(new Price(14, Currency.GOLD), 10)
            .addEnchantmentLvl(new Price(51, Currency.BRONZE), 12)
            .addEnchantmentLvl(new Price(56, Currency.SILVER), 14)
            .addEnchantmentLvl(new Price(64, Currency.BRONZE), 16);

    private static final LevelType.Builder KNOCKBACK = new LevelType.Builder()
            .name("Knockback")
            .display(new ExItemStack(Material.FEATHER))
            .baseLevel(0)
            .levelEnchantment(Enchantment.KNOCKBACK)
            .levelDescription("+1 Knockback")
            .levelItem(ITEM)
            .addEnchantmentLvl(new Price(4, Currency.SILVER), 1)
            .addEnchantmentLvl(new Price(12, Currency.BRONZE), 2)
            .addEnchantmentLvl(new Price(6, Currency.GOLD), 3)
            .addEnchantmentLvl(new Price(32, Currency.BRONZE), 4)
            .addEnchantmentLvl(new Price(29, Currency.SILVER), 5);

    private static final LevelType.Builder SPEED_LEVELS = new LevelType.Builder()
            .name("Attack Speed")
            .display(new ExItemStack(Material.FEATHER))
            .levelItem(ITEM)
            .baseLevel(1)
            .levelDescription("+0.5 per sec.")
            .levelDecimalDigit(1)
            .levelLoreLine(1)
            .levelLoreName("Attack Speed")
            .levelUnit("per sec.")
            .addLoreLvl(null, 6, u -> u.setAttackSpeed(6))
            .addLoreLvl(new Price(12, Currency.BRONZE), 8, u -> u.setAttackSpeed(8))
            .addLoreLvl(new Price(7, Currency.GOLD), 10, u -> u.setAttackSpeed(10))
            .addLoreLvl(new Price(16, Currency.SILVER), 12, u -> u.setAttackSpeed(12))
            .addLoreLvl(new Price(32, Currency.BRONZE), 14, u -> u.setAttackSpeed(14))
            .addLoreLvl(new Price(18, Currency.GOLD), 16, u -> u.setAttackSpeed(16))
            .addLoreLvl(new Price(64, Currency.BRONZE), 18, u -> u.setAttackSpeed(18));

    public static final UpgradeableItem.Builder AXE = new UpgradeableItem.Builder()
            .name("§6Axe")
            .display(ITEM.cloneWithId())
            .baseItem(ITEM.cloneWithId())
            .addLvlType(TYPE)
            .addLvlType(SHARPNESS)
            .addLvlType(KNOCKBACK)
            .addLvlType(SPEED_LEVELS);

    public LumberAxe() {
        super(AXE.getBaseItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
            return;
        }

        LivingEntity entity = e.getEntity();

        if (entity.getKiller() == null || !(entity.getKiller() instanceof Player)) {
            return;
        }

        MobDefUser user = (MobDefUser) Server.getUser(entity.getKiller());

        if (user.getKit() != null && user.getKit().equals(MobDefKit.LUMBERJACK)) {
            user.addPotionEffect(PotionEffectType.REGENERATION, 4 * 20, 1);
            if (Math.random() == 0) {
                user.getPlayer().setAbsorptionAmount(user.getPlayer().getAbsorptionAmount() + 6);
            }
        }
    }
}
