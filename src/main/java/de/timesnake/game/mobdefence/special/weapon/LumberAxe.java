/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffectType;

public class LumberAxe extends SpecialWeapon implements Listener {

  private static final ExItemStack ITEM = new ExItemStack(Material.IRON_AXE).unbreakable()
      .immutable();

  private static final LevelableProperty.Builder TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .levelItem(ITEM)
      .addMaterialLevel(null, "Iron Axe", Material.IRON_AXE)
      .addMaterialLevel(new Price(6, Currency.SILVER), "Diamond Axe", Material.DIAMOND_AXE)
      .addMaterialLevel(new Price(32, Currency.BRONZE), "Netherite Axe", Material.NETHERITE_AXE);

  private static final LevelableProperty.Builder SHARPNESS = new LevelableProperty.Builder()
      .name("Sharpness")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(0)
      .levelEnchantment(Enchantment.SHARPNESS)
      .levelDescription("+1 Sharpness")
      .levelItem(ITEM)
      .addEnchantLevel(new Price(4, Currency.SILVER), 1)
      .addEnchantLevel(new Price(12, Currency.BRONZE), 2)
      .addEnchantLevel(new Price(4, Currency.GOLD), 3)
      .addEnchantLevel(new Price(19, Currency.BRONZE), 4)
      .addEnchantLevel(new Price(12, Currency.SILVER), 5)
      .addEnchantLevel(new Price(8, Currency.GOLD), 6)
      .addEnchantLevel(new Price(27, Currency.BRONZE), 7)
      .addEnchantLevel(new Price(16, Currency.SILVER), 8)
      .levelDescription("+2 Sharpness")
      .addEnchantLevel(new Price(14, Currency.GOLD), 10)
      .addEnchantLevel(new Price(51, Currency.BRONZE), 12)
      .addEnchantLevel(new Price(56, Currency.SILVER), 14)
      .addEnchantLevel(new Price(64, Currency.BRONZE), 16);

  private static final LevelableProperty.Builder KNOCKBACK = new LevelableProperty.Builder()
      .name("Knockback")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(0)
      .levelEnchantment(Enchantment.KNOCKBACK)
      .levelDescription("+1 Knockback")
      .levelItem(ITEM)
      .addEnchantLevel(new Price(4, Currency.SILVER), 1)
      .addEnchantLevel(new Price(12, Currency.BRONZE), 2)
      .addEnchantLevel(new Price(6, Currency.GOLD), 3)
      .addEnchantLevel(new Price(32, Currency.BRONZE), 4)
      .addEnchantLevel(new Price(29, Currency.SILVER), 5);

  private static final LevelableProperty.Builder SPEED_LEVELS = new LevelableProperty.Builder()
      .name("Attack Speed")
      .display(new ExItemStack(Material.FEATHER))
      .levelItem(ITEM)
      .defaultLevel(1)
      .levelDescription("+0.5 per sec.")
      .levelDecimalDigit(1)
      .levelLoreLine(1)
      .levelLoreName("Attack Speed")
      .levelUnit("per sec.")
      .addTagLevel(null, 6, u -> u.setAttackSpeed(6))
      .addTagLevel(new Price(12, Currency.BRONZE), 8, u -> u.setAttackSpeed(8))
      .addTagLevel(new Price(7, Currency.GOLD), 10, u -> u.setAttackSpeed(10))
      .addTagLevel(new Price(16, Currency.SILVER), 12, u -> u.setAttackSpeed(12))
      .addTagLevel(new Price(32, Currency.BRONZE), 14, u -> u.setAttackSpeed(14))
      .addTagLevel(new Price(18, Currency.GOLD), 16, u -> u.setAttackSpeed(16))
      .addTagLevel(new Price(64, Currency.BRONZE), 18, u -> u.setAttackSpeed(18));

  public static final UpgradeableGoodItem.Builder AXE = new UpgradeableGoodItem.Builder()
      .name("§6Axe")
      .display(ITEM.cloneWithId())
      .startItem(ITEM.cloneWithId())
      .addLevelableProperty(TYPE)
      .addLevelableProperty(SHARPNESS)
      .addLevelableProperty(KNOCKBACK)
      .addLevelableProperty(SPEED_LEVELS);

  public LumberAxe() {
    super(AXE.getStartItem());
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onEntityDeath(EntityDeathEvent e) {
    if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
      return;
    }

    LivingEntity entity = e.getEntity();

    if (entity.getKiller() == null) {
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
