package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExSkeleton;
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

import java.util.List;

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

        this.entity = new ExSkeleton(world, false);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(0.9D));
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


        if (this.currentWave < 3) {
            switch (this.random.nextInt(8)) {
                case 0:
                case 1:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE), List.of(2)));
                    entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 30.0F));
                    break;
                default:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.BOW));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 15.0F));

            }
        } else if (this.currentWave < 11) {
            switch (this.random.nextInt(this.currentWave < 7 ? 15 : 10)) {
                case 0:
                case 1:
                case 2:
                case 5:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE), List.of(4)));
                    entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 30.0F));
                    break;
                case 3:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_FIRE), List.of(1)));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 15.0F));
                    break;
                default:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE), List.of(2)));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 15.0F));
                    break;

            }
        } else {
            switch (this.random.nextInt(8)) {
                case 0:
                case 1:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_FIRE), List.of(1)));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.1, 15.0F));
                    break;
                default:
                    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE), List.of(this.currentWave / 4)));
                    entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
                    this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 30.0F));
                    break;

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

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER);
    }
}
