package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExZombie;
import de.timesnake.basic.entities.entity.extension.EntityExtension;
import de.timesnake.basic.entities.entity.extension.ExEntityInsentient;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BossZombie extends MobDefMob<ExZombie> {

    public BossZombie(ExLocation spawn, int currentWave) {
        super(Type.MELEE, HeightMapManager.MapType.NORMAL, 5, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(1.1));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.8));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }

        this.entity.addPathfinderGoal(2, new ExPathfinderGoalSpawnArmy(EntityClass.EntityZombie, 4, 10 * 20) {
            @Override
            public List<? extends EntityExtension<? extends ExEntityInsentient>> getArmee(EntityExtension<?
                    extends ExEntityInsentient> entity) {
                List<ExZombie> zombies = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    Zombie zombie = new Zombie(BossZombie.this.spawn, BossZombie.this.currentWave);

                    zombie.init();
                    zombie.equipArmor();
                    zombie.equipWeapon();
                    zombie.getEntity().setMaxNoDamageTicks(1);

                    zombies.add(zombie.getEntity());
                }

                return zombies;
            }
        });

        this.entity.setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.GOLDEN_HELMET));
        this.entity.setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.GOLDEN_CHESTPLATE));
        this.entity.setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.GOLDEN_LEGGINGS));
        this.entity.setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.GOLDEN_BOOTS));

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.GOLDEN_AXE).addExEnchantment(Enchantment.FIRE_ASPECT, 2));

        this.entity.getBukkitAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(10);
        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(5);
        this.entity.getBukkitAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(5);
        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(Math.sqrt(this.currentWave) * 6);
        this.entity.getBukkitAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.2);

        this.entity.setMaxHealth(this.currentWave * 100);
        this.entity.setHealth(this.currentWave * 100);

        super.spawn();
    }

}
