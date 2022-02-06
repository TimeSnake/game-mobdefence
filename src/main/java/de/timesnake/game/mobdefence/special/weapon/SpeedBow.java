package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpeedBow extends SpecialWeapon implements UserInventoryInteractListener {

    private static final ItemLevelType<?> POWER = new ItemLevelType<>("Power", new ExItemStack(Material.RED_DYE), 0, 7, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.SILVER), new ShopPrice(14, ShopCurrency.GOLD), new ShopPrice(48, ShopCurrency.BRONZE), new ShopPrice(43, ShopCurrency.SILVER)), "+1 Power", Enchantment.ARROW_DAMAGE, List.of(1, 2, 3, 4, 5, 6, 7)));

    private static final ItemLevelType<?> FLAME = new ItemLevelType<>("Flame", new ExItemStack(Material.BLAZE_POWDER), 0, 1, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(8, ShopCurrency.GOLD)), "Flame Arrows", Enchantment.ARROW_FIRE, List.of(1)));

    public static final LevelItem BOW = new LevelItem("Bow", new ExItemStack(Material.BOW, true), new ExItemStack(Material.BOW, true), List.of(POWER, FLAME));

    private final Set<User> cooldownUser = new HashSet<>();

    public SpeedBow() {
        super(BOW.getItem());
        Server.getInventoryEventManager().addInteractListener(this, BOW.getItem());
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }

        if (!event.getClickedItem().equals(BOW.getItem())) {
            return;
        }

        User user = event.getUser();

        event.setCancelled(true);

        if (this.cooldownUser.contains(user)) {
            return;
        }

        if (!user.getPlayer().getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1)) {
            return;
        }

        user.getPlayer().getInventory().removeItem(new ItemStack(Material.ARROW, 1));

        Arrow arrow = user.getPlayer().getWorld().spawnArrow(user.getPlayer().getEyeLocation().add(0, -0.2, 0), user.getPlayer().getLocation().getDirection(), event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) + 2, 1);

        if (event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_FIRE) > 0) {
            arrow.setFireTicks(Integer.MAX_VALUE);
        }

        arrow.setShooter(user.getPlayer());
        arrow.setDamage(event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) + 1.3f);

        this.cooldownUser.add(user);

        Server.runTaskLaterSynchrony(() -> {
            this.cooldownUser.remove(user);
        }, 14, GameMobDefence.getPlugin());
    }
}
