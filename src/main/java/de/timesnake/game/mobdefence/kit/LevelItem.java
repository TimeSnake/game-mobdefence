package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExInventory;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Instrument;
import org.bukkit.Note;

import java.util.List;

public class LevelItem extends Levelable<ItemLevelType<?>> {

    protected ExItemStack item;

    private ShopPrice buyPrice;
    private boolean oneTimeBuy;

    private boolean bought = false;
    private int unlockWave = 0;

    public LevelItem(String name, ExItemStack baseItem, ExItemStack displayItem, List<ItemLevelType<?>> levelTypes) {
        super(name, displayItem, levelTypes);
        this.item = baseItem;
    }

    public LevelItem(String name, boolean oneTimeBuy, ShopPrice price, ExItemStack baseItem, ExItemStack displayItem,
                     List<ItemLevelType<?>> levelTypes) {
        this(name, baseItem, displayItem, levelTypes);

        this.buyPrice = price;
        this.oneTimeBuy = oneTimeBuy;

        this.displayItem.setLore("§7Buy the item", "", "§7Price:        §2" + this.buyPrice.toString());
    }

    public LevelItem(String name, boolean oneTimeBuy, int unlockWave, ShopPrice price, ExItemStack baseItem,
                     ExItemStack displayItem, List<ItemLevelType<?>> levelTypes) {
        this(name, oneTimeBuy, price, baseItem, displayItem, levelTypes);
        this.unlockWave = unlockWave;

        this.displayItem.setLore("§7Buy the item", "", "§7Price:        §2" + this.buyPrice.toString(), "", "§cLocked" +
                " until wave " + this.unlockWave);
    }

    public LevelItem(LevelItem levelItem) {
        super(levelItem);

        this.bought = levelItem.bought;
        this.buyPrice = levelItem.buyPrice;
        this.oneTimeBuy = levelItem.oneTimeBuy;
    }

    @Override
    public void initLevelTypeClone(Levelable<ItemLevelType<?>> levelable) {
        this.item = ((LevelItem) levelable).item.cloneWithId();
    }

    @Override
    public ItemLevelType<?> cloneLevelType(ItemLevelType<?> levelType) {
        return levelType.clone(this.item);
    }

    public ExItemStack getItem() {
        return item;
    }

    public ExItemStack getDisplayItem() {
        return displayItem;
    }

    public void onLevelClick(MobDefUser user, ExInventory inv, ExItemStack item) {
        ItemLevelType<?> levelType = this.levelTypeByItemId.get(item.getId());

        if (this.buyPrice != null) {
            if (levelType == null && this.getDisplayItem().equals(item)) {

                if (MobDefServer.getWaveNumber() < this.unlockWave) {
                    user.sendPluginMessage(Plugin.MOB_DEFENCE, "This item is locked until wave " + this.unlockWave +
                            " is completed");
                    return;
                }

                if (this.bought && this.oneTimeBuy) {
                    user.sendPluginMessage(Plugin.MOB_DEFENCE, "You already bought this item");
                    return;
                }

                if (!user.containsAtLeast(this.buyPrice.asItem())) {
                    user.sendPluginMessage(Plugin.MOB_DEFENCE, "§cNot enough money");
                    user.playNote(Instrument.STICKS, Note.natural(0, Note.Tone.C));
                    return;
                }

                user.removeCertainItemStack(this.buyPrice.asItem());

                user.addItem(this.item);
                user.playNote(Instrument.STICKS, Note.natural(1, Note.Tone.C));
                this.bought = true;

                return;
            }

            if (!this.bought) {
                user.sendPluginMessage(Plugin.MOB_DEFENCE, "You must buy this item before");
                return;
            }
        }

        if (levelType == null) {
            return;
        }

        levelType.tryLevelUp(user);

        this.item = levelType.getItem();

        inv.setItemStack(levelType.getDisplayItem());
    }

    @Override
    public LevelItem clone() {
        return new LevelItem(this);
    }

}
