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
import de.timesnake.game.mobdefence.special.weapon.bullet.BulletManager;
import de.timesnake.game.mobdefence.special.weapon.bullet.PiercingBullet;
import de.timesnake.game.mobdefence.special.weapon.bullet.TargetFinder;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class RocketCrossBow extends SpecialWeapon implements Listener {

  private static final ExItemStack ITEM = new ExItemStack(Material.CROSSBOW).enchant()
      .unbreakable()
      .setLore("§cAim on block or entity to target nearby entities")
      .addExEnchantment(Enchantment.ARROW_INFINITE, 1)
      .immutable();

  private static final LevelType.Builder MULTISHOT_LEVELS = new LevelType.Builder()
      .name("Multishot")
      .display(new ExItemStack(Material.TORCH))
      .baseLevel(1)
      .levelDescription("+2 Arrows")
      .levelDecimalDigit(0)
      .levelLoreLine(4)
      .levelUnit("arrows")
      .levelLoreName("Multishot")
      .levelItem(ITEM)
      .addLoreLvl(null, 2)
      .addLoreLvl(new Price(8, Currency.BRONZE), 3)
      .addLoreLvl(new Price(16, Currency.BRONZE), 5)
      .addLoreLvl(new Price(6, Currency.SILVER), 7)
      .addLoreLvl(new Price(12, Currency.SILVER), 9)
      .addLoreLvl(new Price(4, Currency.GOLD), 11)
      .addLoreLvl(new Price(8, Currency.GOLD), 13)
      .addLoreLvl(new Price(32, Currency.BRONZE), 15)
      .addLoreLvl(new Price(16, Currency.SILVER), 17);


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
      .addLvlType(MULTISHOT_LEVELS)
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

    ItemStack arrow = e.getConsumable();

    e.setCancelled(true);
    e.setConsumeItem(false);

    int multiShot = MULTISHOT_LEVELS.getNumberFromLore(item, Integer::valueOf);
    int damage = DAMAGE_LEVELS.getNumberFromLore(item, Integer::valueOf);
    // int piercing = Integer.parseInt(PIERCING_LEVELS_LEVELS.getValueFromLore(item.getLore()));

    BulletManager bulletManager = MobDefServer.getWeaponManager().getBulletManager();

    Collection<LivingEntity> hitTargets = new HashSet<>();

    Server.runTaskTimerSynchrony((time) ->
            bulletManager.shootBullet(new FollowerArrow(user, 0.9, damage * 2D, 0, hitTargets),
                user.getLocation().getDirection().normalize()), multiShot, true, 0, 4,
        GameMobDefence.getPlugin());

    Server.runTaskLaterSynchrony(() -> user.addItem(arrow), 1, GameMobDefence.getPlugin());
  }

  private static class FollowerArrow extends PiercingBullet {

    public FollowerArrow(User user, double speed, Double damage, int piercing,
        Collection<LivingEntity> hitTargets) {
      super(user, user.getEyeLocation().add(0, -0.5, 0), TargetFinder.NEAREST_ATTACKER, speed,
          damage, piercing
          , hitTargets);
    }

    @Override
    public Entity spawn(Location location) {
      Arrow arrow = location.getWorld().spawnArrow(location, new Vector(), 1F, 0.2F);
      arrow.setShooter(this.shooter.getPlayer());
      arrow.setGravity(false);
      arrow.setInvulnerable(true);
      return arrow;
    }

    @Override
    public LivingEntity getFirstTarget() {
      Entity entity = this.shooter.getTargetEntity(100);
      Location loc;
      if (entity != null) {
        loc = entity.getLocation();
      } else {
        Block block = this.shooter.getTargetBlock(100);
        if (block != null) {
          loc = block.getLocation();
        } else {
          loc = this.shooter.getLocation();
        }
      }

      return TargetFinder.NEAREST_ATTACKER.nextTarget(this.hitTargets, loc);
    }

    @Override
    public WeaponTargetType onHit(LivingEntity entity) {
      return super.onHit(entity);
    }
  }
}
