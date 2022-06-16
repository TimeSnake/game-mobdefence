package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExStray;
import de.timesnake.library.entities.entity.extension.EntityExtension;
import de.timesnake.library.entities.entity.extension.ExEntityInsentient;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BossSkeletonStray extends MobDefMob<ExStray> {

    public BossSkeletonStray(ExLocation spawn, int currentWave) {
        super(Type.BOSS, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExStray(world, false);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1.3, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(1.3D));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));

        this.entity.addPathfinderGoal(2, new ExPathfinderGoalSpawnArmy(EntityClass.EntitySkeletonStray, 3, 10 * 20) {
            @Override
            public List<? extends EntityExtension<? extends ExEntityInsentient>> getArmee(EntityExtension<?
                    extends ExEntityInsentient> entity) {
                List<ExStray> skeletons = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

                    ExStray stray = new ExStray(world, false);

                    ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.5, false,
                            BlockCheck.BREAKABLE_MATERIALS);

                    stray.addPathfinderGoal(2, getCorePathfinder(HeightMapManager.MapType.NORMAL, 1, breakBlock,
                            BREAK_LEVEL));
                    stray.addPathfinderGoal(2, breakBlock);
                    stray.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.9D));
                    stray.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
                    stray.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

                    stray.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

                    for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
                        stray.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
                    }
                    stray.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

                    for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
                        stray.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
                    }

                    stray.setMaxNoDamageTicks(1);

                    if (BossSkeletonStray.this.currentWave > 16) {
                        stray.setMaxHealth(BossSkeletonStray.this.currentWave * 5);
                    } else if (BossSkeletonStray.this.currentWave > 12) {
                        stray.setMaxHealth(60);
                        stray.setHealth(60);
                    } else if (BossSkeletonStray.this.currentWave > 6) {
                        stray.setMaxHealth(40);
                        stray.setHealth(40);
                    }

                    stray.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + BossSkeletonStray.this.currentWave / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

                    stray.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.BOW));

                    skeletons.add(stray);
                }

                return skeletons;
            }
        });

        this.entity.setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.GOLDEN_HELMET));
        this.entity.setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.GOLDEN_CHESTPLATE));
        this.entity.setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.GOLDEN_LEGGINGS));
        this.entity.setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.GOLDEN_BOOTS));

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_FIRE, 2).addExEnchantment(Enchantment.ARROW_DAMAGE, 5).addExEnchantment(Enchantment.ARROW_KNOCKBACK, 4));

        this.entity.getBukkitAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
        this.entity.getBukkitAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);

        this.entity.setMaxHealth(this.currentWave * 70);
        this.entity.setHealth(this.currentWave * 70);

        super.spawn();
    }
}
