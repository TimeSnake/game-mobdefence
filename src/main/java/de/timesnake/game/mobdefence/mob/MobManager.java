package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.entities.entity.bukkit.ExVillager;
import de.timesnake.basic.entities.pathfinder.ExPathfinderGoalLocation;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public class MobManager implements Listener {

    public static final double SPAWN_AMOUNT_MULTIPLIER = 5;
    public static final double SPAWN_AMOUNT_INCREASE = 1.6;

    public static final double SPAWN_TIME_MULTIPLIER = 17;

    public static final int MIN_GROUP_SIZE = 5;
    public static final int MAX_GROUP_SIZE = 7;
    public static final double GROUP_SIZE_INCREASE = 1.4;
    public static final double GROUP_SIZE_PLAYER_MULTIPLIER = 1.7;

    public static final int MOB_LIMIT = 100;

    public static final double MOB_DAMAGE_MULTIPLIER = 1;

    public static final int MOB_TO_COMPRESSED_RATIO = 5;

    private final LinkedList<MobGroup> mobGroups = new LinkedList<>();

    private final Random random = new Random();

    private final MobDropManager dropManager;

    public MobManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
        this.dropManager = new MobDropManager();
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        this.checkRespawn();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!(e.getEntity() instanceof Creeper creeper)) {
            return;
        }

        Beeper.handleExplosion(creeper, e.getLocation());

        this.checkRespawn();
    }

    private void checkRespawn() {
        int alive = MobDefServer.getMap().getWorld().getEntitiesByClasses(MobDefMob.ATTACKER_ENTITY_COUNT_CLASSES.toArray(new Class[0])).size();

        if (alive <= 3 * MobDefServer.getPlayerAmount() * Math.sqrt(MobDefServer.getWaveNumber())) {
            boolean allSpawned = true;
            for (MobGroup mobGroup : this.mobGroups) {
                if (!mobGroup.isSpawned()) {
                    mobGroup.spawn();
                    allSpawned = false;
                    break;
                }
            }

            if (allSpawned && alive == 0 && MobDefServer.isGameRunning() && !MobDefServer.isDelayRunning()) {
                Server.broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 2);
                MobDefServer.initNextWave();
            }

        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
            return;
        }

        if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getDamager().getType())) {
            return;
        }

        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            e.setDamage(e.getDamage() / 6);
        }

        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            e.setDamage(e.getDamage() / 2);
        }
    }

    public boolean compressGroups() {
        return MobDefServer.getMap().getWorld().getEntitiesByClasses(MobDefMob.ATTACKER_ENTITY_COUNT_CLASSES.toArray(new Class[0])).size() > MOB_LIMIT;
    }

    public Collection<Entity> getAliveMobs() {
        return MobDefServer.getMap().getWorld().getEntitiesByClasses(MobDefMob.ATTACKER_ENTITY_COUNT_CLASSES.toArray(new Class[0]));
    }

    // spawn tasks
    public void cancelSpawning() {
        for (MobGroup mobGroup : this.mobGroups) {
            mobGroup.cancel();
        }
    }

    public LivingEntity createCoreEntity() {
        Location loc = MobDefServer.getMap().getCoreLocation();
        ExVillager entity = new ExVillager(loc.getWorld(), ExVillager.Type.PLAINS, false, false);
        entity.addPathfinderGoal(1, new ExPathfinderGoalLocation(loc.getX(), loc.getY(), loc.getZ(), 1.4, 32, 1));
        entity.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        entity.setPersistent(true);
        entity.setInvulnerable(false);

        entity.setMaxHealth(2048);
        entity.setHealth(2048);
        return entity;

    }


    public void spawnWave() {
        int wave = MobDefServer.getWaveNumber();

        double waveSqrt = Math.sqrt(wave);
        double players = MobDefServer.getPlayerAmount();
        double playerSqrt = Math.sqrt(players);

        Server.printText(Plugin.MOB_DEFENCE, "Spawning wave " + wave + " ...");
        this.mobGroups.clear();

        int totalMobAmount = (int) (SPAWN_AMOUNT_MULTIPLIER * Math.sqrt(wave) * SPAWN_AMOUNT_INCREASE * players * this.nextLimitedGaussian(0.2));
        int mobAmount = totalMobAmount;

        int totalTime = (int) (Math.log(totalMobAmount) * SPAWN_TIME_MULTIPLIER);

        int minGroupSize = (int) (MIN_GROUP_SIZE + waveSqrt * GROUP_SIZE_INCREASE + playerSqrt * GROUP_SIZE_PLAYER_MULTIPLIER);
        int maxGroupSize = ((int) (MAX_GROUP_SIZE + waveSqrt * GROUP_SIZE_INCREASE + playerSqrt * GROUP_SIZE_PLAYER_MULTIPLIER));

        List<Integer> groupSizes = new ArrayList<>();

        while (totalMobAmount > 0) {
            int groupSize = this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize;
            groupSizes.add(groupSize);
            totalMobAmount -= groupSize;
        }

        int groupAmount = groupSizes.size();
        int delay = totalTime / groupAmount;

        this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(), groupSizes.get(0), 1));

        for (int i = 1; i < groupSizes.size(); i++) {
            int groupDelay = (int) (this.nextLimitedGaussian(0.2) * delay) + delay * (i - 1);
            this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(), groupSizes.get(i), groupDelay));
        }

        if (wave % 5 == 0) {
            List<MobDefMob<?>> bossMobs = new ArrayList<>();
            for (int i = 0; i < playerSqrt; i++) {
                bossMobs.add(new Illusioner(MobDefServer.getMap().getRandomMobPath().getLocation(), wave));
                bossMobs.add(new BossZombie(MobDefServer.getMap().getRandomMobPath().getLocation(), wave));
            }

            delay = (int) (this.nextLimitedGaussian(0.2) * delay) + delay * (groupSizes.size() - 1);

            MobGroup bossGroup = new MobGroup(bossMobs, delay);
            this.mobGroups.addLast(bossGroup);

            this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(), this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));
            this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(), this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));

            Server.printText(Plugin.MOB_DEFENCE, "Bosses: " + bossGroup.size());
        }

        if (wave % 5 == 1 && wave > 5) {
            List<MobDefMob<?>> bossMobs = new ArrayList<>();
            for (int i = 0; i < playerSqrt; i++) {
                bossMobs.add(new BossSkeletonStray(MobDefServer.getMap().getRandomMobPath().getLocation(), wave));
            }

            delay = (int) (this.nextLimitedGaussian(0.2) * delay) + delay * (groupSizes.size() - 1);

            MobGroup bossGroup = new MobGroup(bossMobs, delay);
            this.mobGroups.addLast(bossGroup);

            this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(), this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));
            this.mobGroups.addLast(new MobGroup(wave, MobDefServer.getMap().getRandomMobPath().getLocation(), this.random.nextInt(maxGroupSize - minGroupSize) + minGroupSize, delay));

            Server.printText(Plugin.MOB_DEFENCE, "Bosses: " + bossGroup.size());
        }

        for (MobGroup mobGroup : mobGroups) {
            mobGroup.run();
        }

        Server.printText(Plugin.MOB_DEFENCE, "Mobs: " + mobAmount + " in " + groupAmount + " groups and bosses");

    }

    private double nextLimitedGaussian() {
        double d = this.random.nextGaussian();
        return d >= -1 && d < 1 ? d + 1 : this.nextLimitedGaussian();
    }

    private double nextLimitedGaussian(double range) {
        return range * this.nextLimitedGaussian() + 1 - range / 2;
    }

}
