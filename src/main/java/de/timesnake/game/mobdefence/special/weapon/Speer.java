package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;

import java.util.List;

public class Speer extends CooldownWeapon {

    private static final ItemLevelType<?> POWER = new ItemLevelType<>("Power", new ExItemStack(Material.RED_DYE), 1, 7, ItemLevel.getLoreNumberLevels("Power", 1, 0, "", 1, List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.SILVER), new ShopPrice(14, ShopCurrency.GOLD), new ShopPrice(48, ShopCurrency.BRONZE), new ShopPrice(43, ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.SILVER)), "+1 Power", List.of(2, 3, 4, 5, 6, 7, 8, 9, 10)));
    private static final ItemLevelType<?> PUNCH = new ItemLevelType<>("Punch", new ExItemStack(Material.FEATHER), 0, 2, ItemLevel.getLoreNumberLevels("Punch", 2, 0, "", 1, List.of(new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.SILVER)), "+1 Punch", List.of(2, 3, 4)));
    private static final ItemLevelType<?> FLAME = new ItemLevelType<>("Flame", new ExItemStack(Material.BLAZE_POWDER), 0, 1, ItemLevel.getLoreNumberLevels("Flame", 3, 0, "", 0, List.of(new ShopPrice(64, ShopCurrency.BRONZE)), "Flame", List.of(1)));

    public static final LevelItem SPEER = new LevelItem("Speer", new ExItemStack(Material.ARROW, true).enchant().setLore("", POWER.getBaseLevelLore(1)).setDisplayName("§6Speer"), new ExItemStack(Material.BOW, true), List.of(POWER, PUNCH, FLAME));

    public Speer() {
        super(SPEER.getItem());
    }

    @Override
    public int getCooldown(ExItemStack item) {
        return 10;
    }

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        if (!user.getPlayer().getInventory().containsAtLeast(SPEER.getItem(), 1)) {
            return;
        }

        user.getPlayer().getInventory().removeItem(SPEER.getItem().asOne());

        int power = Integer.parseInt(POWER.getValueFromLore(item.getLore()));

        int flame = 0;
        try {
            flame = Integer.parseInt(FLAME.getValueFromLore(item.getLore()));
        } catch (NumberFormatException ignored) {
        }

        int punch = 0;
        try {
            punch = Integer.parseInt(PUNCH.getValueFromLore(item.getLore()));
        } catch (NumberFormatException ignored) {
        }

        Arrow arrow = user.getPlayer().getWorld().spawnArrow(user.getPlayer().getEyeLocation().add(0, -0.2, 0), user.getPlayer().getLocation().getDirection(), power + 2, 1);

        if (flame > 0) {
            arrow.setFireTicks(Integer.MAX_VALUE);
        }
        arrow.setKnockbackStrength(punch);
        arrow.setShooter(user.getPlayer());
        arrow.setDamage(item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) + 1.3f);

    }
}
