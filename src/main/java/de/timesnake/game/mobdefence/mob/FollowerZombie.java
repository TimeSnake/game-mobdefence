/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.Endermite;
import de.timesnake.library.entities.entity.bukkit.ExEndermite;
import de.timesnake.library.entities.entity.bukkit.ExSilverfish;
import de.timesnake.library.entities.entity.bukkit.ExZombie;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.bukkit.Silverfish;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalMeleeAttack;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalZombieAttack;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalFollowEntity;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalSpawnArmy;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

public class FollowerZombie extends ArmorMob<ExZombie> {

    public FollowerZombie(ExLocation spawn, int currentWave) {
        super(Type.MELEE, HeightMapManager.MapType.NORMAL, 6, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false, false);

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.5, false,
                BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(1, false));
        this.entity.addPathfinderGoal(2,
                getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(1));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2,
                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                            true, 16D));
        }
        this.entity.addPathfinderGoal(3,
                new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

        for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3,
                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
                            true, 16D));
        }

        int random = this.random.nextInt(2);

        if (random == 0) {
            this.entity.addPathfinderGoal(2,
                    new ExCustomPathfinderGoalSpawnArmy(Silverfish.class, 3,
                            5 * 20) {
                        @Override
                        public List<? extends Monster> getArmee(Mob entity) {
                            World world = MobDefServer.getMap().getWorld().getBukkitWorld();

                            List<Monster> fishs = new ArrayList<>();

                            for (int i = 0; i < 6; i++) {
                                ExSilverfish fish = new ExSilverfish(world, false, false);

                                fish.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(1.2));
                                fish.addPathfinderGoal(2,
                                        new ExCustomPathfinderGoalFollowEntity(entity, 1.1f, 5,
                                                20));
                                fish.addPathfinderGoal(3,
                                        getCorePathfinder(HeightMapManager.MapType.NORMAL, 1, null,
                                                BREAK_LEVEL));

                                fish.addPathfinderGoal(1,
                                        new ExPathfinderGoalHurtByTarget(Monster.class));

                                for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                                    fish.addPathfinderGoal(2,
                                            new ExCustomPathfinderGoalNearestAttackableTarget(
                                                    entityClass));
                                }
                                fish.addPathfinderGoal(3,
                                        new ExCustomPathfinderGoalNearestAttackableTarget(
                                                HumanEntity.class));

                                for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                                    fish.addPathfinderGoal(3,
                                            new ExCustomPathfinderGoalNearestAttackableTarget(
                                                    entityClass));
                                }

                                fish.setMaxNoDamageTicks(1);

                                fishs.add(fish);
                            }

                            return fishs;
                        }
                    });

            this.subEntities = this.getSilverFishs();
        } else if (random == 1) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalSpawnArmy(Endermite.class, 3,
                    5 * 20) {
                @Override
                public List<? extends Monster> getArmee(
                        de.timesnake.library.entities.entity.extension.Mob entity) {
                    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

                    List<ExEndermite> mites = new ArrayList<>();

                    for (int i = 0; i < 8; i++) {
                        ExEndermite mite = new ExEndermite(world, false, false);

                        mite.addPathfinderGoal(1, new ExPathfinderGoalFloat());
                        mite.addPathfinderGoal(2, new ExPathfinderGoalMeleeAttack(1.2));
                        mite.addPathfinderGoal(3,
                                getCorePathfinder(HeightMapManager.MapType.NORMAL, 1, null,
                                        BREAK_LEVEL));

                        mite.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

                        for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                            mite.addPathfinderGoal(2,
                                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
                        }
                        mite.addPathfinderGoal(3,
                                new ExCustomPathfinderGoalNearestAttackableTarget(
                                        HumanEntity.class));

                        for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                            mite.addPathfinderGoal(3,
                                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
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

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(
                2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

        super.spawn();
    }

    @Override
    public void equipArmor() {
        super.equipArmor();
        this.entity.setSlot(ExEnumItemSlot.HEAD,
                new ExItemStack(Material.GOLDEN_HELMET).addExEnchantment(
                        Enchantment.PROTECTION_ENVIRONMENTAL, 5));
    }

    private List<Monster> getSilverFishs() {
        List<Monster> fishs = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ExSilverfish fish = new ExSilverfish(this.entity.getWorld(), false, false);

            fish.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(2.0D));
            fish.addPathfinderGoal(2,
                    new ExCustomPathfinderGoalFollowEntity(this.entity, 1.1f, 5, 20));
            fish.addPathfinderGoal(3, getCorePathfinder(this.getMapType(), 1, null, BREAK_LEVEL));

            fish.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

            for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                fish.addPathfinderGoal(2,
                        new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }
            fish.addPathfinderGoal(3,
                    new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

            for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                fish.addPathfinderGoal(3,
                        new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }

            fish.setMaxNoDamageTicks(1);

            fishs.add(fish);
        }

        return fishs;
    }

    private List<Monster> getEnderMites() {
        List<Monster> mites = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            ExEndermite mite = new ExEndermite(this.entity.getWorld(), false, false);

            mite.addPathfinderGoal(1, new ExPathfinderGoalMeleeAttack(2.0D));
            mite.addPathfinderGoal(2,
                    new ExCustomPathfinderGoalFollowEntity(this.entity, 1.1f, 5, 20));
            mite.addPathfinderGoal(3, getCorePathfinder(this.getMapType(), 1, null, BREAK_LEVEL));

            mite.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

            for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                mite.addPathfinderGoal(2,
                        new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }
            mite.addPathfinderGoal(3,
                    new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

            for (Class<? extends de.timesnake.library.entities.entity.extension.Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                mite.addPathfinderGoal(3,
                        new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
            }

            mite.setMaxNoDamageTicks(1);

            mites.add(mite);
        }

        return mites;
    }
}
