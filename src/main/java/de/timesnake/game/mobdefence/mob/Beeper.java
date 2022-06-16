package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.bukkit.ExBee;
import de.timesnake.library.entities.entity.bukkit.ExCreeper;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

public class Beeper extends MobDefMob<ExCreeper> {

    public static void handleExplosion(Creeper creeper, Location location) {
        if (creeper.getCustomName() == null || !creeper.getCustomName().equals(NAME)) {
            return;
        }

        Player player = location.getNearbyPlayers(16).stream().findFirst().orElse(null);

        if (player == null) {
            return;
        }

        for (int i = 0; i < 6; i++) {
            ExBee bee = new ExBee(location.getWorld(), true);
            bee.setPosition(location.getX(), location.getY() + 1.5, location.getZ());
            bee.setTarget(((CraftPlayer) player).getHandle(), EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY,
                    true);
            EntityManager.spawnEntity(location.getWorld(), bee);
        }
    }

    private static final String NAME = "beeper";

    public Beeper(ExLocation spawn, int currentWave) {
        super(Type.OTHER, HeightMapManager.MapType.WALL_FINDER, 5, spawn, currentWave);
    }

    @Override
    public void spawn() {

        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExCreeper(world, false);

        ExPathfinderGoalLocationSwell swell = new ExPathfinderGoalLocationSwell(4, 7);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, swell);

        if (this.currentWave > 10) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.4, swell, 5));
        } else if (this.currentWave > 5) {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.3, swell, 5));
        } else {
            this.entity.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.WALL_FINDER, 1.2, swell, 5));
        }

        this.entity.addPathfinderGoal(3, new ExPathfinderGoalSwell(3, 5));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalMeleeAttack(1.1D));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));
        this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman, true,
                true));

        if (this.currentWave > 10) {
            this.entity.setMaxHealth(60);
            this.entity.setHealth(60);
        } else if (this.currentWave > 5) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        } else {
            this.entity.setMaxHealth(30);
            this.entity.setHealth(30);
        }

        this.entity.setCustomName(NAME);
        this.entity.setCustomNameVisible(false);
        this.entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.BEEHIVE));

        super.spawn();


    }
}
