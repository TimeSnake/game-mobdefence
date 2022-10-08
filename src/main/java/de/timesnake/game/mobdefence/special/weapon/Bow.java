package de.timesnake.game.mobdefence.special.weapon;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bow extends SpecialWeapon implements UserInventoryInteractListener, Listener {

    private static final ItemLevelType<?> POWER = new ItemLevelType<>("Power", new ExItemStack(Material.RED_DYE), 0,
            7, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(12,
                    ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.SILVER),
            new ShopPrice(14, ShopCurrency.GOLD), new ShopPrice(48, ShopCurrency.BRONZE), new ShopPrice(43,
                    ShopCurrency.SILVER)), "+1 Power", Enchantment.ARROW_DAMAGE, List.of(1, 2, 3, 4, 5, 6, 7)));

    private static final ItemLevelType<?> FLAME = new ItemLevelType<>("Flame", new ExItemStack(Material.BLAZE_POWDER)
            , 0, 1, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(8, ShopCurrency.GOLD)), "Flame Arrows",
            Enchantment.ARROW_FIRE, List.of(1)));

    private static final ItemLevelType<?> PIERCING = new ItemLevelType<>("Piercing", new ExItemStack(Material.ZOMBIE_HEAD),
            0, 5, ItemLevel.getLoreNumberLevels("Piercing", 1, 0, "mobs", 1,
            List.of(new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.SILVER),
                    new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.BRONZE),
                    new ShopPrice(64, ShopCurrency.BRONZE)),
            "+1 mob", List.of(1, 2, 3, 4, 5)));

    public static final LevelItem BOW = new LevelItem("Bow",
            new ExItemStack(Material.BOW).setUnbreakable(true).setLore("", PIERCING.getBaseLevelLore(0)),
            new ExItemStack(Material.BOW).setUnbreakable(true), List.of(POWER, FLAME, PIERCING));
    private final Set<User> cooldownUser = new HashSet<>();

    public Bow() {
        super(BOW.getItem());
        //Server.getInventoryEventManager().addInteractListener(this, BOW.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
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

        Arrow arrow = user.getPlayer().getWorld().spawnArrow(user.getPlayer().getEyeLocation().add(0, -0.2, 0),
                user.getPlayer().getLocation().getDirection(),
                event.getClickedItem().getEnchantmentLevel(Enchantment.ARROW_DAMAGE) + 2, 1);

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

    @EventHandler
    public void onPlayerLaunchProjectile(PlayerLaunchProjectileEvent e) {
        ExItemStack bow = ExItemStack.getItem(e.getItemStack(), false);

        if (!BOW.getItem().equals(bow)) {
            return;
        }

        Arrow arrow = ((Arrow) e.getProjectile());

        int piercing = Integer.parseInt(PIERCING.getValueFromLore(bow.getLore()));

        arrow.setPierceLevel(piercing);

    }
}
