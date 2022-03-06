package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.EntityDamageByUserEvent;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeaknessShears extends SpecialWeapon implements Listener {

    private static final double DAMAGE = 4;
    private static final int WEAKNESS = 10;
    private static final int WEAKNESS_AMPLIFIER = 1;
    private static final int COOLDOWN = 7;

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 6, ItemLevel.getLoreNumberLevels("Damage", 1, 1, "❤", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER), new ShopPrice(4, ShopCurrency.GOLD), new ShopPrice(25, ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(46, ShopCurrency.BRONZE), new ShopPrice(32, ShopCurrency.SILVER), new ShopPrice(17, ShopCurrency.GOLD)), "+0.5 ❤", List.of(5, 6, 7, 8, 9, 10, 12, 13)));

    private static final ItemLevelType<?> WEAKNESS_LEVELS = new ItemLevelType<>("Weakness Duration", new ExItemStack(Material.STONE_SWORD), 1, 3, ItemLevel.getLoreNumberLevels("Weakness", 2, 0, "s", 2, List.of(new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.BRONZE)), "+5 Seconds", List.of(15, 20)));

    private static final ItemLevelType<?> WEAKNESS_AMPLIFIER_LEVELS = new ItemLevelType<>("Weakness Amplifier", new ExItemStack(Material.GOLDEN_BOOTS), 1, 3, ItemLevel.getLoreNumberLevels("Weakness Amplifier", 3, 0, "", 2, List.of(new ShopPrice(12, ShopCurrency.SILVER), new ShopPrice(12, ShopCurrency.GOLD)), "+1 Weakness", List.of(2, 3)));

    public static final LevelItem LEVEL_ITEM = new LevelItem("§6Shears", new ExItemStack(Material.SHEARS, "§6Shears").setUnbreakable(true).setLore("", DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), WEAKNESS_LEVELS.getBaseLevelLore(WEAKNESS), WEAKNESS_AMPLIFIER_LEVELS.getBaseLevelLore(WEAKNESS_AMPLIFIER)).enchant(), new ExItemStack(Material.SHEARS, "§6Shears"), List.of(DAMAGE_LEVELS, WEAKNESS_LEVELS, WEAKNESS_AMPLIFIER_LEVELS));

    private final Set<User> cooldownUsers = new HashSet<>();

    public WeaknessShears() {
        super(LEVEL_ITEM.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onUserDamageEntity(EntityDamageByUserEvent e) {
        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntity().getType())) {
            return;
        }

        User user = e.getUser();

        ExItemStack shears = ExItemStack.getItem(user.getInventory().getItemInMainHand(), false);

        if (shears == null || !shears.equals(LEVEL_ITEM.getItem())) {
            return;
        }

        if (this.cooldownUsers.contains(user)) {
            return;
        }

        this.cooldownUsers.add(user);

        List<String> lore = shears.getLore();

        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(lore));
        int weakness = Integer.parseInt(WEAKNESS_LEVELS.getValueFromLore(lore));
        int amplifier = Integer.parseInt(WEAKNESS_AMPLIFIER_LEVELS.getValueFromLore(lore));

        e.setDamage(damage * 2);

        ((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, weakness * 20, amplifier));

        Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), COOLDOWN, GameMobDefence.getPlugin());
    }
}
