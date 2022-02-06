package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class Sword extends SpecialWeapon implements Listener {

    private static final ItemLevelType<?> SWORD_TYPE = new ItemLevelType<>("Type", new ExItemStack(Material.ANVIL), 1, 5, ItemLevel.getMaterialLevels(2, List.of(new ShopPrice(8, ShopCurrency.BRONZE), new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)), List.of("Stone Sword", "Iron Sword", "Diamond Sword", "Netherite Sword"), List.of(Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD)));

    private static final ItemLevelType<?> SHARPNESS = new ItemLevelType<>("Sharpness", new ExItemStack(Material.RED_DYE), 0, 10, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(9, ShopCurrency.BRONZE), new ShopPrice(15, ShopCurrency.SILVER), new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.BRONZE), new ShopPrice(25, ShopCurrency.SILVER), new ShopPrice(15, ShopCurrency.GOLD), new ShopPrice(48, ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.BRONZE), new ShopPrice(64, ShopCurrency.SILVER)), "+1 Sharpness", Enchantment.DAMAGE_ALL, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));

    private static final ItemLevelType<?> SWEEPING_EDGE = new ItemLevelType<>("Sweeping Edge", new ExItemStack(Material.FEATHER), 0, 5, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(8, ShopCurrency.SILVER), new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(45, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)), "+1 Sweeping Edge", Enchantment.SWEEPING_EDGE, List.of(1, 2, 3, 4, 5)));

    public static final ExItemStack WOODEN_SWORD = new ExItemStack(Material.WOODEN_SWORD, true);
    public static final LevelItem SWORD = new LevelItem("Sword", WOODEN_SWORD, new ExItemStack(Material.IRON_SWORD), List.of(SWORD_TYPE, SHARPNESS, SWEEPING_EDGE));


    public Sword() {
        super(SWORD.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }

        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
            return;
        }

        User user = Server.getUser(((Player) e.getDamager()));

        for (LivingEntity entity : user.getLocation().getNearbyLivingEntities(2, 1)) {
            if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                entity.damage(2, user.getPlayer());
            }
        }
    }
}
