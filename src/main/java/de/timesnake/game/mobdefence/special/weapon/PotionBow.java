/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionBow extends SpecialWeapon implements Listener {


  public static final UpgradeableGoodItem.Builder BOW = new UpgradeableGoodItem.Builder()
      .name("Splash Bow")
      .price(new Price(6, Currency.GOLD))
      .startItem(new ExItemStack(Material.BOW).addExEnchantment(Enchantment.INFINITY, 1)
          .setUnbreakable(true)
          .setDisplayName("§6Potion Bow"))
      .unlockedAtWave(5);

  public PotionBow() {
    super(BOW.getStartItem());
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onEntityShootBow(EntityShootBowEvent e) {
    if (!(e.getProjectile() instanceof Arrow arrow)) {
      return;
    }

    if (!(e.getEntity() instanceof Player)) {
      return;
    }

    User user = Server.getUser(((Player) e.getEntity()));
    ExItemStack item = new ExItemStack(e.getBow());

    if (!item.equals(BOW.getStartItem())) {
      return;
    }

    ItemStack potionItem = this.getNearestPotion(user);

    if (potionItem == null) {
      e.setCancelled(true);
      e.setConsumeItem(false);
      return;
    }

    ThrownPotion potion = arrow.getWorld().spawn(arrow.getLocation(), ThrownPotion.class);

    potion.setVelocity(arrow.getVelocity());
    potion.setItem(potionItem);
    potion.setPotionMeta(((PotionMeta) potionItem.getItemMeta()));
    potion.setShooter(user.getPlayer());

    Server.runTaskLaterSynchrony(() -> user.addItem(e.getConsumable()), 1,
        GameMobDefence.getPlugin());
  }

  private ItemStack getNearestPotion(User user) {
    if (user.getInventory().getItemInOffHand().getType().equals(Material.LINGERING_POTION)
        || user.getInventory().getItemInOffHand().getType().equals(Material.SPLASH_POTION)) {
      return user.getInventory().getItemInOffHand();
    }

    for (ItemStack item : user.getInventory()) {
      if (item.getType().equals(Material.LINGERING_POTION)
          || item.getType().equals(Material.SPLASH_POTION)) {
        return item;
      }
    }

    return null;
  }
}
