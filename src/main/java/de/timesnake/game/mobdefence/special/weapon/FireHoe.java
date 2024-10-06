/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.EntityDamageByUserEvent;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGoodItem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FireHoe extends CooldownWeapon implements Listener {

  public static final int COOLDOWN = 7;
  public static final int FIRE_WIND_COOLDOWN = 40;

  private static final ExItemStack ITEM = new ExItemStack(Material.GOLDEN_HOE)
      .setDisplayName("§6Frozen Fire Stick")
      .immutable();

  private static final LevelableProperty.Builder DAMAGE_LEVELS = new LevelableProperty.Builder()
      .name("Damage")
      .display(new ExItemStack(Material.RED_DYE))
      .defaultLevel(1)
      .levelDescription("+1 ❤")
      .levelUnit("❤")
      .levelDecimalDigit(0)
      .levelLoreLine(1)
      .levelLoreName("Damage")
      .levelItem(ITEM)
      .addTagLevel(null, 2)
      .addTagLevel(new Price(5, Currency.BRONZE), 3)
      .addTagLevel(new Price(5, Currency.SILVER), 4)
      .addTagLevel(new Price(5, Currency.GOLD), 5)
      .addTagLevel(new Price(32, Currency.BRONZE), 6)
      .addTagLevel(new Price(32, Currency.SILVER), 7);

  private static final LevelableProperty.Builder BURNING_TIME_LEVELS = new LevelableProperty.Builder()
      .name("Burning Time")
      .display(new ExItemStack(Material.BLAZE_POWDER))
      .defaultLevel(1)
      .levelDescription("+1 Second")
      .levelUnit("s")
      .levelDecimalDigit(0)
      .levelLoreLine(2)
      .levelLoreName("Burning Time")
      .levelItem(ITEM)
      .addTagLevel(null, 3)
      .addTagLevel(new Price(5, Currency.BRONZE), 4)
      .addTagLevel(new Price(4, Currency.SILVER), 5)
      .addTagLevel(new Price(3, Currency.GOLD), 6)
      .addTagLevel(new Price(12, Currency.BRONZE), 7)
      .addTagLevel(new Price(14, Currency.SILVER), 8)
      .addTagLevel(new Price(10, Currency.GOLD), 9);

  private static final LevelableProperty.Builder SLOWNESS_LEVELS = new LevelableProperty.Builder()
      .name("Slowness")
      .display(new ExItemStack(Material.FEATHER))
      .defaultLevel(1)
      .levelDescription("+1 Second")
      .levelUnit("s")
      .levelDecimalDigit(0)
      .levelLoreLine(3)
      .levelLoreName("Slowness")
      .addTagLevel(null, 3)
      .levelItem(ITEM)
      .addTagLevel(new Price(24, Currency.BRONZE), 4)
      .addTagLevel(new Price(48, Currency.BRONZE), 5);

  public static final UpgradeableGoodItem.Builder FIRE_HOE = new UpgradeableGoodItem.Builder()
      .name("Frozen Fire Hoe")
      .display(new ExItemStack(Material.GOLDEN_HOE).setDisplayName("§6Frozen Fire Hoe").enchant())
      .startItem(ITEM.cloneWithId().enchant().setUnbreakable(true))
      .addLevelableProperty(DAMAGE_LEVELS)
      .addLevelableProperty(BURNING_TIME_LEVELS)
      .addLevelableProperty(SLOWNESS_LEVELS);


  private final Set<User> cooldownUsers = new HashSet<>();

  public FireHoe() {
    super(FIRE_HOE.getStartItem());
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByUserEvent event) {
    if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(event.getEntity().getType())) {
      return;
    }

    User user = event.getUser();

    ExItemStack hoe = ExItemStack.getItem(user.getInventory().getItemInMainHand(), false);

    if (hoe == null || !hoe.equals(FIRE_HOE.getStartItem())) {
      return;
    }

    if (this.cooldownUsers.contains(user)) {
      return;
    }

    this.cooldownUsers.add(user);

    int damage = DAMAGE_LEVELS.getValueFromItem(hoe);
    int burningTime = BURNING_TIME_LEVELS.getValueFromItem(hoe);
    int slowness = SLOWNESS_LEVELS.getValueFromItem(hoe);

    LivingEntity entity = (LivingEntity) event.getEntity();

    event.setDamage(damage * 2);

    entity.setFireTicks(burningTime * 20);
    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, slowness - 1));

    Server.runTaskLaterSynchrony(() -> cooldownUsers.remove(user), COOLDOWN,
        GameMobDefence.getPlugin());
  }

  @Override
  public void onInteract(ExItemStack item, MobDefUser user) {
    Location loc =
        user.getLocation().add(0, 1.5, 0)
            .add(user.getLocation().getDirection().normalize().multiply(0.3));
    World world = loc.getWorld();

    for (double angle = -Math.PI / 10; angle < Math.PI / 9; angle += Math.PI * 0.05) {
      Vector vec = loc.getDirection().clone().rotateAroundY(angle).normalize();
      double x = vec.getX();
      double z = vec.getZ();

      world.spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ(), 2, x, 0, z, 0,
          null);
    }

    for (LivingEntity entity : user.getLocation().getNearbyLivingEntities(5,
        e -> MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getType()))) {
      Vector vec = entity.getLocation().toVector().subtract(user.getLocation().toVector());
      float angle = vec.angle(new Vector(0, 0, 1));

      if (angle > -15 && angle < 20) {
        entity.damage(4);
        entity.setFireTicks(20 * 3);
      }
    }

  }

  @Override
  public int getCooldown(ExItemStack item) {
    return FIRE_WIND_COOLDOWN;
  }
}
