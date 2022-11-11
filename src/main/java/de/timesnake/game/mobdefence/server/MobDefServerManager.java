/*
 * timesnake.game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.game.mobdefence.server;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.scoreboard.Sideboard;
import de.timesnake.basic.bukkit.util.user.scoreboard.Tablist;
import de.timesnake.basic.game.util.Map;
import de.timesnake.basic.game.util.TmpGame;
import de.timesnake.basic.loungebridge.util.server.LoungeBridgeServerManager;
import de.timesnake.basic.loungebridge.util.server.TablistManager;
import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.basic.loungebridge.util.user.Kit;
import de.timesnake.basic.loungebridge.util.user.KitNotDefinedException;
import de.timesnake.basic.loungebridge.util.user.OfflineUser;
import de.timesnake.database.util.game.DbGame;
import de.timesnake.database.util.game.DbMap;
import de.timesnake.database.util.game.DbTmpGame;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.map.MobDefMap;
import de.timesnake.game.mobdefence.mob.MobManager;
import de.timesnake.game.mobdefence.special.weapon.WeaponManager;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.OfflineMobDefUser;
import de.timesnake.game.mobdefence.user.UserManager;
import de.timesnake.library.basic.util.Status;
import de.timesnake.library.basic.util.TimeCoins;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.basic.util.statistics.StatType;
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

    public static final double CORE_HEALTH_MULTIPLIER = 400; // in half hearts
    public static final float TIME_COINS_MULTIPLIER = 1.5f * TimeCoins.MULTIPLIER;
    public static final int WAVE_DELAY = 80; // in seconds

    public static MobDefServerManager getInstance() {
        return (MobDefServerManager) LoungeBridgeServerManager.getInstance();
    }

    private static final boolean DEBUG = false;
    private boolean running;
    private Integer playerAmount; // set at game start
    private double coreMaxHealth;
    private LivingEntity coreEntity;
    private BossBar coreHealthBar;
    private double coreHealth;
    private Sideboard sideboard;
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

        this.sideboard = Server.getScoreboardManager().registerSideboard("mobdef",
                "§6§l" + this.getGame().getDisplayName());
        this.sideboard.setScore(4, "§3§lWave");
        this.updateSideboardWave();
        this.sideboard.setScore(2, "§f-------------");
        this.sideboard.setScore(1, "§9§lPlayers");
        this.updateSideboardPlayers();
    }

    @Override
    public TablistManager loadTablistManager() {
        return new TablistManager() {
            @Override
            public void loadTablist(Tablist.Type type) {
                super.loadTablist(Tablist.Type.HEARTS);
            }
        };
    }

    @Override
    public GameUser loadUser(Player player) {
        return new MobDefUser(player);
    }

    @Override
    protected TmpGame loadGame(DbGame dbGame, boolean loadWorlds) {
        return new TmpGame((DbTmpGame) dbGame, loadWorlds) {
            @Override
            public Map loadMap(DbMap dbMap, boolean loadWorld) {
                return new MobDefMap(dbMap);
            }
        };
    }

    @Override
    public Plugin getGamePlugin() {
        return Plugin.MOB_DEFENCE;
    }

    @Override
    public boolean isGameRunning() {
        return this.running;
    }

    @Override
    @Deprecated
    public void broadcastGameMessage(String message) {
        Server.broadcastMessage(Plugin.MOB_DEFENCE, message);
    }

    @Override
    public void broadcastGameMessage(Component message) {
        Server.broadcastMessage(Plugin.MOB_DEFENCE, message);
    }

    @Override
    public void onMapLoad() {
        for (Entity entity : this.getMap().getWorld().getBukkitWorld().getEntities()) {
            if (entity instanceof LivingEntity || entity instanceof Item) {
                entity.remove();
            }
        }
        Server.printText(Plugin.MOB_DEFENCE, "Cleared living entities");

        this.coreEntity = this.mobManager.createCoreEntity();
        EntityManager.spawnEntity(this.getMap().getWorld().getBukkitWorld(), this.coreEntity);
        Server.printText(Plugin.MOB_DEFENCE, "Spawned core entity");

        ((MobDefMap) this.getMap()).getHeightMapManager().resetMaps();
        ((MobDefMap) this.getMap()).getHeightMapManager().updateMaps();

        for (ItemFrame frame : this.getMap().getWorld().getEntitiesByClass(ItemFrame.class)) {
            frame.setFixed(true);
        }
    }

    @Override
    public void onGamePrepare() {
        this.waveNumber = 0;
        this.userManager.runTasks();
        this.getMap().getWorld().setDifficulty(Difficulty.EASY);
        this.getMap().getWorld().setGameRule(GameRule.NATURAL_REGENERATION, true);
        this.updateSideboardWave();
    }

    @Override
    public void onGameStart() {
        this.running = true;
        this.playerAmount = Server.getInGameUsers().size();
        for (User user : Server.getInGameUsers()) {
            ((MobDefUser) user).startGame();
        }
        this.coreHealth = this.coreMaxHealth = CORE_HEALTH_MULTIPLIER;
        this.updateCoreHealthBar();

        if (this.playerAmount == 1) {
            Server.getInGameUsers().iterator().next().addItem(new ShopPrice(32, ShopCurrency.BRONZE).asItem(),
                    new ShopPrice(16, ShopCurrency.SILVER).asItem(), new ShopPrice(8, ShopCurrency.GOLD).asItem());
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
            this.getMobDefUserManager().getReviveManager().removeDyingUser(((MobDefUser) user), true);
        }
    }

    @Override
    public void onGameUserQuitBeforeStart(GameUser user) {

    }

    @Override
    public boolean isRejoiningAllowed() {
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
                ((MobDefUser) user).rejoinGame();
            }
            if (this.waveNumber > 0) {
                user.addCoins(this.waveNumber * TIME_COINS_MULTIPLIER, true);
                user.addItem(new ShopPrice((int) (8 * Math.sqrt(this.waveNumber)), ShopCurrency.BRONZE).asItem(),
                        new ShopPrice((int) (4 * Math.sqrt(this.waveNumber)), ShopCurrency.SILVER).asItem(),
                        new ShopPrice((int) (2 * Math.sqrt(this.waveNumber)), ShopCurrency.GOLD).asItem());
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
            this.broadcastGameMessage(Component.text("No more natural regeneration", ExTextColor.WARNING));
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
    public org.bukkit.Location getSpectatorSpawn() {
        return ((MobDefMap) this.getMap()).getUserSpawn();
    }

    @Override
    public Kit getKit(int index) throws KitNotDefinedException {
        for (MobDefKit kit : MobDefKit.KITS) {
            if (kit.getId().equals(index)) {
                return kit;
            }
        }
        throw new KitNotDefinedException(index);
    }

    @Override
    public Kit[] getKits() {
        return MobDefKit.KITS;
    }

    @EventHandler
    public void onEntityByEntityDamage(EntityDamageByEntityEvent e) {
        if (this.coreEntity != null && e.getEntity().getUniqueId().equals(this.coreEntity.getUniqueId())) {
            if (e.getDamager() instanceof Player || (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player)) {
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
        this.coreHealthBar.setTitle("§c§lHealth: §c" + ((int) this.coreHealth) + "/" + ((int) this.coreMaxHealth));
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
        this.sideboard.setScore(0, this.getAliveUsers().size() + " §7alive");
    }

    public void updateSideboardWave() {
        this.sideboard.setScore(3, String.valueOf(this.waveNumber));
    }

    public Integer getPlayerAmount() {
        return this.playerAmount;
    }

    public BossBar getCoreHealthBar() {
        return coreHealthBar;
    }

    public LivingEntity getCoreEntity() {
        return coreEntity;
    }

    public Integer getWaveNumber() {
        return waveNumber;
    }

    public Sideboard getSideboard() {
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
