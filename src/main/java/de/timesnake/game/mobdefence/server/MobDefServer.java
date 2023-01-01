/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.server;

import de.timesnake.basic.bukkit.util.user.scoreboard.Sideboard;
import de.timesnake.basic.loungebridge.util.server.LoungeBridgeServer;
import de.timesnake.game.mobdefence.map.MobDefMap;
import de.timesnake.game.mobdefence.mob.MobManager;
import de.timesnake.game.mobdefence.shop.BaseShops;
import de.timesnake.game.mobdefence.special.weapon.WeaponManager;
import de.timesnake.game.mobdefence.user.MobDefUser;
import de.timesnake.game.mobdefence.user.UserManager;
import de.timesnake.library.basic.util.statistics.IntegerStat;
import de.timesnake.library.basic.util.statistics.StatType;
import java.util.Collection;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;

public class MobDefServer extends LoungeBridgeServer {

    public static final StatType<Integer> MOB_KILLS = new IntegerStat("mob_kill", "Mob Kills",
            0, 10, 2, true, 0, 2);

    public static MobDefMap getMap() {
        return LoungeBridgeServer.getMap();
    }

    public static Integer getPlayerAmount() {
        return server.getPlayerAmount();
    }

    public static void setCoreHealth(double health) {
        server.setCoreHealth(health);
    }

    public static void removeCoreHealth(double health) {
        server.removeCoreHealth(health);
    }

    public static BossBar getCoreHealthBar() {
        return server.getCoreHealthBar();
    }

    public static LivingEntity getCoreEntity() {
        return server.getCoreEntity();
    }

    public static Integer getWaveNumber() {
        return server.getWaveNumber();
    }

    public static Sideboard getSideboard() {
        return server.getSideboard();
    }

    public static void updateSideboardPlayers() {
        server.updateSideboardPlayers();
    }

    public static void initNextWave() {
        server.initNextWave();
    }

    public static Collection<MobDefUser> getAliveUsers() {
        return server.getAliveUsers();
    }

    public static void stopGame() {
        server.stopGame();
    }

    public static MobManager getMobManager() {
        return server.getMobManager();
    }

    public static UserManager getMobDefUserManager() {
        return server.getMobDefUserManager();
    }

    public static WeaponManager getWeaponManager() {
        return server.getWeaponManager();
    }

    public static BaseShops getBaseShops() {
        return server.getBaseShops();
    }

    public static boolean isDelayRunning() {
        return server.isDelayRunning();
    }

    private static final MobDefServerManager server = MobDefServerManager.getInstance();
}
