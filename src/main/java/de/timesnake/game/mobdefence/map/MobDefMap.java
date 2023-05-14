/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.basic.bukkit.util.world.ExWorld.Restriction;
import de.timesnake.basic.game.util.game.Map;
import de.timesnake.basic.loungebridge.util.game.ResetableMap;
import de.timesnake.database.util.game.DbMap;
import de.timesnake.game.mobdefence.mob.map.HeightBlock;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.library.basic.util.Loggers;
import java.util.HashMap;
import java.util.List;
import org.bukkit.GameRule;
import org.bukkit.Material;

public class MobDefMap extends Map implements ResetableMap {

    public static final Integer MOB_SPAWN_START_INDEX = 10;
    public static final Integer STAGE_LOC_SIZE = 100;
    public static final Integer HEIGHT_MAP_UPDATE_DELAY = 20 * 8;
    private static final Integer DEFAULT_MAP_RADIUS = 100;
    private static final Integer CORE_LOCATION_INDEX = 0;
    private static final Integer USER_SPAWN_INDEX = 1;
    private final java.util.Map<Integer, MobDefStage> stages = new HashMap<>();
    private MobDefStage current;

    public MobDefMap(DbMap map) {
        super(map, true);

        this.getWorld().setTime(18000);
        this.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        this.getWorld().setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        this.getWorld().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        this.getWorld().setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        this.getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
        this.getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        this.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.getWorld().restrict(ExWorld.Restriction.FIRE_SPREAD_SPEED, 0f);
        this.getWorld().restrict(ExWorld.Restriction.ENTITY_EXPLODE, false);
        this.getWorld().restrict(Restriction.CRAFTING, true);
        this.getWorld().setAutoSave(false);
        this.getWorld().setExceptService(true);
        this.getWorld().restrict(ExWorld.Restriction.ENTITY_BLOCK_BREAK, true);
        this.getWorld().restrict(ExWorld.Restriction.BLOCK_BURN_UP, true);
        this.getWorld().restrict(Restriction.OPEN_INVENTORIES, List.of(Material.AIR));
        this.getWorld().restrict(Restriction.CRAFTING, true);

        for (int stageNumber = 0; stageNumber <= map.getLastLocationNumber();
                stageNumber += STAGE_LOC_SIZE) {
            Loggers.GAME.info("Loading stage " + stageNumber + " ...");
            MobDefStage stage = new MobDefStage(stageNumber, this.getCoreLocation(),
                    super.getLocationsById());
            Loggers.GAME.info("Loaded stage" + stageNumber);
            this.stages.put(stageNumber, stage);
        }
        this.current = this.stages.get(0);
    }

    public boolean nextStage() {
        MobDefStage next = this.stages.get(this.current.getNumber() + 1);
        if (next == null) {
            return false;
        }
        this.current = next;
        return true;
    }

    public ExLocation getCoreLocation() {
        return super.getLocation(CORE_LOCATION_INDEX);
    }

    public ExLocation getUserSpawn() {
        return super.getLocation(USER_SPAWN_INDEX);
    }

    public Integer getMapRadius() {
        return DEFAULT_MAP_RADIUS;
    }

    public MobSpawn getRandomMobPath() {
        return this.current.getRandomMobPath();
    }

    public void startHeightMapUpdater() {
        this.current.getHeightMapManager().startHeightMapUpdater();
    }

    public void stopHeightMapUpdater() {
        this.current.getHeightMapManager().stopHeightMapUpdater();
    }

    public HeightBlock getHeightBlockByLocation(HeightMapManager.MapType type,
            ExLocation location) {
        return this.current.getHeightMapManager().getMap(type).getHeightBlock(location);
    }

    public HeightMapManager getHeightMapManager() {
        return this.current.getHeightMapManager();
    }
}