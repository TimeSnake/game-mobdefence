/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExSkeleton;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalBowShoot;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Skeleton extends ArmorMob<ExSkeleton> {

    public Skeleton(ExLocation spawn, int currentWave) {
        super(Type.RANGED, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {
        this.init();
        super.spawn();
    }

    public void init() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExSkeleton(world, false, false);

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false,
                BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2,
                getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.9D));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

        for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2,
                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3,
                new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class));

        for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3,
                    new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }

        if (this.currentWave < 3) {
            switch (this.random.nextInt(8)) {
                case 0, 1 -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                                    2));
                    entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 30.0F));
                }
                default -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.BOW));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 15.0F));
                }
            }
        } else if (this.currentWave < 11) {
            switch (this.random.nextInt(this.currentWave < 7 ? 15 : 10)) {
                case 0, 1, 2, 5 -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                                    4));
                    entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
                }
                case 3 -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_FIRE,
                                    1));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 10, 15.0F));
                }
                default -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                                    2));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 10, 15.0F));
                }
            }
        } else {
            switch (this.random.nextInt(8)) {
                case 0, 1 -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_FIRE,
                                    1));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 10, 15.0F));
                }
                default -> {
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                            new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                                    this.currentWave / 4));
                    entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
                }
            }
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

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER);
    }
}
