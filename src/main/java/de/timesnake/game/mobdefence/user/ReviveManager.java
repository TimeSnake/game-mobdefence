/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.entity.HoloDisplay;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelableProperty;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableGood;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.function.BiConsumer;

public class ReviveManager {

  private static final double RADIUS = 3;

  private static final BiConsumer<MobDefUser, Integer> RESPAWN = (u, i) -> MobDefServer.getMobDefUserManager()
      .getReviveManager().setReviveRespawnTime(i);
  private static final LevelableProperty.Builder RESPAWN_TIME_LEVELS = new LevelableProperty.Builder()
      .name("Respawn Time")
      .display(new ExItemStack(Material.WHITE_CANDLE))
      .defaultLevel(1)
      .levelDescription("-1s Revive Time")
      .addLevel(null, (MobDefUser u) -> RESPAWN.accept(u, 7))
      .addLevel(new Price(3, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 6))
      .addLevel(new Price(5, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 5))
      .addLevel(new Price(7, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 4))
      .addLevel(new Price(9, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 3));


  private static final BiConsumer<MobDefUser, Integer> DESPAWN = (u, i) -> MobDefServer.getMobDefUserManager()
      .getReviveManager().setReviveDespawnTime(i);
  private static final LevelableProperty.Builder DESPAWN_TIME_LEVELS = new LevelableProperty.Builder()
      .name("Despawn Time")
      .display(new ExItemStack(Material.BLACK_CANDLE))
      .defaultLevel(1)
      .levelDescription("+3s Despawn Time")
      .addLevel(null, (MobDefUser u) -> DESPAWN.accept(u, 30))
      .addLevel(new Price(2, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 33))
      .addLevel(new Price(4, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 36))
      .addLevel(new Price(6, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 39))
      .addLevel(new Price(8, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 42))
      .addLevel(new Price(10, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 45))
      .addLevel(new Price(12, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 48))
      .addLevel(new Price(14, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 51))
      .addLevel(new Price(16, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 54))
      .addLevel(new Price(18, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 57))
      .addLevel(new Price(20, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 60));

  public static final UpgradeableGood.Builder REVIVE = new UpgradeableGood.Builder() {
    @Override
    public UpgradeableGood build() {
      this.checkBuild();
      return new UpgradeableGood(this) {
        @Override
        public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
          LevelableProperty levelType = this.levelType.get1(item);
          if (levelType == null) {
            return;
          }
          levelType.tryLevelUp(user);
          inv.setItemStack(levelType.getDisplayItem());
        }
      };
    }
  }
      .name("Revive")
      .display(new ExItemStack(Material.TOTEM_OF_UNDYING))
      .addLevelableProperty(RESPAWN_TIME_LEVELS)
      .addLevelableProperty(DESPAWN_TIME_LEVELS);

  private int reviveRespawnTime = 7;
  private int reviveDespawnTime = 30;

  public DeadPlayer addDeadUser(MobDefUser user) {
    DeadPlayer deadPlayer = new DeadPlayer(user, user.getExLocation());
    deadPlayer.spawn();
    deadPlayer.startDying(user);
    return deadPlayer;
  }

  private void setReviveRespawnTime(int time) {
    this.reviveRespawnTime = time;
  }

  private void setReviveDespawnTime(int time) {
    this.reviveDespawnTime = time;
  }

  public class DeadPlayer extends de.timesnake.basic.loungebridge.util.tool.advanced.DeadPlayer {

    private BukkitTask deadTask;
    private HoloDisplay displayEntity;

    public DeadPlayer(User user, ExLocation location) {
      super(user, location);
    }

    private void startDying(MobDefUser user) {
      this.displayEntity = new HoloDisplay(ExLocation.fromLocation(this.bodyEntity.getLocation().add(0, 0.5, 0)),
          List.of("§cDead in 30s"));
      this.displayEntity.setPublic(true);

      Server.getEntityManager().registerEntity(this.displayEntity);

      this.deadTask = Server.runTaskTimerAsynchrony(new DyingProcess(user, ReviveManager.this.reviveDespawnTime),
          0, 20, GameMobDefence.getPlugin());
    }

    public void remove() {
      if (this.deadTask != null) {
        this.deadTask.cancel();
      }
      this.despawn();
      Server.getEntityManager().unregisterEntity(this.displayEntity);
    }

    private class DyingProcess implements Runnable {

      private final MobDefUser user;
      private int reviveTime;
      private int despawnTime;

      DyingProcess(MobDefUser user, int despawnTime) {
        this.user = user;
        this.despawnTime = despawnTime;
        this.reviveTime = 0;
      }

      @Override
      public void run() {
        ((MobDefUser) DeadPlayer.this.getUser()).setReviveUser(null);

        for (MobDefUser user : MobDefServer.getAliveUsers()) {
          if (!((MobDefUser) DeadPlayer.this.getUser()).isBeingRevived()) {
            if (DeadPlayer.this.getLocation().distanceSquared(user.getLocation()) <= RADIUS) {
              ((MobDefUser) DeadPlayer.this.getUser()).setReviveUser(user);
              break;
            }
          }
        }

        if (user.isBeingRevived()) {
          if (reviveTime == ReviveManager.this.reviveRespawnTime) {
            MobDefServer.broadcastGameTDMessage(user.getTDChatName() + "§w was revived by "
                + user.getReviveUser().getTDChatName());

            DeadPlayer.this.remove();
            Server.getEntityManager().unregisterEntity(DeadPlayer.this.displayEntity);

            Server.runTaskSynchrony(user::leaveSpectatorAndRejoin, GameMobDefence.getPlugin());
            DeadPlayer.this.deadTask.cancel();
            return;
          }

          user.getReviveUser().playSound(Sound.ENTITY_PLAYER_LEVELUP, 2);
          user.getReviveUser().sendPluginTDMessage(Plugin.MOB_DEFENCE, "§sReviving " + user.getTDChatName()
              + "§s, §v" + (reviveRespawnTime - reviveTime) + "s");

          DeadPlayer.this.displayEntity.setText(List.of("§2Revived in " + (reviveRespawnTime - reviveTime) + "s"));

          reviveTime++;
        } else {
          this.reviveTime = 0;

          if (this.despawnTime <= 0) {
            MobDefServer.broadcastGameTDMessage(user.getTDChatName() + "§w is now resting in pieces");
            DeadPlayer.this.remove();
            return;
          }

          DeadPlayer.this.displayEntity.setText(List.of("§cDead in " + this.despawnTime + "s"));
        }

        this.despawnTime--;
      }
    }
  }

}
