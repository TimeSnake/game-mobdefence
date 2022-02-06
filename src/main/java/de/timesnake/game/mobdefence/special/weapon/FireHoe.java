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

public class FireHoe extends SpecialWeapon implements Listener {

    public static final double DAMAGE = 2.5;
    public static final int BURNING_TIME = 3; // in seconds
    public static final int SLOWNESS = 2;
    public static final int COOLDOWN = 7;

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 6, ItemLevel.getLoreNumberLevels("Damage", 1, 1, "❤", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER), new ShopPrice(5, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(32, ShopCurrency.SILVER)), "+1 ❤", List.of(3, 4, 5, 6, 7)));

    private static final ItemLevelType<?> BURNING_TIME_LEVELS = new ItemLevelType<>("Burning Time", new ExItemStack(Material.BLAZE_POWDER), 1, 7, ItemLevel.getLoreNumberLevels("Burning Time", 2, 0, "s", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(3, ShopCurrency.GOLD), new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(8, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD)), "+1 Second", List.of(4, 5, 6, 7, 8, 9)));

    private static final ItemLevelType<?> SLOWNESS_LEVELS = new ItemLevelType<>("Slowness", new ExItemStack(Material.GOLDEN_BOOTS), 1, 3, ItemLevel.getLoreNumberLevels("Slowness", 3, 0, "", 2, List.of(new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.BRONZE)), "+1 Second", List.of(3, 4)));

    public static final LevelItem FIRE_HOE = new LevelItem("Frozen Fire Hoe", new ExItemStack(Material.GOLDEN_HOE, "§6Frozen Fire Hoe").enchant().setUnbreakable(true).setLore("", DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), BURNING_TIME_LEVELS.getBaseLevelLore(BURNING_TIME), SLOWNESS_LEVELS.getBaseLevelLore(SLOWNESS)), new ExItemStack(Material.GOLDEN_HOE, "§6Frozen Fire Hoe").enchant(), List.of(DAMAGE_LEVELS, BURNING_TIME_LEVELS, SLOWNESS_LEVELS));


    private Set<User> cooldownUsers = new HashSet<>();

    public FireHoe() {
        super(FIRE_HOE.getItem());
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByUserEvent event) {
        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(event.getEntity().getType())) {
            return;
        }

        User user = event.getUser();

        ExItemStack hoe = new ExItemStack(user.getInventory().getItemInMainHand());

        if (!hoe.equals(FIRE_HOE.getItem())) {
            return;
        }

        if (this.cooldownUsers.contains(user)) {
            return;
        }

        this.cooldownUsers.add(user);

        List<String> lore = hoe.getLore();

        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(lore));
        int burningTime = Integer.parseInt(BURNING_TIME_LEVELS.getValueFromLore(lore));
        int slowness = Integer.parseInt(SLOWNESS_LEVELS.getValueFromLore(lore));

        LivingEntity entity = (LivingEntity) event.getEntity();

        event.setDamage(damage * 2);

        entity.setFireTicks(burningTime * 20);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 3, slowness - 1));

        Server.runTaskLaterSynchrony(() -> cooldownUsers.remove(user), COOLDOWN, GameMobDefence.getPlugin());
    }

}
