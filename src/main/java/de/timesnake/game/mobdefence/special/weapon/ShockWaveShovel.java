package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShockWaveShovel extends SpecialWeapon implements UserInventoryInteractListener {

    public static final double COOLDOWN = 30; // in seconds
    public static final int FREEZE_TIME = 3; // in seconds
    public static final int RADIUS = 4; // in blocks
    public static final int DAMAGE = 2; // in hearts

    public static final double KNOCKBACK = 3;

    private static final ItemLevelType<?> COOLDOWN_LEVELS = new ItemLevelType<>("Cooldown", new ExItemStack(Material.FEATHER), 1, 6, ItemLevel.getLoreNumberLevels("Cooldown", 1, 0, "seconds", 2, List.of(new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(12, ShopCurrency.GOLD), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.SILVER), new ShopPrice(32, ShopCurrency.GOLD)), List.of("-5 seconds", "-5 seconds", "-3 seconds", "-3 seconds", "-3 seconds"), List.of(25, 20, 17, 14, 11)));

    private static final ItemLevelType<?> FREEZE_TIME_LEVELS = new ItemLevelType<>("Freeze Time", new ExItemStack(Material.FROSTED_ICE), 1, 6, ItemLevel.getLoreNumberLevels("Freeze Time", 2, 0, "seconds", 2, List.of(new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(22, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.SILVER), new ShopPrice(64, ShopCurrency.BRONZE)), "+1 second", List.of(4, 5, 6, 7, 8)));

    private static final ItemLevelType<?> RADIUS_LEVELS = new ItemLevelType<>("Radius", new ExItemStack(Material.TARGET), 1, 4, ItemLevel.getLoreNumberLevels("RADIUS", 3, 0, "blocks", 2, List.of(new ShopPrice(8, ShopCurrency.BRONZE), new ShopPrice(7, ShopCurrency.SILVER), new ShopPrice(6, ShopCurrency.GOLD)), "+1 block", List.of(5, 6, 7)));

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 4, ItemLevel.getLoreNumberLevels("Damage", 4, 0, "❤", 2, List.of(new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(18, ShopCurrency.SILVER), new ShopPrice(24, ShopCurrency.GOLD)), "+1 ❤", List.of(3, 4, 5)));

    public static final LevelItem SHOCK_WAVE_SHOVEL = new LevelItem("Shock Wave Shovel", true, new ShopPrice(8, ShopCurrency.GOLD), new ExItemStack(Material.STONE_SHOVEL, "§6Shock Wave Shovel").setLore("", COOLDOWN_LEVELS.getBaseLevelLore(COOLDOWN), FREEZE_TIME_LEVELS.getBaseLevelLore(FREEZE_TIME), RADIUS_LEVELS.getBaseLevelLore(RADIUS), DAMAGE_LEVELS.getBaseLevelLore(DAMAGE)), new ExItemStack(Material.STONE_SHOVEL), List.of(COOLDOWN_LEVELS, FREEZE_TIME_LEVELS, RADIUS_LEVELS, DAMAGE_LEVELS));

    private final Set<User> cooldownUsers = new HashSet<>();

    public ShockWaveShovel() {
        super(SHOCK_WAVE_SHOVEL.getItem());
        Server.getInventoryEventManager().addInteractListener(this, SHOCK_WAVE_SHOVEL.getItem());
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();

        if (!this.cooldownUsers.contains(user)) {
            this.cooldownUsers.add(user);

            ExItemStack item = event.getClickedItem();
            List<String> lore = item.getLore();

            Location userLoc = user.getLocation();

            int cooldown = Integer.parseInt(COOLDOWN_LEVELS.getValueFromLore(lore));
            int freezeTime = Integer.parseInt(FREEZE_TIME_LEVELS.getValueFromLore(lore));
            int radius = Integer.parseInt(RADIUS_LEVELS.getValueFromLore(lore));
            int damage = Integer.parseInt(DAMAGE_LEVELS.getValueFromLore(lore));

            for (LivingEntity entity : userLoc.getNearbyLivingEntities(radius, 2, radius)) {
                if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                    entity.damage(damage * 2, user.getPlayer());
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, freezeTime, 6));

                    Location entityLoc = entity.getLocation();

                    Vector vec = new Vector(entityLoc.getX() - userLoc.getX(), 0, entityLoc.getZ() - userLoc.getZ());
                    entity.setVelocity(vec.normalize().setY(1).normalize().multiply(KNOCKBACK));
                }
            }

            Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), cooldown, GameMobDefence.getPlugin());
        }

    }
}
