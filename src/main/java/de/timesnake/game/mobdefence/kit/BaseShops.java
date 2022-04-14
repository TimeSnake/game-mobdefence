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

    public static final TeamItemShop BLOCK_SHOP = new TeamItemShop("§6Blocks", 31,
            new ExItemStack(Material.OAK_FENCE),
            List.of(),
            List.of(MobDefKit.OAK_FENCE, MobDefKit.OAK_FENCE_GATE, MobDefKit.OAK_PLANKS, MobDefKit.OAK_SLABS,
                    MobDefKit.IRON_BARS, MobDefKit.COBBLESTONE_WALL, MobDefKit.STONE_AXE, MobDefKit.IRON_PICKAXE),
            TrapMaker.getShopTrades());

    public static final TeamItemShop BASIC_SHOP = new TeamItemShop("§6Basics", 29,
            new ExItemStack(Material.COOKED_BEEF),
            List.of(),
            List.of(MobDefKit.APPLE, MobDefKit.COOKED_BEEF, MobDefKit.PUMPKIN_PIE, MobDefKit.DRIED_KELP,
                    MobDefKit.GOLDEN_APPLE, MobDefKit.GOLDEN_CARROT, MobDefKit.MILK, WaterBottle.WATER,
                    MobDefKit.SPEED, MobDefKit.INSTANT_HEAL));

    public static final TeamItemShop TEAM_SHOP = new TeamItemShop("§6Team Stuff", 33,
            new ExItemStack(Material.PLAYER_HEAD),
            List.of(TeamHealth.MAX_HEALTH, ReviveManager.REVIVE),
            List.of(MobDefKit.IRON_GOLEM, MobDefKit.REGEN));

    private static final List<TeamItemShop> SHOPS = List.of(BLOCK_SHOP, BASIC_SHOP, TEAM_SHOP);

    private final HashMap<String, TeamItemShop> shopsByName = new HashMap<>();

    public BaseShops() {
        this.resetShops();
    }

    public void resetShops() {
        this.shopsByName.clear();

        for (TeamItemShop shop : SHOPS) {
            this.shopsByName.put(shop.getName(), shop.clone());
        }
    }

    public TeamItemShop getShop(String name) {
        return this.shopsByName.get(name);
    }
}
