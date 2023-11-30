/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.server;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.scoreboard.ExSideboard;
import de.timesnake.basic.bukkit.util.user.scoreboard.ExSideboard.LineId;
import de.timesnake.basic.bukkit.util.user.scoreboard.ExSideboardBuilder;
import de.timesnake.basic.bukkit.util.user.scoreboard.Sideboard;
import de.timesnake.basic.bukkit.util.user.scoreboard.Tablist;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.game.util.game.Map;
import de.timesnake.basic.game.util.user.SpectatorManager;
import de.timesnake.basic.loungebridge.util.game.TmpGame;
import de.timesnake.basic.loungebridge.util.server.LoungeBridgeServerManager;
import de.timesnake.basic.loungebridge.util.server.TablistManager;
import de.timesnake.basic.loungebridge.util.tool.listener.GameUserDeathListener;
import de.timesnake.basic.loungebridge.util.tool.listener.GameUserRespawnListener;
import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.basic.loungebridge.util.user.KitManager;
import de.timesnake.basic.loungebridge.util.user.OfflineUser;
import de.timesnake.database.util.game.DbGame;
import de.timesnake.database.util.game.DbMap;
import de.timesnake.database.util.game.DbTmpGame;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.kit.KitShopManager;
import de.timesnake.game.mobdefence.kit.MobDefKitManager;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.map.MobDefMap;
import de.timesnake.game.mobdefence.mob.MobManager;
import de.timesnake.game.mobdefence.shop.BaseShops;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.special.weapon.WeaponManager;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.OfflineMobDefUser;
import de.timesnake.game.mobdefence.user.UserManager;
import de.timesnake.library.basic.util.Loggers;
import de.timesnake.library.basic.util.Status;
import de.timesnake.library.basic.util.TimeCoins;
import de.timesnake.library.basic.util.statistics.StatType;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.EntityManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class MobDefServerManager extends LoungeBridgeServerManager<TmpGame> implements Listener {

  public static final LineId<Integer> WAVE_LINE = LineId.of("wave", "§c§lWave", false,
      Object::toString);

  public static final double CORE_HEALTH_MULTIPLIER = 400; // in half hearts
  public static final float TIME_COINS_MULTIPLIER = 1.5f * TimeCoins.MULTIPLIER;
  public static final int WAVE_DELAY = 80; // in seconds

  public static MobDefServerManager getInstance() {
    return (MobDefServerManager) LoungeBridgeServerManager.getInstance();
  }

  private static final boolean DEBUG = false;
  private Integer playerAmount; // set at game start
  private double coreMaxHealth;
  private net.minecraft.world.entity.LivingEntity coreEntity;
  private BossBar coreHealthBar;
  private double coreHealth;
  private ExSideboard sideboard;
  private Integer delay;
  private boolean delayIsRunning = false;
  private BukkitTask delayTask;
  private Integer waveNumber = 0;
  private MobManager mobManager;
  private KitShopManager kitShopManager;
  private UserManager userManager;
  private WeaponManager weaponManager;
  private BaseShops baseShops;

  public void onMobGameEnable() {
    super.onLoungeBridgeEnable();
    Server.registerListener(this, GameMobDefence.getPlugin());

    this.mobManager = new MobManager();
    this.kitShopManager = new KitShopManager();
    this.userManager = new UserManager();
    this.weaponManager = new WeaponManager();
    this.baseShops = new BaseShops();

    this.coreHealthBar = Server.createBossBar("§c§lHealth", BarColor.RED, BarStyle.SOLID);
    this.coreHealthBar.setProgress(1);

    this.sideboard = Server.getScoreboardManager().registerExSideboard(new ExSideboardBuilder()
        .name("mobdef")
        .title("§6§l" + this.getGame().getDisplayName())
        .lineSpacer()
        .addLine(WAVE_LINE)
        .addLine(LineId.PLAYERS));
    this.updateSideboardWave();
  }

  @Override
  public void loadTools() {
    super.loadTools();

    this.getToolManager().add((GameUserDeathListener) (e, user) -> {
      e.setAutoRespawn(true);
      if (user.getStatus().equals(Status.User.IN_GAME)) {
        ((MobDefUser) user).saveInventory();
      }
    });

    this.getToolManager().add((GameUserRespawnListener) user -> {
      if (MobDefServer.getAliveUsers().size() <= 1) {
        MobDefServer.stopGame();
      }
      if (user.getStatus().equals(Status.User.IN_GAME)) {
        user.joinSpectator();
      }
      return MobDefServer.getMap().getUserSpawn();
    });
  }

  @Override
  public TablistManager initTablistManager() {
    return new TablistManager() {
      @Override
      public void loadTablist(Tablist.Type type) {
        super.loadTablist(Tablist.Type.HEALTH);
      }
    };
  }

  @Override
  public GameUser loadUser(Player player) {
    return new MobDefUser(player);
  }

  @Override
  public Sideboard getGameSideboard() {
    return this.sideboard;
  }

  @Override
  protected TmpGame loadGame(DbGame dbGame, boolean loadWorlds) {
    return new TmpGame((DbTmpGame) dbGame, loadWorlds) {
      @Override
      public Map loadMap(DbMap dbMap, boolean loadWorld) {
        return new MobDefMap(dbMap);
      }

      @Override
      public KitManager<?> loadKitManager() {
        return new MobDefKitManager();
      }
    };
  }

  @Override
  protected SpectatorManager initSpectatorManager() {
    return new de.timesnake.basic.loungebridge.core.main.SpectatorManager() {
      @Override
      public GameMode getReJoinGameMode() {
        return GameMode.SURVIVAL;
      }
    };
  }

  @Override
  public Plugin getGamePlugin() {
    return Plugin.MOB_DEFENCE;
  }

  @Override
  public void onMapLoad() {
    for (Entity entity : this.getMap().getWorld().getBukkitWorld().getEntities()) {
      if (entity instanceof LivingEntity || entity instanceof Item) {
        entity.remove();
      }
    }
    Loggers.GAME.info("Cleared living entities");

    this.coreEntity = this.mobManager.createCoreEntity();
    EntityManager.spawnEntity(this.getMap().getWorld().getBukkitWorld(), this.coreEntity);
    Loggers.GAME.info("Spawned core entity");

    ((MobDefMap) this.getMap()).getHeightMapManager().resetMaps();
    ((MobDefMap) this.getMap()).getHeightMapManager().updateMaps();

    for (ItemFrame frame : this.getMap().getWorld().getEntitiesByClass(ItemFrame.class)) {
      frame.setFixed(true);
    }

    this.waveNumber = 0;
    this.updateSideboardWave();

    this.getMap().getWorld().setDifficulty(Difficulty.EASY);
    this.getMap().getWorld().setGameRule(GameRule.NATURAL_REGENERATION, true);
  }

  @Override
  public void onGameStart() {
    this.running = true;

    this.userManager.runTasks();

    this.playerAmount = Server.getInGameUsers().size();
    for (User user : Server.getInGameUsers()) {
      ((MobDefUser) user).startGame();
    }
    this.coreHealth = this.coreMaxHealth = CORE_HEALTH_MULTIPLIER;
    this.updateCoreHealthBar();

    if (this.playerAmount == 1) {
      Server.getInGameUsers().iterator().next()
          .addItem(new Price(32, Currency.BRONZE).asItem(),
              new Price(16, Currency.SILVER).asItem(),
              new Price(8, Currency.GOLD).asItem());
    }

    // start wave
    this.initNextWave();
  }

  @Override
  public void onGameUserQuit(GameUser user) {
    if (this.getAliveUsers().size() == 0) {
      this.stopGame();
    }

    if (!((MobDefUser) user).isAlive() && ((MobDefUser) user).getDeadBody() != null) {
      this.getMobDefUserManager().getReviveManager()
          .removeDyingUser(((MobDefUser) user), true);
    }
  }

  @Override
  public boolean isRejoiningAllowed() {
    return true;
  }

  @Override
  public boolean isOutGameRejoiningAllowed() {
    return true;
  }

  @Override
  public void onGameUserRejoin(GameUser user) {
    if (user.getStatus().equals(Status.User.OUT_GAME)) {
      user.joinSpectator();
    } else {
      user.joinGame();
      ((MobDefUser) user).startGame();
    }
  }

  @Override
  public OfflineUser loadOfflineUser(GameUser user) {
    return new OfflineMobDefUser(((MobDefUser) user));
  }

  public void initNextWave() {
    if (!this.isGameRunning()) {
      return;
    }

    if (this.delayIsRunning) {
      return;
    }

    this.delayIsRunning = true;

    this.userManager.getReviveManager().clear();

    for (User user : Server.getInOutGameUsers()) {
      if (!((MobDefUser) user).isAlive()) {
        ((MobDefUser) user).leaveSpectatorAndRejoin();
      }
      if (this.waveNumber > 0) {
        user.addCoins(this.waveNumber * TIME_COINS_MULTIPLIER, true);
        user.addItem(
            new Price((int) (8 * Math.sqrt(this.waveNumber)), Currency.BRONZE).asItem(),
            new Price((int) (4 * Math.sqrt(this.waveNumber)), Currency.SILVER).asItem(),
            new Price((int) (2 * Math.sqrt(this.waveNumber)), Currency.GOLD).asItem());
      }
    }

    this.updateSideboardPlayers();

    this.delay = this.waveNumber == 0 ? 20 : WAVE_DELAY;

    if (DEBUG) {
      this.delay = 10;
    }

    if (this.delayTask != null) {
      this.delayTask.cancel();
    }

    this.delayTask = Server.runTaskTimerSynchrony(() -> {

      this.updateSideboardDelay();
      if (delay <= 0) {
        this.delayIsRunning = false;
        this.broadcastGameMessage(Component.text("Next wave spawns ", ExTextColor.PUBLIC)
            .append(Component.text("now", ExTextColor.VALUE)));
        this.startNextWave();
        delayTask.cancel();
        return;
      }

      if (this.delay <= 5) {
        this.broadcastGameMessage(Component.text("Next wave spawns in ", ExTextColor.PUBLIC)
            .append(Component.text(this.delay + "s", ExTextColor.VALUE)));
      }
      delay--;
    }, 0, 20, GameMobDefence.getPlugin());
  }

  public void startNextWave() {
    this.waveNumber++;
    this.updateSideboardWave();

    if (this.waveNumber == 3) {
      this.getMap().getWorld().setDifficulty(Difficulty.NORMAL);
    } else if (this.waveNumber == 6) {
      this.getMap().getWorld().setDifficulty(Difficulty.HARD);
    }

    if (this.waveNumber == 13) {
      this.getMap().getWorld().setGameRule(GameRule.NATURAL_REGENERATION, false);
      this.broadcastGameMessage(
          Component.text("No more natural regeneration", ExTextColor.WARNING));
    }

    Server.broadcastSound(Sound.ITEM_GOAT_HORN_SOUND_2, 2);

    this.mobManager.spawnWave();
  }

  @Override
  public void onGameStop() {
    if (!this.running) {
      return;
    }

    if (this.delayTask != null) {
      this.delayTask.cancel();
    }

    this.running = false;

    this.mobManager.cancelSpawning();

    this.userManager.cancelTasks();

    for (LivingEntity entity : this.getMap().getWorld().getBukkitWorld().getLivingEntities()) {
      if (!(entity instanceof Player)) {
        entity.remove();
      }
    }

    super.closeGame();
  }

  @Override
  public void onGameReset() {
    this.coreHealthBar.setProgress(1);
    this.baseShops.resetShops();
  }

  @Override
  public ExLocation getSpectatorSpawn() {
    return ((MobDefMap) this.getMap()).getUserSpawn();
  }

  @EventHandler
  public void onEntityByEntityDamage(EntityDamageByEntityEvent e) {
    if (this.coreEntity != null && e.getEntity().getUniqueId().equals(this.coreEntity.getBukkitEntity().getUniqueId())) {
      if (e.getDamager() instanceof Player || (e.getDamager() instanceof Projectile
          && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
        e.setDamage(0);
        e.setCancelled(true);
        return;
      }

      this.removeCoreHealth(e.getDamage());
      Server.broadcastNote(Instrument.PLING, Note.natural(0, Note.Tone.C));
      e.setDamage(0);
    }
  }

  public void setCoreHealth(double health) {
    this.coreHealth = health <= 0 ? 0 : health;
    this.updateCoreHealthBar();

    if (this.coreHealth == 0) {
      this.stopGame();
    }
  }

  public void removeCoreHealth(double health) {
    this.setCoreHealth(this.coreHealth - health);
    this.updateCoreHealthBar();
  }

  public void updateCoreHealthBar() {
    this.coreHealthBar.setProgress(this.coreHealth / coreMaxHealth);
    this.coreHealthBar.setTitle(
        "§c§lHealth: §c" + ((int) this.coreHealth) + "/" + ((int) this.coreMaxHealth));
  }

  public void updateSideboardDelay() {
    if (this.delay <= 0) {
      this.sideboard.removeScore(7);
      this.sideboard.removeScore(6);
      this.sideboard.removeScore(5);
    } else {
      this.sideboard.setScore(7, "§c§lNext Wave");
      this.sideboard.setScore(6, "§7in §f" + this.delay + " s");
      this.sideboard.setScore(5, "§r§f-------------");
    }
  }

  public void updateSideboardPlayers() {
    this.sideboard.updateScore(LineId.PLAYERS, this.getAliveUsers().size());
  }

  public void updateSideboardWave() {
    this.sideboard.updateScore(WAVE_LINE, this.waveNumber);
  }

  public Integer getPlayerAmount() {
    return this.playerAmount;
  }

  public BossBar getCoreHealthBar() {
    return coreHealthBar;
  }

  public net.minecraft.world.entity.LivingEntity getCoreEntity() {
    return coreEntity;
  }

  public Integer getWaveNumber() {
    return waveNumber;
  }

  public ExSideboard getSideboard() {
    return sideboard;
  }

  public MobManager getMobManager() {
    return mobManager;
  }

  public UserManager getMobDefUserManager() {
    return userManager;
  }

  public WeaponManager getWeaponManager() {
    return weaponManager;
  }

  public boolean isDelayRunning() {
    return delayIsRunning;
  }

  public Collection<MobDefUser> getAliveUsers() {
    Collection<MobDefUser> users = new ArrayList<>();
    for (User user :
        Server.getUsers((user) -> user.getStatus().equals(Status.User.IN_GAME)
            || user.getStatus().equals(Status.User.PRE_GAME))) {

      if (((MobDefUser) user).isAlive()) {
        users.add(((MobDefUser) user));
      }
    }
    return users;
  }

  public BaseShops getBaseShops() {
    return baseShops;
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent e) {
    ((MobDefMap) this.getMap()).getHeightMapManager().updateMaps();
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent e) {
    ((MobDefMap) this.getMap()).getHeightMapManager().updateMaps();
  }

  @Override
  public void saveGameUserStats(GameUser user) {
    super.saveGameUserStats(user);

    user.getStat(MobDefServer.MOB_KILLS).increaseAll(user.getStatistic(Statistic.MOB_KILLS));
  }

  @Override
  public Set<StatType<?>> getStats() {
    return Set.of(MobDefServer.MOB_KILLS);
  }
}
