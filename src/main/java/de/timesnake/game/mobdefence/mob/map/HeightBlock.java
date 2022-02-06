package de.timesnake.game.mobdefence.mob.map;

import de.timesnake.basic.bukkit.util.world.ExLocation;

public class HeightBlock {

    private int level;
    private final ExLocation location;
    private HeightBlock next;

    public HeightBlock(int level, ExLocation location, HeightBlock next) {
        this.level = level;
        this.location = location.getExBlock().getLocation();
        this.next = next;
    }

    public int getLevel() {
        return level;
    }

    public ExLocation getLocation() {
        return location;
    }

    public HeightBlock getNext() {
        return next;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    protected void setLevel(int level) {
        this.level = level;
    }

    protected void setNext(HeightBlock next) {
        this.next = next;
    }
}
