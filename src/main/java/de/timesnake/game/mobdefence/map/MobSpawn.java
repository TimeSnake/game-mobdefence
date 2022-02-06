package de.timesnake.game.mobdefence.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;

public class MobSpawn {

    private final Integer priority;
    private final ExLocation block;

    public MobSpawn(Integer priority, ExLocation block) {
        this.priority = priority;
        this.block = block;
    }

    public Integer getPriority() {
        return priority;
    }

    public ExLocation getLocation() {
        return block;
    }
}
