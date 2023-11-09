/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExInventory;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.entity.HoloDisplay;
import de.timesnake.basic.bukkit.util.world.entity.PacketPlayer;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Upgradeable;
import de.timesnake.library.basic.util.Tuple;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.entity.PlayerBuilder;
import de.timesnake.library.packets.core.packet.out.entity.ClientboundSetEntityDataPacketBuilder;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ReviveManager {

  private static final double RADIUS = 3;

  private static final BiConsumer<MobDefUser, Integer> RESPAWN = (u, i) -> MobDefServer.getMobDefUserManager()
      .getReviveManager().setReviveRespawnTime(i);
  private static final LevelType.Builder RESPAWN_TIME_LEVELS = new LevelType.Builder()
      .name("Respawn Time")
      .display(new ExItemStack(Material.PLAYER_HEAD))
      .baseLevel(1)
      .levelDescription("-1s Revive Time")
      .addLvl(null, (MobDefUser u) -> RESPAWN.accept(u, 7))
      .addLvl(new Price(3, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 6))
      .addLvl(new Price(5, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 5))
      .addLvl(new Price(7, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 4))
      .addLvl(new Price(9, Currency.EMERALD), (MobDefUser u) -> RESPAWN.accept(u, 3));


  private static final BiConsumer<MobDefUser, Integer> DESPAWN = (u, i) -> MobDefServer.getMobDefUserManager()
      .getReviveManager().setReviveDespawnTime(i);
  private static final LevelType.Builder DESPAWN_TIME_LEVELS = new LevelType.Builder()
      .name("Despawn Time")
      .display(new ExItemStack(Material.SKELETON_SKULL))
      .baseLevel(1)
      .levelDescription("+3s Despawn Time")
      .addLvl(null, (MobDefUser u) -> DESPAWN.accept(u, 30))
      .addLvl(new Price(2, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 33))
      .addLvl(new Price(4, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 36))
      .addLvl(new Price(6, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 39))
      .addLvl(new Price(8, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 42))
      .addLvl(new Price(10, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 45))
      .addLvl(new Price(12, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 48))
      .addLvl(new Price(14, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 51))
      .addLvl(new Price(16, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 54))
      .addLvl(new Price(18, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 57))
      .addLvl(new Price(20, Currency.EMERALD), (MobDefUser u) -> DESPAWN.accept(u, 60));

  public static final Upgradeable.Builder REVIVE = new Upgradeable.Builder() {
    @Override
    public Upgradeable build() {
      this.checkBuild();
      return new Upgradeable(this) {
        @Override
        public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
          LevelType levelType = this.levelType.get1(item);
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
      .addLvlType(RESPAWN_TIME_LEVELS)
      .addLvlType(DESPAWN_TIME_LEVELS);

  private final HashMap<MobDefUser, Location> deathLocationsByUser = new HashMap<>();
  private final Map<MobDefUser, BukkitTask> deadTasksByUser = new HashMap<>();
  private final Map<MobDefUser, HoloDisplay> displayEntitiesByUser = new HashMap<>();
  private int reviveRespawnTime = 7;
  private int reviveDespawnTime = 30;
  private BukkitTask revivingCheckTask;

  public PacketPlayer addDeadUser(MobDefUser user, Location location) {
    this.deathLocationsByUser.put(user, location);

    Tuple<String, String> textures = user.asPlayerBuilder().getTextures();
    ExLocation loc = user.getExLocation();

    Player deadBody = PlayerBuilder.ofName(user.getName(), textures.getA(), textures.getB())
        .applyOnEntity(e -> {
          e.setPos(loc.getX(), loc.getY() + 0.2, loc.getZ());
          e.setRot(120, 0);
          e.setNoGravity(true);
          e.setCustomName(net.minecraft.network.chat.Component.literal(user.getName()));
          e.setCustomNameVisible(true);
          e.setPose(Pose.SLEEPING);
        })
        .build();

    PacketPlayer player = new PacketPlayer(deadBody, loc);
    player.setPoseTag(ClientboundSetEntityDataPacketBuilder.Type.SLEEPING, true);

    Server.getEntityManager().registerEntity(player);

    this.startDying(user, deadBody);

    return player;
  }

  private void startDying(MobDefUser user, Player deadBody) {

    HoloDisplay stand = new HoloDisplay(ExLocation.fromLocation(deadBody.getBukkitEntity().getLocation().add(0, -1.1, 0)),
        List.of("§cDead in 30s"));
    stand.setPublic(true);

    this.displayEntitiesByUser.put(user, stand);

    Server.getEntityManager().registerEntity(stand);

    this.deadTasksByUser.put(user, Server.runTaskTimerAsynchrony(new DyingProcess(user, this.reviveDespawnTime),
        0, 20, GameMobDefence.getPlugin()));
  }

  public void run() {
    this.revivingCheckTask = Server.runTaskTimerSynchrony(() -> {
      for (Map.Entry<MobDefUser, Location> entry : this.deathLocationsByUser.entrySet()) {

        MobDefUser reviving = null;

        for (MobDefUser user : MobDefServer.getAliveUsers()) {
          if (entry.getValue().distanceSquared(user.getLocation()) <= RADIUS) {
            if (!entry.getKey().isBeingRevived()) {
              this.reviveUser(user, entry.getKey());
            }
            reviving = user;
            break;
          }
        }

        entry.getKey().setBeingRevivedUser(reviving);

      }
    }, 0, 20, GameMobDefence.getPlugin());
  }

  public void stop() {
    if (this.revivingCheckTask != null) {
      this.revivingCheckTask.cancel();
    }
    this.clear();
  }

  public void clear() {
    for (Map.Entry<MobDefUser, BukkitTask> entry : this.deadTasksByUser.entrySet()) {
      entry.getValue().cancel();
      ReviveManager.this.removeDyingUser(entry.getKey(), false);
    }

    this.deathLocationsByUser.clear();
    this.displayEntitiesByUser.clear();
    this.deadTasksByUser.clear();
  }

  private void reviveUser(MobDefUser user, MobDefUser deadUser) {
    if (deadUser.isBeingRevived()) {
      return;
    }

    PacketPlayer deadBody = deadUser.getDeadBody();

    if (deadBody == null) {
      return;
    }

    deadUser.setBeingRevivedUser(user);
  }

  public void removeDyingUser(MobDefUser user, boolean removeFromList) {
    if (removeFromList) {
      ReviveManager.this.deadTasksByUser.remove(user).cancel();
    } else {
      ReviveManager.this.deadTasksByUser.get(user).cancel();
    }

    Server.getEntityManager().unregisterEntity(user.getDeadBody());
    Server.getEntityManager().unregisterEntity(this.displayEntitiesByUser.remove(user));

    if (removeFromList) {
      this.deathLocationsByUser.remove(user);
    }
  }

  private void setReviveRespawnTime(int time) {
    this.reviveRespawnTime = time;
  }

  private void setReviveDespawnTime(int time) {
    this.reviveDespawnTime = time;
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
      HoloDisplay entity = ReviveManager.this.displayEntitiesByUser.get(user);

      if (user.isBeingRevived()) {
        if (reviveTime == ReviveManager.this.reviveRespawnTime) {
          MobDefServer.broadcastGameMessage(user.getChatNameComponent()
              .append(Component.text(" was revived by ", ExTextColor.WARNING))
              .append(user.getBeingRevivedUser().getChatNameComponent()));
          ReviveManager.this.removeDyingUser(user, true);
          Server.runTaskSynchrony(user::leaveSpectatorAndRejoin,
              GameMobDefence.getPlugin());
          return;
        }

        user.getBeingRevivedUser().playSound(Sound.ENTITY_PLAYER_LEVELUP, 2);
        user.getBeingRevivedUser().sendPluginMessage(Plugin.MOB_DEFENCE,
            Component.text("Reviving ", ExTextColor.PERSONAL)
                .append(user.getChatNameComponent())
                .append(Component.text(
                    ", " + (reviveRespawnTime - reviveTime) + "s",
                    ExTextColor.PERSONAL)));

        entity.setText(List.of("§2Revived in " + (reviveRespawnTime - reviveTime) + "s"));

        reviveTime++;
      } else {
        this.reviveTime = 0;

        if (this.despawnTime <= 0) {
          MobDefServer.broadcastGameMessage(user.getChatNameComponent()
              .append(Component.text(" is now resting in pieces",
                  ExTextColor.WARNING)));
          ReviveManager.this.removeDyingUser(user, true);
          return;
        }

        entity.setText(List.of("§cDead in " + this.despawnTime + "s"));

      }

      this.despawnTime--;
    }
  }

}
