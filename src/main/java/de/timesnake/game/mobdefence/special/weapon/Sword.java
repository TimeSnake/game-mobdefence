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
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
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

  private static final LevelableProperty.Builder TYPE = new LevelableProperty.Builder()
      .name("Type")
      .display(new ExItemStack(Material.ANVIL))
      .defaultLevel(1)
      .addMaterialLevel(null, "Wooden Sword", Material.WOODEN_SWORD)
      .addMaterialLevel(new Price(8, Currency.BRONZE), "Stone Sword", Material.STONE_SWORD)
      .addMaterialLevel(new Price(12, Currency.SILVER), "Iron Sword", Material.IRON_SWORD)
      .addMaterialLevel(new Price(32, Currency.SILVER), "Diamond Sword", Material.DIAMOND_SWORD)
      .addMaterialLevel(new Price(16, Currency.GOLD), "Netherite Sword", Material.NETHERITE_SWORD);

  private static final LevelableProperty.Builder SHARPNESS = new LevelableProperty.Builder()
      .name("Sharpness")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(0)
      .levelEnchantment(Enchantment.SHARPNESS)
      .levelDescription("+1 Sharpness")
      .addEnchantLevel(new Price(9, Currency.BRONZE), 1)
      .addEnchantLevel(new Price(15, Currency.SILVER), 2)
      .addEnchantLevel(new Price(7, Currency.GOLD), 3)
      .addEnchantLevel(new Price(27, Currency.BRONZE), 4)
      .addEnchantLevel(new Price(25, Currency.SILVER), 5)
      .addEnchantLevel(new Price(15, Currency.GOLD), 6)
      .addEnchantLevel(new Price(48, Currency.BRONZE), 7)
      .addEnchantLevel(new Price(48, Currency.SILVER), 8)
      .addEnchantLevel(new Price(64, Currency.BRONZE), 9)
      .addEnchantLevel(new Price(64, Currency.SILVER), 10);

  private static final LevelableProperty.Builder SWEEPING_EDGE = new LevelableProperty.Builder()
      .name("Sweeping Edge")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(0)
      .levelEnchantment(Enchantment.SWEEPING_EDGE)
      .levelDescription("+1 Sweeping Edge")
      .addEnchantLevel(new Price(8, Currency.SILVER), 1)
      .addEnchantLevel(new Price(6, Currency.GOLD), 2)
      .addEnchantLevel(new Price(32, Currency.BRONZE), 3)
      .addEnchantLevel(new Price(45, Currency.SILVER), 4);

  public static final UpgradeableGoodItem.Builder SWORD = new UpgradeableGoodItem.Builder()
      .name("Sword")
      .display(new ExItemStack(Material.IRON_SWORD))
      .startItem(ITEM.cloneWithId())
      .addLevelableProperty(TYPE)
      .addLevelableProperty(SHARPNESS)
      .addLevelableProperty(SWEEPING_EDGE);


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
