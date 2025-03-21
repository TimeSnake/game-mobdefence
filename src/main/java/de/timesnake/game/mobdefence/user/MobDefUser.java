/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.game.mobdefence.kit.KitShops;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.TeamHealth;
import de.timesnake.library.basic.util.Status;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobDefUser extends GameUser {

  private boolean alive;
  private ItemStack[] invItems;

  private KitShops shop;

  private ReviveManager.DeadPlayer deadBody;
  private ExLocation deathLocation;
  private MobDefUser reviveUser;

  private int revives;

  public MobDefUser(Player player) {
    super(player);
  }

  @Override
  public void onGameJoin() {
    super.onGameJoin();

    this.teleport(MobDefServer.getMap().getUserSpawn());
    this.lockLocation();
    this.setBossBar(MobDefServer.getCoreHealthBar());
    this.setGameMode(GameMode.SURVIVAL);

    this.alive = true;
    this.revives = 0;

    MobDefServer.updateSideboardPlayers();

    this.setStatistic(Statistic.MOB_KILLS, 0);
    this.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(TeamHealth.getMaxHealth());
    this.heal();
  }

  public void startGame() {
    this.unlockLocation();
  }

  @Override
  public boolean applyKit() {
    boolean loaded = super.applyKit();

    if (!loaded) {
      return false;
    }

    this.loadKit(true);

    return true;
  }

  public void loadKit(boolean giveItems) {
    this.shop = ((MobDefKit) this.kit).getShop(this, giveItems);

    if (this.kit.equals(MobDefKit.ALCHEMIST)) {
      Server.runTaskSynchrony(() -> this.addPotionEffect(PotionEffectType.FIRE_RESISTANCE, 0),
          GameMobDefence.getPlugin());
      this.setAttackSpeed(4);
    } else if (this.kit.equals(MobDefKit.KNIGHT)) {
      this.setPvpMode(true);
    } else {
      this.setPvpMode(false);
    }

    if (this.kit.equals(MobDefKit.LUMBERJACK)) {
      AttributeInstance speed = this.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
      AttributeInstance damage = this.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

      speed.setBaseValue(6);
      damage.setBaseValue(2);
    }
  }

  public void saveInventory() {
    this.invItems = this.getInventory().getContents();
  }

  @Override
  public void joinSpectator() {
    this.alive = false;

    if (this.getStatus().equals(Status.User.IN_GAME) && MobDefServer.isGameRunning()) {
      if (MobDefServer.isDelayRunning()) {
        this.rejoinGame(this.getExLocation(), Status.User.IN_GAME);
        return;
      } else {
        this.deadBody = MobDefServer.getMobDefUserManager().getReviveManager().addDeadUser(this);
        this.deathLocation = this.getExLocation();
      }
    }

    super.joinSpectator();

    MobDefServer.updateSideboardPlayers();

    this.setGameMode(GameMode.CREATIVE);
  }

  public void leaveSpectatorAndRejoin() {
    ExLocation respawnLocation;

    if (this.deathLocation != null) {
      respawnLocation = this.deathLocation;
      if (this.deadBody != null) {
        this.deadBody.remove();
        this.deadBody = null;
      }
    } else {
      respawnLocation = MobDefServer.getMap().getUserSpawn();
    }

    this.leaveSpectatorAndRejoin(respawnLocation, Status.User.IN_GAME);
  }

  @Override
  public void rejoinGame(@Nullable ExLocation location, @NotNull Status.User newStatus) {
    super.rejoinGame(location, newStatus);

    this.alive = true;
    this.reviveUser = null;

    if (this.kit.equals(MobDefKit.ALCHEMIST)) {
      Server.runTaskSynchrony(() -> this.addPotionEffect(PotionEffectType.FIRE_RESISTANCE, 0),
          GameMobDefence.getPlugin());
    }

    MobDefServer.updateSideboardPlayers();

    this.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(TeamHealth.getMaxHealth());
    this.heal();

    this.setInvulnerable(true);
    Server.runTaskLaterSynchrony(() -> this.getPlayer().setInvulnerable(false), 20,
        GameMobDefence.getPlugin());
  }

  @Override
  public void onGameStop() {
    super.onGameStop();

    if (this.deadBody != null) {
      this.deadBody.remove();
    }
  }

  @Override
  public void setRejoinInventory() {
    super.setRejoinInventory();
    this.getInventory().setContents(this.invItems);
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  public KitShops getShop() {
    return shop;
  }

  public void setShop(KitShops shop) {
    this.shop = shop;
    this.shop.setUser(this);
  }

  public boolean isBeingRevived() {
    return this.reviveUser != null;
  }

  public MobDefUser getReviveUser() {
    return this.reviveUser;
  }

  public void setReviveUser(MobDefUser user) {
    this.reviveUser = user;
  }

  public ReviveManager.DeadPlayer getDeadBody() {
    return deadBody;
  }

  public void addRevive() {
    this.revives++;
  }

  public int getRevives() {
    return revives;
  }
}
