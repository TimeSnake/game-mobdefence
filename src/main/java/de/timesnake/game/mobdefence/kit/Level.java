package de.timesnake.game.mobdefence.kit;

public class Level<V> {

    protected final int level;
    protected final ShopPrice price;
    protected final String description;

    protected int unlockWave = 0;

    protected final V value;

    public Level(int level, ShopPrice price, String description, V value) {
        this.level = level;
        this.price = price;
        this.description = description;
        this.value = value;
    }

    public Level(int level, int unlockWave, ShopPrice price, String description, V value) {
        this(level, price, description, value);
        this.unlockWave = unlockWave;
    }

    public int getLevel() {
        return level;
    }

    public ShopPrice getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public V getValue() {
        return value;
    }

    public int getUnlockWave() {
        return unlockWave;
    }
}
