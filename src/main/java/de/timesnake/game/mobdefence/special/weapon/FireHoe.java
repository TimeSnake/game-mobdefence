package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.EntityDamageByUserEvent;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.user.MobDefUser;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FireHoe extends CooldownWeapon implements Listener {

    public static final double DAMAGE = 2.5;
    public static final int BURNING_TIME = 3; // in seconds
    public static final int SLOWNESS = 2;
    public static final int COOLDOWN = 7;
    public static final int FIRE_WIND_COOLDOWN = 40;

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 6, ItemLevel.getLoreNumberLevels("Damage", 1, 1, "❤", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER), new ShopPrice(5, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(32, ShopCurrency.SILVER)), "+1 ❤", List.of(3, 4, 5, 6, 7)));

    private static final ItemLevelType<?> BURNING_TIME_LEVELS = new ItemLevelType<>("Burning Time", new ExItemStack(Material.BLAZE_POWDER), 1, 7, ItemLevel.getLoreNumberLevels("Burning Time", 2, 0, "s", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(4, ShopCurrency.SILVER), new ShopPrice(3, ShopCurrency.GOLD), new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(8, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD)), "+1 Second", List.of(4, 5, 6, 7, 8, 9)));

    private static final ItemLevelType<?> SLOWNESS_LEVELS = new ItemLevelType<>("Slowness", new ExItemStack(Material.GOLDEN_BOOTS), 1, 3, ItemLevel.getLoreNumberLevels("Slowness", 3, 0, "", 2, List.of(new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(48, ShopCurrency.BRONZE)), "+1 Second", List.of(3, 4)));

    public static final LevelItem FIRE_HOE = new LevelItem("Frozen Fire Hoe", new ExItemStack(Material.GOLDEN_HOE, "§6Frozen Fire Hoe").enchant().setUnbreakable(true).setLore("", DAMAGE_LEVELS.getBaseLevelLore(DAMAGE), BURNING_TIME_LEVELS.getBaseLevelLore(BURNING_TIME), SLOWNESS_LEVELS.getBaseLevelLore(SLOWNESS)), new ExItemStack(Material.GOLDEN_HOE, "§6Frozen Fire Hoe").enchant(), List.of(DAMAGE_LEVELS, BURNING_TIME_LEVELS, SLOWNESS_LEVELS));


    private final Set<User> cooldownUsers = new HashSet<>();

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

        ExItemStack hoe = ExItemStack.getItem(user.getInventory().getItemInMainHand(), false);

        if (hoe == null || !hoe.equals(FIRE_HOE.getItem())) {
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

    @Override
    public void onInteract(ExItemStack item, MobDefUser user) {
        Location loc = user.getLocation().add(0, 1.5, 0).add(user.getLocation().getDirection().normalize().multiply(0.3));
        World world = loc.getWorld();

        for (double angle = -Math.PI / 10; angle < Math.PI / 9; angle += Math.PI * 0.05) {
            Vector vec = loc.getDirection().clone().rotateAroundY(angle).normalize();
            double x = vec.getX();
            double z = vec.getZ();

            world.spawnParticle(Particle.FLAME, loc.getX(), loc.getY(), loc.getZ(), 2, x, 0, z, 0, null);
        }

        for (LivingEntity entity : user.getLocation().getNearbyLivingEntities(5, e -> MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getType()))) {
            Vector vec = entity.getLocation().toVector().subtract(user.getLocation().toVector());
            float angle = vec.angle(new Vector(0, 0, 1));

            if (angle > -15 && angle < 20) {
                entity.damage(4);
                entity.setFireTicks(20 * 3);
            }
        }

    }

    @Override
    public int getCooldown(ExItemStack item) {
        return FIRE_WIND_COOLDOWN;
    }
}
