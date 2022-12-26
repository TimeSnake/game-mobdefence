/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.Trade;
import de.timesnake.game.mobdefence.special.BlockSpawner;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExBlaze;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.LivingEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBlazeFireball;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

public class Blaze extends BlockSpawner implements Listener {

    public static final ExItemStack ITEM = new ExItemStack(Material.MAGMA_BLOCK, "§6 3 Blazes", "§7Place the block to" +
            " spawn a blaze", "§c3 Blazes").immutable();

    public static final Trade.Builder BLAZE = new Trade.Builder()
            .price(new Price(16, Currency.SILVER))
            .giveItems(Blaze.ITEM);

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
        ExBlaze blaze = new ExBlaze(location.getWorld(), false, false);

        blaze.setPosition(location.getX(), location.getY(), location.getZ());

        blaze.addPathfinderGoal(1, new ExCustomPathfinderGoalBlazeFireball());
        blaze.addPathfinderGoal(8, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        blaze.addPathfinderGoal(8, new ExPathfinderGoalRandomLookaround());

        blaze.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES.toArray(Class[]::new)));

        for (Class<? extends LivingEntity> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            blaze.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }

        blaze.setPersistent(true);

        blaze.setMaxHealth(40);
        blaze.setHealth(40);

        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), blaze);
    }
}
