package de.timesnake.game.mobdefence.kit;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.special.TeamHealth;
import de.timesnake.game.mobdefence.special.trap.TrapMaker;
import de.timesnake.game.mobdefence.special.weapon.WaterBottle;
import de.timesnake.game.mobdefence.user.ReviveManager;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.List;

public class BaseShops {

    public static final TeamItemShop BLOCK_SHOP = new TeamItemShop("§6Blocks", 31, new ExItemStack(Material.OAK_FENCE), List.of(), List.of(ItemTrade.OAK_FENCE, ItemTrade.OAK_FENCE_GATE, ItemTrade.OAK_PLANKS, ItemTrade.OAK_SLABS, ItemTrade.IRON_BARS, ItemTrade.COBBLESTONE_WALL, ItemTrade.STONE_AXE, ItemTrade.IRON_PICKAXE), TrapMaker.getShopTrades());

    public static final TeamItemShop BASIC_SHOP = new TeamItemShop("§6Basics", 29, new ExItemStack(Material.COOKED_BEEF), List.of(), List.of(ItemTrade.APPLE, ItemTrade.COOKED_BEEF, ItemTrade.PUMPKIN_PIE, ItemTrade.DRIED_KELP, ItemTrade.GOLDEN_APPLE, ItemTrade.GOLDEN_CARROT, ItemTrade.MILK, WaterBottle.WATER, ItemTrade.SPEED, ItemTrade.INSTANT_HEAL));

    public static final TeamItemShop TEAM_SHOP = new TeamItemShop("§6Team Stuff", 33, new ExItemStack(Material.PLAYER_HEAD), List.of(TeamHealth.MAX_HEALTH, ReviveManager.REVIVE), List.of(ItemTrade.IRON_GOLEM, ItemTrade.REGEN));

    private static final List<TeamItemShop> SHOPS = List.of(BLOCK_SHOP, BASIC_SHOP, TEAM_SHOP);

    private final HashMap<String, TeamItemShop> shopsByName = new HashMap<>();

    public BaseShops() {
        this.resetShops();
    }

    public void resetShops() {
        for (TeamItemShop shop : SHOPS) {
            this.shopsByName.put(shop.getName(), shop.clone());
        }
    }

    public TeamItemShop getShop(String name) {
        return this.shopsByName.get(name);
    }
}
