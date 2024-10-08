/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.trap;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.SimpleGood;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum TrapMaker {

  EXPLOSION(new SimpleGood.Builder()
      .slot(7)
      .price(new Price(8, Currency.BRONZE), 1, 8),
      new ExItemStack(Material.CRIMSON_BUTTON, "§6Explosion Trap")
          .setLore("§fRadius: §73 blocks", "§7Explodes if 3 mobs are nearby")) {
    @Override
    public Trap newInstance(ExBlock block) {
      return new RangedTrap(block, 2, 3) {
        @Override
        public boolean trigger(Collection<LivingEntity> entities) {
          this.getLocation().getWorld()
              .createExplosion(this.getLocation(), 4, false, false);
          return super.trigger(entities);
        }
      };
    }
  },

  ARROW(new SimpleGood.Builder()
      .slot(8)
      .price(new Price(3, Currency.SILVER), 1, 8),
      new ExItemStack(Material.STONE_BUTTON, "§6Arrow Trap")
          .setLore("§fRadius: §77 blocks", "§fUses: §716", "§7Shoots against mobs")) {
    @Override
    public Trap newInstance(ExBlock block) {
      return new MultipleRangedTrap(block, 7, 1, 16) {
        @Override
        public boolean trigger(Collection<LivingEntity> entities) {

          for (LivingEntity entity : entities) {
            this.getLocation().getWorld()
                .playSound(this.getLocation(), Sound.ENTITY_ARROW_SHOOT, 2, 1);
            Location eyeLoc = entity.getEyeLocation();
            Location loc = this.getLocation();
            Vector vec =
                new Vector(eyeLoc.getX(), eyeLoc.getY(), eyeLoc.getZ()).subtract(
                    new Vector(loc.getX(), loc.getY(), loc.getZ()));

            loc.getWorld().spawnArrow(loc, vec, 5, 1);
          }

          return super.trigger(entities);
        }
      };
    }
  },

  SLOWNESS(new SimpleGood.Builder()
      .slot(16)
      .price(new Price(7, Currency.BRONZE), 1, 8),
      new ExItemStack(Material.POLISHED_BLACKSTONE_BUTTON, "§6Slowness Trap")
          .setLore("§fRadius: §75 blocks", "§fUses: §73",
              "§7Gives mobs slowness V for 20s")) {
    @Override
    public Trap newInstance(ExBlock block) {
      return new MultipleRangedTrap(block, 5, 2, 3) {
        @Override
        public boolean trigger(Collection<LivingEntity> entities) {
          this.getLocation().getWorld()
              .playSound(this.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 2, 1);
          for (LivingEntity entity : entities) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 4));
          }

          return super.trigger(entities);
        }
      };
    }
  },

  POISON(new SimpleGood.Builder()
      .slot(17)
      .price(new Price(7, Currency.BRONZE), 1, 8),
      new ExItemStack(Material.WARPED_BUTTON, "§6Poison Trap")
          .setLore("§fRadius: §74 blocks", "§7Gives mobs poison III for 20s")) {
    @Override
    public Trap newInstance(ExBlock block) {
      return new RangedTrap(block, 4, 4) {
        @Override
        public boolean trigger(Collection<LivingEntity> entities) {
          this.getLocation().getWorld()
              .playSound(this.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 2, 1);
          for (LivingEntity entity : entities) {
            entity.addPotionEffect(
                new PotionEffect(PotionEffectType.POISON, 20 * 20, 3));
          }

          return super.trigger(entities);
        }
      };
    }
  };


  public static List<SimpleGood.Builder> getShopTrades() {
    List<SimpleGood.Builder> trades = new ArrayList<>();

    for (TrapMaker trapMaker : TrapMaker.values()) {
      trades.add(trapMaker.getTrade());
    }
    return trades;
  }

  private final ExItemStack item;

  public SimpleGood.Builder getTrade() {
    return trade;
  }

  private final SimpleGood.Builder trade;

  public ExItemStack getItem() {
    return item;
  }

  TrapMaker(SimpleGood.Builder trade, ExItemStack item) {
    trade.giveItems(item);
    this.trade = trade;
    this.item = item;
  }

  public abstract Trap newInstance(ExBlock block);
}
