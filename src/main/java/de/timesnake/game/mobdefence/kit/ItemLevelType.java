package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.user.MobDefUser;

import java.util.List;

public class ItemLevelType<L extends ItemLevel<?>> extends LevelType<L> {

    private ExItemStack item;

    public ItemLevelType(String name, ExItemStack displayItem, int baseLevel, int maxLevel, List<L> levels) {
        super(name, displayItem, baseLevel, maxLevel, levels);
    }

    public ItemLevelType(ItemLevelType<L> levelType, ExItemStack item) {
        super(levelType);
        this.item = item;
    }

    public String getBaseLevelLore(Number value) {
        return ((ItemLevel.LoreNumberLevel) this.getFirstLevel()).getLoreText(value);
    }

    public String getValueFromLore(List<String> lore) {
        return ((ItemLevel.LoreNumberLevel<?>) this.getFirstLevel()).getValueFromLore(lore);
    }

    @Override
    protected boolean levelUp(MobDefUser user, L level) {

        ExItemStack leveledItem = level.levelUp(this.item);

        if (leveledItem == null) {
            return false;
        }

        user.replaceExItemStack(this.item, leveledItem);

        this.item = leveledItem;

        return false;
    }

    public ExItemStack getItem() {
        return item;
    }

    public ItemLevelType<L> clone(ExItemStack item) {
        return new ItemLevelType<>(this, item);
    }
}
