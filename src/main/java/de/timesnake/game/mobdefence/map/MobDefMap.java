package de.timesnake.game.mobdefence.map;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.game.util.Map;
import de.timesnake.database.util.game.DbMap;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.mob.map.HeightBlock;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import org.bukkit.GameRule;

import java.util.HashMap;

public class MobDefMap extends Map {

    private static final Integer DEFAULT_MAP_RADIUS = 100;

    private static final Integer CORE_LOCATION_INDEX = 0;
    private static final Integer USER_SPAWN_INDEX = 1;

    public static final Integer MOB_SPAWN_START_INDEX = 10;
    public static final Integer STAGE_LOC_SIZE = 100;

    public static final Integer HEIGHT_MAP_UPDATE_DELAY = 20 * 8;

    private MobDefStage current;

    private final java.util.Map<Integer, MobDefStage> stages = new HashMap<>();

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
        this.getWorld().allowFireSpread(false);
        this.getWorld().allowEntityExplode(true);
        this.getWorld().setAutoSave(false);
        this.getWorld().setExceptService(true);
        this.getWorld().allowEntityBlockBreak(false);
        this.getWorld().allowBlockBurnUp(false);

        for (int stageNumber = 0; stageNumber <= map.getLastLocationNumber(); stageNumber += STAGE_LOC_SIZE) {
            Server.printText(Plugin.MOB_DEFENCE, "Loading stage " + stageNumber + " ...");
            MobDefStage stage = new MobDefStage(stageNumber, this.getCoreLocation(), super.getLocationsById());
            Server.printText(Plugin.MOB_DEFENCE, "Loaded stage" + stageNumber);
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

    public HeightBlock getHeightBlockByLocation(HeightMapManager.MapType type, ExLocation location) {
        return this.current.getHeightMapManager().getMap(type).getHeightBlock(location);
    }

    public HeightMapManager getHeightMapManager() {
        return this.current.getHeightMapManager();
    }
}