package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.entities.EntityManager;
import de.timesnake.basic.entities.entity.bukkit.ExSnowman;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Snowman extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.CARVED_PUMPKIN, "§6 4 Snowmen", "§7Place the block to spawn a snowman", "§c4 Snowmen");

    public static final ItemTrade SNOWMAN = new ItemTrade(false, new ShopPrice(8, ShopCurrency.GOLD), List.of(Snowman.ITEM), Snowman.ITEM);

    public Snowman() {
        super(EntityType.SNOWMAN, ITEM);
    }

    @Override
    public int getLeftEntities(ExItemStack item) {
        return Integer.parseInt(item.getLore().get(1).replace("§c", "").replace(" Snowmen", "")) - 1;
    }

    @Override
    public void updateItem(ExItemStack item, int left) {
        item.setLore("Place the block to spawn a snowman", "§c" + left + " Snowmen");
    }

    @Override
    public void spawnEntities(Location location) {
        ExSnowman snowman = new ExSnowman(location.getWorld(), false);
        snowman.setPosition(location.getX(), location.getY(), location.getZ());
        snowman.setSlot(ExEnumItemSlot.HEAD, null);

        snowman.addPathfinderGoal(1, new ExPathfinderGoalArrowAttack(0D, 1, 10.0F));
        snowman.addPathfinderGoal(3, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        snowman.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        snowman.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            snowman.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, false));
        }

        snowman.setPersistent(true);

        snowman.setMaxHealth(40);
        snowman.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), snowman);
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if (e.getHitEntity() == null || !(e.getHitEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(entity.getType())) {
            e.setCancelled(true);
            return;
        }

        if (!(snowball.getShooter() instanceof org.bukkit.entity.Snowman)) {
            return;
        }

        entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 1));
        entity.damage(1, snowball);
    }
}
