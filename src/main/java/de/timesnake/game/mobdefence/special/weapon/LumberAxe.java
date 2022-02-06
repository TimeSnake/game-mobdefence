package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class LumberAxe extends SpecialWeapon implements Listener {

    private static final int ATTACK_SPEED = 6;

    private static final ItemLevelType<?> TYPE = new ItemLevelType<>("Type", new ExItemStack(Material.ANVIL), 1, 3, ItemLevel.getMaterialLevels(2, List.of(new ShopPrice(6, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.BRONZE)), List.of("Diamond Axe", "Netherite Axe"), List.of(Material.DIAMOND_AXE, Material.NETHERITE_AXE)));

    private static final ItemLevelType<?> SHARPNESS = new ItemLevelType<>("Sharpness", new ExItemStack(Material.RED_DYE), 0, 12, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(4, ShopCurrency.GOLD), new ShopPrice(19, ShopCurrency.BRONZE), new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD), new ShopPrice(27, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(14, ShopCurrency.GOLD), new ShopPrice(51, ShopCurrency.BRONZE), new ShopPrice(56, ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.BRONZE)), List.of("+1 Sharpness", "+1 Sharpness", "+1 Sharpness", "+1 Sharpness", "+1 Sharpness", "+1 Sharpness", "+1 Sharpness", "+1 Sharpness", "+2 Sharpness", "+2 Sharpness", "+2 Sharpness", "+2 Sharpness"), Enchantment.DAMAGE_ALL, List.of(1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 14, 16)));

    private static final ItemLevelType<?> KNOCKBACK = new ItemLevelType<>("Knockback", new ExItemStack(Material.FEATHER), 0, 5, ItemLevel.getEnchantmentLevels(1, List.of(new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(6, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(29, ShopCurrency.SILVER)), "+1 Knockback", Enchantment.KNOCKBACK, List.of(1, 2, 3, 4, 5)));

    private static final ItemLevelType<?> ATTACK_SPEED_LEVELS = new ItemLevelType<>("Attack Speed", new ExItemStack(Material.FEATHER), 1, 11, ItemLevel.getLoreNumberLevels("Attack Speed", 2, 0, "per second", 1, List.of(new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(7, ShopCurrency.GOLD), new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(18, ShopCurrency.GOLD), new ShopPrice(64, ShopCurrency.BRONZE)), "+0.5 per second", List.of(8, 10, 12, 14, 16, 18))) {
        @Override
        protected boolean levelUp(MobDefUser user, ItemLevel.LoreNumberLevel<Integer> level) {
            AttributeInstance speed = user.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
            speed.setBaseValue(level.getValue());

            return super.levelUp(user, level);
        }
    };

    public static final LevelItem AXE = new LevelItem("§6Axe", new ExItemStack(Material.IRON_AXE, true).setLore("", ATTACK_SPEED_LEVELS.getBaseLevelLore(ATTACK_SPEED)), new ExItemStack(Material.IRON_AXE), List.of(TYPE, SHARPNESS, ATTACK_SPEED_LEVELS));

    public LumberAxe() {
        super(AXE.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
            return;
        }

        LivingEntity entity = e.getEntity();

        if (entity.getKiller() == null || !(entity.getKiller() instanceof Player)) {
            return;
        }

        MobDefUser user = (MobDefUser) Server.getUser(entity.getKiller());

        if (user.getKit() != null && user.getKit().equals(MobDefKit.LUMBERJACK)) {
            user.addPotionEffect(PotionEffectType.REGENERATION, 4 * 20, 1);
            if (Math.random() == 0) {
                user.getPlayer().setAbsorptionAmount(user.getPlayer().getAbsorptionAmount() + 6);
            }
        }
    }
}
