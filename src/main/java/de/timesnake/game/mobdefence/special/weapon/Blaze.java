package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.entities.EntityManager;
import de.timesnake.basic.entities.entity.bukkit.ExBlaze;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.List;

public class Blaze extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.MAGMA_BLOCK, "§6 3 Blazes", "Place the block to spawn a blaze", "§c3 Blazes");

    public static final ItemTrade BLAZE = new ItemTrade(false, new ShopPrice(16, ShopCurrency.SILVER), List.of(Blaze.ITEM), Blaze.ITEM);

    public Blaze() {
        super(EntityType.BLAZE, ITEM);
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @Override
    public int getLeftEntities(ExItemStack item) {
        return Integer.parseInt(item.getLore().get(1).replace("§c", "").replace(" Blazes", "")) - 1;
    }

    @Override
    public void updateItem(ExItemStack item, int left) {
        item.setLore("Place the block to spawn a blaze", "§c" + left + " Blazes");
    }

    @Override
    public void spawnEntities(Location location) {
        ExBlaze blaze = new ExBlaze(location.getWorld(), false);

        blaze.setPosition(location.getX(), location.getY(), location.getZ());

        blaze.addPathfinderGoal(1, new ExPathfinderGoalBlazeFireball());
        blaze.addPathfinderGoal(8, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        blaze.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        blaze.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            blaze.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        blaze.setPersistent(true);

        blaze.setMaxHealth(40);
        blaze.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), blaze);
    }
}
