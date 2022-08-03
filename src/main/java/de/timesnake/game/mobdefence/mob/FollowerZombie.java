package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.ExtendedCraftEntity;
import de.timesnake.library.entities.entity.bukkit.ExEndermite;
import de.timesnake.library.entities.entity.bukkit.ExSilverfish;
import de.timesnake.library.entities.entity.bukkit.ExZombie;
import de.timesnake.library.entities.entity.extension.ExEntityInsentient;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMeleeAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalZombieAttack;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class FollowerZombie extends ArmorMob<ExZombie> {

    public FollowerZombie(ExLocation spawn, int currentWave) {
        super(Type.MELEE, HeightMapManager.MapType.NORMAL, 6, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false);

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.5, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(1, false));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalRandomStrollLand(1));
        this.entity.addPathfinderGoal(4, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                    true, 16D));
        }

        int random = this.random.nextInt(2);

        if (random == 0) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalSpawnArmy(EntityClass.EntitySilverfish, 3,
                    5 * 20) {
                @Override
                public List<? extends ExtendedCraftEntity<? extends ExEntityInsentient>> getArmee(ExtendedCraftEntity<?
                        extends ExEntityInsentient> entity) {
                    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

                    List<ExtendedCraftEntity<? extends ExEntityInsentient>> fishs = new ArrayList<>();

                    for (int i = 0; i < 6; i++) {
                        ExSilverfish fish = new ExSilverfish(world, false);

                        fish.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(1.2));
                        fish.addPathfinderGoal(2, new ExCustomPathfinderGoalFollowEntity(entity, 1.1f, 5, 20));
                        fish.addPathfinderGoal(3, getCorePathfinder(HeightMapManager.MapType.NORMAL, 1, null,
                                BREAK_LEVEL));

                        fish.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

                        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                            fish.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
                        }
                        fish.addPathfinderGoal(3,
                                new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

                        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                            fish.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
                        }

                        fish.setMaxNoDamageTicks(1);

                        fishs.add(fish);
                    }

                    return fishs;
                }
            });

            this.subEntities = this.getSilverFishs();
        } else if (random == 1) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalSpawnArmy(EntityClass.EntityEndermite, 3,
                    5 * 20) {
                @Override
                public List<? extends ExtendedCraftEntity<? extends ExEntityInsentient>> getArmee(ExtendedCraftEntity<?
                        extends ExEntityInsentient> entity) {
                    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

                    List<ExEndermite> mites = new ArrayList<>();

                    for (int i = 0; i < 8; i++) {
                        ExEndermite mite = new ExEndermite(world, false);

                        mite.addPathfinderGoal(1, new ExPathfinderGoalFloat());
                        mite.addPathfinderGoal(2, new ExPathfinderGoalMeleeAttack(1.2));
                        mite.addPathfinderGoal(3, getCorePathfinder(HeightMapManager.MapType.NORMAL, 1, null,
                                BREAK_LEVEL));

                        mite.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

                        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                            mite.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
                        }
                        mite.addPathfinderGoal(3,
                                new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

                        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                            mite.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
                        }

                        mite.setMaxNoDamageTicks(1);

                        mites.add(mite);
                    }

                    return mites;
                }
            });

            this.subEntities = this.getEnderMites();
        }

        if (this.currentWave > 16) {
            this.entity.setMaxHealth(this.currentWave * 5);
        } else if (this.currentWave > 12) {
            this.entity.setMaxHealth(60);
            this.entity.setHealth(60);
        } else if (this.currentWave > 6) {
            this.entity.setMaxHealth(40);
            this.entity.setHealth(40);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

        super.spawn();
    }

    @Override
    public void equipArmor() {
        super.equipArmor();
        this.entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.GOLDEN_HELMET).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
    }

    private List<ExtendedCraftEntity<?>> getSilverFishs() {
        List<ExtendedCraftEntity<?>> fishs = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ExSilverfish fish = new ExSilverfish(this.entity.getWorld(), false);

            fish.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(2.0D));
            fish.addPathfinderGoal(2, new ExCustomPathfinderGoalFollowEntity(this.entity, 1.1f, 5, 20));
            fish.addPathfinderGoal(3, getCorePathfinder(this.getMapType(), 1, null, BREAK_LEVEL));

            fish.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

            for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                fish.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }
            fish.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

            for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                fish.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }

            fish.setMaxNoDamageTicks(1);

            fishs.add(fish);
        }

        return fishs;
    }

    private List<ExtendedCraftEntity<?>> getEnderMites() {
        List<ExtendedCraftEntity<?>> mites = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ExEndermite mite = new ExEndermite(this.entity.getWorld(), false);

            mite.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(2.0D));
            mite.addPathfinderGoal(2, new ExCustomPathfinderGoalFollowEntity(this.entity, 1.1f, 5, 20));
            mite.addPathfinderGoal(3, getCorePathfinder(this.getMapType(), 1, null, BREAK_LEVEL));

            mite.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

            for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                mite.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }
            mite.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

            for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                mite.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }

            mite.setMaxNoDamageTicks(1);

            mites.add(mite);
        }

        return mites;
    }
}
