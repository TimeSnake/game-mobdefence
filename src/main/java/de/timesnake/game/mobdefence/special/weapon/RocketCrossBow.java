/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.FireworkRocketBuilder;
import net.minecraft.network.chat.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class RocketCrossBow extends SpecialWeapon implements Listener {

  private static final ExItemStack ITEM = new ExItemStack(Material.CROSSBOW).enchant()
      .unbreakable()
      .setLore("§cAim on block or entity to target nearby entities")
      .addExEnchantment(Enchantment.ARROW_INFINITE, 1)
      .immutable();

  private static final LevelType.Builder DAMAGE_LEVELS = new LevelType.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .baseLevel(1)
      .levelDescription("+1 ❤")
      .levelUnit("❤")
      .levelDecimalDigit(0)
      .levelLoreLine(2)
      .levelLoreName("Damage")
      .levelItem(ITEM)
      .addLoreLvl(null, 15)
      .addLoreLvl(new Price(24, Currency.BRONZE), 6)
      .addLoreLvl(new Price(24, Currency.SILVER), 7)
      .addLoreLvl(new Price(24, Currency.GOLD), 8)
      .addLoreLvl(new Price(48, Currency.SILVER), 9);

  public static final UpgradeableItem.Builder CROSSBOW = new UpgradeableItem.Builder()
      .name("Rocket Crossbow")
      .price(new Price(16, Currency.SILVER))
      .display(ITEM.cloneWithId())
      .baseItem(ITEM.cloneWithId())
      .addLvlType(DAMAGE_LEVELS);


  public RocketCrossBow() {
    super(CROSSBOW.getBaseItem());
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onEntityShootBow(EntityShootBowEvent e) {
    if (!(e.getProjectile() instanceof Arrow)) {
      return;
    }

    if (!(e.getEntity() instanceof Player)) {
      return;
    }

    User user = Server.getUser((Player) e.getEntity());
    ExItemStack item = new ExItemStack(e.getBow());

    if (!item.equals(CROSSBOW.getBaseItem())) {
      return;
    }

    e.setConsumeItem(false);
    e.setCancelled(true);

    int damage = DAMAGE_LEVELS.getNumberFromLore(item, Integer::valueOf);

    World world = user.getWorld();
    Entity projectile = e.getProjectile();

    FireworkRocketBuilder builder = new FireworkRocketBuilder()
        .applyOnEntity(entity -> {
          entity.setPos(projectile.getX(), projectile.getY(), projectile.getZ());
          entity.setRot(projectile.getYaw(), projectile.getPitch());
          entity.setDeltaMovement(projectile.getVelocity().getX(), projectile.getVelocity().getY(),
              projectile.getVelocity().getZ());
          entity.setOwner(user.getMinecraftPlayer());
          entity.setCustomName(Component.literal(String.valueOf(damage)));
          entity.setCustomNameVisible(false);
        });

    EntityManager.spawnEntity(world, builder.build(user.getExWorld().getHandle()));
  }

  @EventHandler
  public void onFireworkExplode(EntityDamageByEntityEvent e) {
    if (!(e.getDamager() instanceof Firework firework)) {
      return;
    }

    if (e.getEntity() instanceof Player) {
      e.setCancelled(true);
      e.setDamage(0);
      return;
    }

    if (firework.getCustomName() != null) {
      int damage = Integer.parseInt(firework.getCustomName());
      e.setDamage(damage);
    }
  }
}
