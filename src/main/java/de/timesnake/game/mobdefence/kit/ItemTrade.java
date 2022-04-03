package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.CoreRegeneration;
import de.timesnake.game.mobdefence.special.weapon.IronGolem;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemTrade extends ShopTrade {

    // blocks
    public static final IncreasingItemTrade OAK_FENCE = new IncreasingItemTrade(10, new ShopPrice(2,
            ShopCurrency.BRONZE),
            1, 8, List.of(new ExItemStack(Material.OAK_FENCE, 3)),
            new ExItemStack(Material.OAK_FENCE, 3, "§6Oak Fence"));

    public static final IncreasingItemTrade OAK_FENCE_GATE = new IncreasingItemTrade(11, new ShopPrice(2,
            ShopCurrency.BRONZE),
            1, 6, List.of(new ExItemStack(Material.OAK_FENCE_GATE, 2)),
            new ExItemStack(Material.OAK_FENCE_GATE, 2, "§6Oak Fence Gate"));

    public static final IncreasingItemTrade OAK_PLANKS = new IncreasingItemTrade(19, new ShopPrice(1,
            ShopCurrency.SILVER),
            1, 10, List.of(new ExItemStack(Material.OAK_PLANKS, 4)),
            new ExItemStack(Material.OAK_PLANKS, 4, "§6Oak Plank"));

    public static final IncreasingItemTrade OAK_SLABS = new IncreasingItemTrade(20, new ShopPrice(1,
            ShopCurrency.SILVER),
            1, 10, List.of(new ExItemStack(Material.OAK_SLAB, 4)),
            new ExItemStack(Material.OAK_SLAB, 4, "§6Oak Slab"));

    public static final IncreasingItemTrade IRON_BARS = new IncreasingItemTrade(28, new ShopPrice(2,
            ShopCurrency.SILVER),
            1, 8, List.of(new ExItemStack(Material.IRON_BARS, 3)),
            new ExItemStack(Material.IRON_BARS, 3, "§6Iron Bars"));

    public static final IncreasingItemTrade COBBLESTONE_WALL = new IncreasingItemTrade(29, new ShopPrice(2,
            ShopCurrency.SILVER),
            1, 8, List.of(new ExItemStack(Material.COBBLESTONE_WALL, 2)),
            new ExItemStack(Material.COBBLESTONE_WALL, 2, "§6Cobblestone Wall"));

    public static final ItemTrade STONE_AXE = new ItemTrade(13, false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.STONE_AXE, true)),
            new ExItemStack(Material.STONE_AXE, 1, "§6Stone Axe"));

    public static final ItemTrade IRON_PICKAXE = new ItemTrade(14, false, new ShopPrice(8, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.STONE_PICKAXE, true)),
            new ExItemStack(Material.STONE_PICKAXE, 1, "§6Stone Pickaxe"));

    // basics
    public static final ItemTrade APPLE = new ItemTrade(10, false, new ShopPrice(1, ShopCurrency.SILVER),
            List.of(new ExItemStack(Material.APPLE, 6)),
            new ExItemStack(Material.APPLE, 6, "§6Apple"));

    public static final ExItemStack BEEF = new ExItemStack(Material.COOKED_BEEF, 12).setSlot(7);
    public static final ItemTrade COOKED_BEEF = new ItemTrade(19, false, new ShopPrice(4, ShopCurrency.BRONZE),
            List.of(BEEF), new ExItemStack(Material.COOKED_BEEF, 12, "§6Cooked Beef"));

    public static final ItemTrade PUMPKIN_PIE = new ItemTrade(11, false, new ShopPrice(2, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.PUMPKIN_PIE, 4)),
            new ExItemStack(Material.PUMPKIN_PIE, 4, "§6Pumpkin Pie"));

    public static final ExItemStack KELP = new ExItemStack(Material.DRIED_KELP, 8, "§6Dried Kelp").setLore("§7Healthy" +
            " Fast Food");
    public static final ItemTrade DRIED_KELP = new ItemTrade(20, false, new ShopPrice(1, ShopCurrency.BRONZE),
            List.of(KELP), KELP, "Healthy Fast Food");

    public static final ItemTrade GOLDEN_APPLE = new IncreasingItemTrade(12, new ShopPrice(2, ShopCurrency.SILVER),
            1, 6, List.of(new ExItemStack(Material.GOLDEN_APPLE, 1)),
            new ExItemStack(Material.GOLDEN_APPLE, 1, "§6Golden Apple"));

    public static final ItemTrade GOLDEN_CARROT = new ItemTrade(21, false, new ShopPrice(1, ShopCurrency.GOLD),
            List.of(new ExItemStack(Material.GOLDEN_CARROT, 6)),
            new ExItemStack(Material.GOLDEN_CARROT, 6, "§6Golden Carrot"));


    public static final ItemTrade MILK = new ItemTrade(14, false, new ShopPrice(2, ShopCurrency.BRONZE),
            List.of(new ExItemStack(Material.MILK_BUCKET)), new ExItemStack(Material.MILK_BUCKET, "§6Milk"));


    public static final ItemTrade ENDER_PEARL = new ItemTrade(15, false, new ShopPrice(1, ShopCurrency.GOLD),
            List.of(new ExItemStack(Material.ENDER_PEARL, "§6Ender Pearl")),
            new ExItemStack(Material.ENDER_PEARL, "§6Ender Pearl"));

    public static final ItemTrade SPEED = new ItemTrade(37, false, new ShopPrice(1, ShopCurrency.SILVER),
            List.of(new ExItemStack(Material.SPLASH_POTION, PotionType.SPEED, false, false)),
            new ExItemStack(Material.SPLASH_POTION, PotionType.SPEED, false, false).setDisplayName("§6Speed"));

    public static final ItemTrade INSTANT_HEAL = new IncreasingItemTrade(38, new ShopPrice(6, ShopCurrency.BRONZE),
            1, 32,
            List.of(new ExItemStack(Material.SPLASH_POTION, PotionType.INSTANT_HEAL, false, true).setDisplayName(
                    "§6Instant Heal")),
            new ExItemStack(Material.SPLASH_POTION, PotionType.INSTANT_HEAL, false, true).setDisplayName("§6Instant " +
                    "Heal"));

    public static final ItemTrade IRON_GOLEM = new IncreasingItemTrade(37, new ShopPrice(12, ShopCurrency.SILVER),
            2, 4, List.of(IronGolem.ITEM), IronGolem.ITEM);

    public static final ShopTrade REGEN = new ShopTrade(true, new ShopPrice(4, ShopCurrency.EMERALD),
            CoreRegeneration.ITEM) {
        @Override
        public ShopTrade clone() {
            return this;
        }

        @Override
        public void sell(MobDefUser user) {
            MobDefServer.getMobDefUserManager().getCoreRegeneration().run(user);
        }
    };


    private final Collection<ExItemStack> sellingItems;

    public ItemTrade(int slot, boolean oneTimeBuy, ShopPrice price, Collection<ExItemStack> sellingItems, ExItemStack displayItem, String... description) {
        super(slot, oneTimeBuy, price, displayItem, description);
        this.sellingItems = sellingItems;
    }

    public ItemTrade(boolean oneTimeBuy, ShopPrice price, Collection<ExItemStack> sellingItems, ExItemStack displayItem, String... description) {
        this(0, oneTimeBuy, price, sellingItems, displayItem, description);
    }

    public ItemTrade(ItemTrade itemTrade) {
        super(itemTrade);
        this.sellingItems = new ArrayList<>();
        for (ExItemStack sellingItem : itemTrade.sellingItems) {
            this.sellingItems.add(sellingItem.cloneWithId());
        }
    }

    @Override
    public ItemTrade clone() {
        return new ItemTrade(this);
    }

    @Override
    public void sell(MobDefUser user) {
        for (ExItemStack sellingItem : this.sellingItems) {
            user.addItem(sellingItem.cloneWithId());
        }
    }

    public Collection<ExItemStack> getSellingItems() {
        return sellingItems;
    }
}
