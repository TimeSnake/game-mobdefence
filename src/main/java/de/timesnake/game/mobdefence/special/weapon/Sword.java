/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Sword extends SpecialWeapon implements Listener {

  public static final ExItemStack ITEM = new ExItemStack(Material.WOODEN_SWORD)
      .unbreakable()
      .immutable();

  private static final LevelType.Builder TYPE = new LevelType.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .baseLevel(1)
      .levelItem(ITEM)
      .addMaterialLvl(null, "Wooden Sword", Material.WOODEN_SWORD)
      .addMaterialLvl(new Price(8, Currency.BRONZE), "Stone Sword", Material.STONE_SWORD)
      .addMaterialLvl(new Price(12, Currency.SILVER), "Iron Sword", Material.IRON_SWORD)
      .addMaterialLvl(new Price(32, Currency.SILVER), "Diamond Sword", Material.DIAMOND_SWORD)
      .addMaterialLvl(new Price(16, Currency.GOLD), "Netherite Sword", Material.NETHERITE_SWORD);

  private static final LevelType.Builder SHARPNESS = new LevelType.Builder()
      .name("Sharpness")
      .display(new ExItemStack(Material.RED_DYE))
      .baseLevel(0)
      .levelEnchantment(Enchantment.SHARPNESS)
      .levelDescription("+1 Sharpness")
      .levelItem(ITEM)
      .addEnchantmentLvl(new Price(9, Currency.BRONZE), 1)
      .addEnchantmentLvl(new Price(15, Currency.SILVER), 2)
      .addEnchantmentLvl(new Price(7, Currency.GOLD), 3)
      .addEnchantmentLvl(new Price(27, Currency.BRONZE), 4)
      .addEnchantmentLvl(new Price(25, Currency.SILVER), 5)
      .addEnchantmentLvl(new Price(15, Currency.GOLD), 6)
      .addEnchantmentLvl(new Price(48, Currency.BRONZE), 7)
      .addEnchantmentLvl(new Price(48, Currency.SILVER), 8)
      .addEnchantmentLvl(new Price(64, Currency.BRONZE), 9)
      .addEnchantmentLvl(new Price(64, Currency.SILVER), 10);

  private static final LevelType.Builder SWEEPING_EDGE = new LevelType.Builder()
      .name("Sweeping Edge")
      .display(new ExItemStack(Material.FEATHER))
      .baseLevel(0)
      .levelEnchantment(Enchantment.SWEEPING_EDGE)
      .levelDescription("+1 Sweeping Edge")
      .levelItem(ITEM)
      .addEnchantmentLvl(new Price(8, Currency.SILVER), 1)
      .addEnchantmentLvl(new Price(6, Currency.GOLD), 2)
      .addEnchantmentLvl(new Price(32, Currency.BRONZE), 3)
      .addEnchantmentLvl(new Price(45, Currency.SILVER), 4);

  public static final UpgradeableItem.Builder SWORD = new UpgradeableItem.Builder()
      .name("Sword")
      .display(new ExItemStack(Material.IRON_SWORD))
      .baseItem(ITEM.cloneWithId())
      .addLvlType(TYPE)
      .addLvlType(SHARPNESS)
      .addLvlType(SWEEPING_EDGE);


  public Sword() {
    super(ITEM);
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void on(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Player)) {
      return;
    }

    if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
      return;
    }

    User user = Server.getUser(((Player) e.getDamager()));

    for (LivingEntity entity : user.getLocation().getNearbyLivingEntities(2, 1)) {
      if (MobDefServer.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
        entity.damage(2, user.getPlayer());
      }
    }
  }
}
