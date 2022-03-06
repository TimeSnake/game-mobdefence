package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.EntityManager;
import de.timesnake.basic.entities.entity.extension.EntityExtension;
import de.timesnake.basic.entities.entity.extension.ExEntityInsentient;
import de.timesnake.basic.entities.pathfinder.ExPathfinderGoal;
import de.timesnake.basic.entities.pathfinder.ExPathfinderGoalBreakBlock;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.ExHeightPathfinder;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class MobDefMob<M extends Mob & EntityExtension<? extends ExEntityInsentient>> {

    public static final int BREAK_LEVEL = 16;

    public static final List<Class<?>> ATTACKER_ENTITY_CLASSES = List.of(org.bukkit.entity.Zombie.class, org.bukkit.entity.Skeleton.class, org.bukkit.entity.Blaze.class, org.bukkit.entity.Illusioner.class, org.bukkit.entity.Witch.class, org.bukkit.entity.Pillager.class, org.bukkit.entity.Evoker.class, org.bukkit.entity.Vindicator.class, Vex.class, Silverfish.class, Endermite.class, org.bukkit.entity.Creeper.class, org.bukkit.entity.CaveSpider.class, Stray.class);

    public static final List<Class<?>> ATTACKER_ENTITY_COUNT_CLASSES = List.of(org.bukkit.entity.Zombie.class, org.bukkit.entity.Skeleton.class, org.bukkit.entity.Blaze.class, org.bukkit.entity.Illusioner.class, org.bukkit.entity.Witch.class, org.bukkit.entity.Pillager.class, org.bukkit.entity.Evoker.class, org.bukkit.entity.Vindicator.class, org.bukkit.entity.Creeper.class, org.bukkit.entity.CaveSpider.class, Stray.class);

    public static final List<EntityType> ATTACKER_ENTITY_TYPES = List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.ILLUSIONER, EntityType.WITCH, EntityType.PILLAGER, EntityType.EVOKER, EntityType.VINDICATOR, EntityType.VEX, EntityType.SILVERFISH, EntityType.ENDERMITE, EntityType.CREEPER, EntityType.CAVE_SPIDER, EntityType.STRAY);

    public static final List<EntityClass<? extends EntityInsentient>> FIRST_DEFENDER_CLASSES = List.of(EntityClass.EntitySheep);

    public static final List<EntityClass<? extends EntityInsentient>> SECOND_DEFENDER_CLASSES = List.of(EntityClass.EntityIronGolem, EntityClass.EntityVillager, EntityClass.EntityWolf);

    public enum Type {
        MELEE, BREAKER, RANGED, OTHER, BOSS
    }

    static ExPathfinderGoal getCorePathfinder(HeightMapManager.MapType mapType, double speed, ExPathfinderGoal breakPathfinder, int breakLevel) {

        return new ExHeightPathfinder(MobDefServer.getMap().getHeightMapManager().getMap(mapType), 3, speed, 32, 0, breakPathfinder, breakLevel);
    }

    static ExPathfinderGoalBreakBlock getBreakPathfinder(double speed, boolean ignoreTarget, Collection<Material> breakable) {
        return new ExPathfinderGoalBreakBlock(speed, ignoreTarget, (block) -> MobDefServer.getMap().getHeightMapManager().updateMaps(), breakable);
    }

    public static MobDefMob<?> getRandomMob(int wave, MobDefMob.Type type, ExLocation spawn) {
        Random random = new Random();
        int r;
        MobDefMob<?> mob = null;
        switch (type) {
            case MELEE:
                r = random.nextInt(10);
                switch (r) {
                    case 3:
                    case 4:
                        mob = new Vindicator(spawn, wave);
                        break;
                    case 5:
                        mob = new FollowerZombie(spawn, wave);
                        break;
                    case 6:
                        mob = new CaveSpider(spawn, wave);
                        break;
                    default:
                        mob = new Zombie(spawn, wave);
                }
                break;
            case RANGED:
                r = random.nextInt(10);
                switch (r) {
                    case 0:
                    case 1:
                    case 2:
                        mob = new Pillager(spawn, wave);
                        break;
                    case 9:
                        mob = new FollowerSkeleton(spawn, wave);
                        break;
                    default:
                        mob = new Skeleton(spawn, wave);
                }
                break;
            case BREAKER:
                r = random.nextInt(3);
                switch (r) {
                    case 0:
                    case 1:
                        mob = new ZombieBreaker(spawn, wave);
                        break;
                    case 2:
                        mob = new Creeper(spawn, wave);
                        break;
                }
                break;
            case OTHER:
                r = random.nextInt(4);
                switch (r) {
                    case 0:
                        mob = new BabyZombie(spawn, wave);
                        break;
                    case 1:
                        mob = new Witch(spawn, wave);
                        break;
                    case 2:
                        mob = new Evoker(spawn, wave);
                        break;
                    case 3:
                        mob = new CaveSpider(spawn, wave);
                        break;
                }
                break;
        }

        if (mob != null && mob.getWave() <= wave) {
            return mob;
        }
        return getRandomMob(wave, type, spawn);
    }

    protected M entity;

    protected List<EntityExtension<?>> subEntities = new ArrayList<>();

    protected final Type type;
    protected final HeightMapManager.MapType mapType;
    protected final int wave;

    protected final int currentWave;
    protected final ExLocation spawn;

    protected final Random random = new Random();


    MobDefMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn, int currentWave) {
        this.type = type;
        this.mapType = mapType;
        this.wave = wave;
        this.currentWave = currentWave;
        this.spawn = spawn;
    }

    public M getEntity() {
        return entity;
    }

    public Type getType() {
        return type;
    }

    public ExLocation getSpawn() {
        return spawn;
    }

    public HeightMapManager.MapType getMapType() {
        return mapType;
    }

    public int getWave() {
        return wave;
    }

    public void spawn() {
        this.entity.setPersistent(true);
        this.entity.getExtension().setPositionRotation(this.spawn.getX(), this.spawn.getY(), this.spawn.getZ(), this.spawn.getYaw(), this.spawn.getPitch());
        EntityManager.spawnEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), this.entity);

        this.entity.getExtension().setMaxNoDamageTicks(1);

        for (EntityExtension<?> subEntity : this.subEntities) {
            subEntity.getExtension().setPersistent(true);
            subEntity.getExtension().setPositionRotation(this.spawn.getX(), this.spawn.getY(), this.spawn.getZ(), this.spawn.getYaw(), this.spawn.getPitch());
            EntityManager.spawnExEntity(MobDefServer.getMap().getWorld().getBukkitWorld(), subEntity);
        }

    }
}
