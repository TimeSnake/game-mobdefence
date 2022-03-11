package de.timesnake.game.mobdefence.special.weapon;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Iceball extends SpecialWeapon implements Listener {

    private static final String NAME = "iceball";
    private static final String PIERCING_NAME = "piercing";

    private static final double FIRE_RATE = 2; // per second
    private static final double DAMAGE = 1.5;
    private static final double SPEED = 2;

    private static final ItemLevelType<?> SPEED_LEVELS = new ItemLevelType<>("Speed", new ExItemStack(Material.FEATHER), 1, 5, ItemLevel.getLoreNumberLevels("Speed", 1, 0, "", 2, List.of(new ShopPrice(1, ShopCurrency.GOLD), new ShopPrice(16, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(8, ShopCurrency.GOLD)), "+0.5", List.of(1.5, 2, 2.5, 3)));

    private static final ItemLevelType<?> DAMAGE_LEVELS = new ItemLevelType<>("Damage", new ExItemStack(Material.RED_DYE), 1, 8, ItemLevel.getLoreNumberLevels("Damage", 2, 1, "❤", 2, List.of(new ShopPrice(5, ShopCurrency.BRONZE), new ShopPrice(5, ShopCurrency.SILVER), new ShopPrice(5, ShopCurrency.GOLD), new ShopPrice(10, ShopCurrency.GOLD), new ShopPrice(16, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)), "+0.5 ❤", List.of(2, 2.5, 3, 3.5, 4, 4, 5)));

    private static final ItemLevelType<?> PIERCING_LEVELS = new ItemLevelType<>("Piercing", new ExItemStack(Material.TORCH), 0, 5, ItemLevel.getLoreNumberLevels("Piercing", 3, 0, "mobs", 1, List.of(new ShopPrice(10, ShopCurrency.SILVER), new ShopPrice(11, ShopCurrency.GOLD), new ShopPrice(32, ShopCurrency.BRONZE), new ShopPrice(24, ShopCurrency.SILVER), new ShopPrice(16, ShopCurrency.GOLD)), "+1 mob", List.of(1, 2, 3, 4, 5)));

    public static final ExItemStack ICEBALL = new ExItemStack(Material.SNOWBALL, "§6Ice Ball").setLore("", SPEED_LEVELS.getBaseLevelLore(1), DAMAGE_LEVELS.getBaseLevelLore(1.5), PIERCING_LEVELS.getBaseLevelLore(0));

    public static final LevelItem ITEM = new LevelItem("§6Iceball", ICEBALL, ICEBALL, List.of(SPEED_LEVELS, PIERCING_LEVELS));

    private final Set<User> cooldownUsers = new HashSet<>();

    public Iceball() {
        super(ICEBALL);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onPlayerLaunchProjectile(PlayerLaunchProjectileEvent e) {

        ExItemStack item = new ExItemStack(e.getItemStack());

        if (!item.equals(ICEBALL)) {
            return;
        }

        e.setCancelled(true);

        User user = Server.getUser(e.getPlayer());

        if (this.cooldownUsers.contains(user)) {
            return;
        }

        double speed = Double.parseDouble(SPEED_LEVELS.getValueFromLore(item.getLore()));
        double damage = Double.parseDouble(DAMAGE_LEVELS.getValueFromLore(item.getLore()));
        int piercing = Integer.parseInt(PIERCING_LEVELS.getValueFromLore(item.getLore()));

        Snowball snowball = user.getExWorld().spawn(user.getPlayer().getEyeLocation().add(0, -0.2, 0), Snowball.class);

        snowball.setCustomName(NAME + damage + PIERCING_NAME + piercing);
        snowball.setCustomNameVisible(false);
        snowball.setPersistent(true);
        snowball.setShooter(user.getPlayer());
        snowball.setVelocity(user.getLocation().getDirection().normalize().multiply(speed));

        this.cooldownUsers.add(user);
        Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), 5, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if (snowball.getCustomName() == null || !snowball.getCustomName().contains(NAME)) {
            return;
        }

        String[] nameParts = snowball.getCustomName().replaceAll(NAME, "").split(PIERCING_NAME);

        double damage = Double.parseDouble(nameParts[0]) * 2;
        int piercing = Integer.parseInt(nameParts[1]);

        if (e.getHitEntity() != null && e.getHitEntity() instanceof LivingEntity entity) {

            e.setCancelled(true);

            if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
                return;
            }

            entity.damage(damage, snowball);
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));

            if (piercing == 0) {
                snowball.remove();
            }
        }

        if (e.getHitBlock() == null) {
            return;
        }

        Vector snowballVector = snowball.getVelocity();

        final double magnitude = Math.sqrt(Math.pow(snowballVector.getX(), 2) + Math.pow(snowballVector.getY(), 2) + Math.pow(snowballVector.getZ(), 2));

        if (magnitude < 0.2) {
            return;
        }

        Location hitLoc = snowball.getLocation();

        BlockIterator b = new BlockIterator(hitLoc.getWorld(), hitLoc.toVector(), snowballVector, 0, 3);

        Block blockBefore = snowball.getLocation().getBlock();
        Block nextBlock = b.next();

        while (b.hasNext() && nextBlock.getType() == Material.AIR) {
            blockBefore = nextBlock;
            nextBlock = b.next();
        }

        BlockFace blockFace = nextBlock.getFace(blockBefore);

        if (blockFace != null) {

            // Convert blockFace SELF to UP:
            if (blockFace == BlockFace.SELF) {
                blockFace = BlockFace.UP;
            }

            Vector hitPlain = new Vector(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());

            double dotProduct = snowballVector.dot(hitPlain);
            Vector u = hitPlain.multiply(dotProduct).multiply(2.0);

            float speed = (float) magnitude;
            speed *= 0.6F;

            Snowball newSnowball = snowball.getWorld().spawn(snowball.getLocation(), Snowball.class);
            newSnowball.setVelocity(snowballVector.subtract(u).normalize().multiply(speed));
            newSnowball.setCustomName(NAME + damage + PIERCING_NAME + piercing);
            newSnowball.setCustomNameVisible(false);
            newSnowball.setShooter(snowball.getShooter());

            snowball.remove();

        }
    }


}
