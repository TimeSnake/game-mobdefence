package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.kit.ItemTrade;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExBlaze;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBlazeFireball;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.EntityClass;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.List;

public class Blaze extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.MAGMA_BLOCK, "§6 3 Blazes", "§7Place the block to" +
            " spawn a blaze", "§c3 Blazes");

    public static final ItemTrade BLAZE = new ItemTrade(false, new ShopPrice(16, ShopCurrency.SILVER),
            List.of(Blaze.ITEM), Blaze.ITEM);

    public Blaze() {
        super(EntityType.BLAZE, ITEM, 1);
    }

    @Override
    public int getAmountFromString(String s) {
        return Integer.parseInt(s.replace("§c", "").replace(" Blazes", ""));
    }

    @Override
    public String parseAmountToString(int amount) {
        return "§c" + amount + " Blazes";
    }

    @Override
    public void spawnEntities(Location location) {
        ExBlaze blaze = new ExBlaze(location.getWorld(), false);

        blaze.setPosition(location.getX(), location.getY(), location.getZ());

        blaze.addPathfinderGoal(1, new ExCustomPathfinderGoalBlazeFireball());
        blaze.addPathfinderGoal(8, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        blaze.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        blaze.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));

        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            blaze.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }

        blaze.setPersistent(true);

        blaze.setMaxHealth(40);
        blaze.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), blaze);
    }
}
