/*
 * workspace.game-mobdefence.main
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

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.server.TimeTask;
import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.basic.util.Tuple;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.entities.entity.bukkit.ExArmorStand;
import de.timesnake.library.entities.entity.bukkit.ExPlayer;
import de.timesnake.library.entities.wrapper.ExEntityPose;
import de.timesnake.library.packets.util.packet.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ReviveManager {

    private static List<Level<Integer>> getTimeLevels(int start, String text, List<ShopPrice> prices,
                                                      List<Integer> respawnTimes) {
        List<Level<Integer>> levels = new ArrayList<>();

        Iterator<ShopPrice> priceIt = prices.listIterator();
        Iterator<Integer> timeIt = respawnTimes.listIterator();

        for (int level = start; priceIt.hasNext() && timeIt.hasNext(); level++) {
            levels.add(new Level<>(level, priceIt.next(), text, timeIt.next()));
        }
        return levels;
    }

    private static final double RADIUS = 3;
    private static final LevelType<Level<Integer>> RESPAWN_TIME_LEVELS = new LevelType<>("Respawn Time",
            new ExItemStack(Material.PLAYER_HEAD), 1, 5, getTimeLevels(2, "-1s Revive Time", List.of(new ShopPrice(3,
                    ShopCurrency.EMERALD), new ShopPrice(5, ShopCurrency.EMERALD), new ShopPrice(7,
                    ShopCurrency.EMERALD),
            new ShopPrice(9, ShopCurrency.EMERALD)), List.of(6, 5, 4, 3))) {
        @Override
        protected boolean levelUp(MobDefUser user, Level<Integer> level) {
            MobDefServer.getMobDefUserManager().getReviveManager().setReviveRespawnTime(level.getValue());
            return true;
        }
    };
    private static final LevelType<Level<Integer>> DESPAWN_TIME_LEVELS = new LevelType<>("Despawn Time",
            new ExItemStack(Material.SKELETON_SKULL), 1, 11, getTimeLevels(2, "+3s Despawn Time",
            List.of(new ShopPrice(2, ShopCurrency.EMERALD), new ShopPrice(4, ShopCurrency.EMERALD), new ShopPrice(6,
                    ShopCurrency.EMERALD), new ShopPrice(8, ShopCurrency.EMERALD), new ShopPrice(10,
                    ShopCurrency.EMERALD), new ShopPrice(12, ShopCurrency.EMERALD), new ShopPrice(14,
                    ShopCurrency.EMERALD), new ShopPrice(16, ShopCurrency.EMERALD), new ShopPrice(18,
                    ShopCurrency.EMERALD), new ShopPrice(20, ShopCurrency.EMERALD), new ShopPrice(22,
                    ShopCurrency.EMERALD)), List.of(33, 36, 39, 42, 45, 48, 51, 54, 57, 60))) {
        @Override
        protected boolean levelUp(MobDefUser user, Level<Integer> level) {
            MobDefServer.getMobDefUserManager().getReviveManager().setReviveDespawnTime(level.getValue());
            return true;
        }
    };
    public static final Revive REVIVE = new Revive("Revive", new ExItemStack(Material.TOTEM_OF_UNDYING),
            List.of(RESPAWN_TIME_LEVELS, DESPAWN_TIME_LEVELS));
    private final HashMap<MobDefUser, Location> deathLocationsByUser = new HashMap<>();
    private final Map<MobDefUser, BukkitTask> deadTasksByUser = new HashMap<>();
    private final Map<MobDefUser, ExArmorStand> displayEntitiesByUser = new HashMap<>();
    private int reviveRespawnTime = 7;
    private int reviveDespawnTime = 30;
    private BukkitTask revivingCheckTask;

    public ExPlayer addDeadUser(MobDefUser user, Location location) {
        this.deathLocationsByUser.put(user, location);

        ExPlayer deadBody = new ExPlayer(user.getExWorld().getBukkitWorld(), user.getName());

        Tuple<String, String> textures = user.asExPlayer().getTextureValueSiganture();

        deadBody.setTextures(textures.getA(), textures.getB());

        Location loc = user.getLocation();
        deadBody.setPositionRotation(loc.getX(), loc.getY() + 0.2, loc.getZ(), 120, 0);
        deadBody.setNoGravity(true);
        deadBody.setCustomName(user.getName());
        deadBody.setCustomNameVisible(true);

        deadBody.setPose(ExEntityPose.SLEEPING);

        Server.broadcastPacket(ExPacketPlayOutPlayerInfo.wrap(ExPacketPlayOutPlayerInfo.Action.ADD_PLAYER, deadBody));
        Server.broadcastPacket(ExPacketPlayOutSpawnNamedEntity.wrap(deadBody));
        Server.broadcastPacket(ExPacketPlayOutEntityMetadata.wrap((Player) deadBody,
                ExPacketPlayOutEntityMetadata.DataType.UPDATE));

        Server.runTaskLaterSynchrony(() -> Server.broadcastPacket(ExPacketPlayOutPlayerInfo.wrap(ExPacketPlayOutPlayerInfo.Action.REMOVE_PLAYER, deadBody)), 3, GameMobDefence.getPlugin());

        this.startDying(user, deadBody);

        return deadBody;
    }

    private void startDying(MobDefUser user, ExPlayer deadBody) {

        ExArmorStand stand = new ExArmorStand(user.getExWorld().getBukkitWorld());

        stand.setCustomName("§cDead in 30s");
        stand.setCustomNameVisible(true);
        stand.setSmall(true);
        stand.setInvulnerable(false);
        stand.setInvisible(true);
        stand.setNoGravity(true);

        Location loc = deadBody.getLocation();
        stand.setPosition(loc.getX(), loc.getY() - 1.1, loc.getZ());

        this.displayEntitiesByUser.put(user, stand);

        Server.broadcastPacket(ExPacketPlayOutSpawnEntity.wrap(stand));
        Server.broadcastPacket(ExPacketPlayOutEntityMetadata.wrap(stand,
                ExPacketPlayOutEntityMetadata.DataType.UPDATE));

        this.deadTasksByUser.put(user, Server.runTaskTimerAsynchrony(new DyingProcess(user), this.reviveDespawnTime,
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

        ExPlayer deadBody = deadUser.getDeadBody();

        if (deadBody == null) {
            return;
        }

        deadUser.setBeingRevivedUser(user);

        user.sendPluginMessage(Plugin.MOB_DEFENCE, Component.text("Reviving ", ExTextColor.WARNING)
                .append(deadUser.getChatNameComponent()));
    }

    public void removeDyingUser(MobDefUser user, boolean removeFromList) {
        if (removeFromList) {
            ReviveManager.this.deadTasksByUser.remove(user).cancel();
        } else {
            ReviveManager.this.deadTasksByUser.get(user).cancel();
        }

        Server.broadcastPacket(ExPacketPlayOutPlayerInfo.wrap(ExPacketPlayOutPlayerInfo.Action.REMOVE_PLAYER,
                user.getDeadBody()));
        Server.broadcastPacket(ExPacketPlayOutEntityDestroy.wrap(user.getDeadBody()));
        Server.broadcastPacket(ExPacketPlayOutEntityDestroy.wrap(this.displayEntitiesByUser.remove(user)));

        Server.runTaskSynchrony(() -> {
            if (user.getDeadBody() != null) user.getDeadBody().kill();
        }, GameMobDefence.getPlugin());

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

    private static class Revive extends Levelable<LevelType<Level<Integer>>> {

        protected Revive(String name, ExItemStack displayItem, List<LevelType<Level<Integer>>> levelTypes) {
            super(name, displayItem, levelTypes);
        }

        protected Revive(Revive revive) {
            super(revive);
        }

        @Override
        public LevelType<Level<Integer>> cloneLevelType(LevelType<Level<Integer>> levelType) {
            return levelType;
        }

        @Override
        public Levelable<LevelType<Level<Integer>>> clone() {
            return new Revive(this);
        }

        @Override
        public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
            LevelType<?> levelType = this.levelTypeByItemId.get(item.getId());

            if (levelType == null) {
                return;
            }

            levelType.tryLevelUp(user);

            inv.setItemStack(levelType.getDisplayItem());
        }
    }

    private class DyingProcess implements TimeTask {

        private final MobDefUser user;
        private int reviveTime;

        DyingProcess(MobDefUser user) {
            this.user = user;
            this.reviveTime = 0;
        }

        @Override
        public void run(int time) {

            ExArmorStand entity = ReviveManager.this.displayEntitiesByUser.get(user);

            if (user.isBeingRevived()) {
                if (reviveTime == ReviveManager.this.reviveRespawnTime) {
                    MobDefServer.broadcastGameMessage(user.getChatNameComponent()
                            .append(Component.text(" was revived by ", ExTextColor.WARNING))
                            .append(user.getBeingRevivedUser().getChatNameComponent()));
                    ReviveManager.this.removeDyingUser(user, true);
                    Server.runTaskSynchrony(user::rejoinGame, GameMobDefence.getPlugin());
                    return;
                }

                user.getBeingRevivedUser().playSound(Sound.ENTITY_PLAYER_LEVELUP, 2);

                entity.setCustomName("§2Revived in " + (reviveRespawnTime - reviveTime) + "s");

                reviveTime++;
            } else {
                this.reviveTime = 0;

                if (time <= 0) {
                    MobDefServer.broadcastGameMessage(user.getChatNameComponent()
                            .append(Component.text(" is now resting in pieces", ExTextColor.WARNING)));
                    ReviveManager.this.removeDyingUser(user, true);
                    return;
                }

                entity.setCustomName("§cDead in " + time + "s");

            }

            Server.broadcastPacket(ExPacketPlayOutEntityMetadata.wrap(entity,
                    ExPacketPlayOutEntityMetadata.DataType.UPDATE));

        }
    }

}
