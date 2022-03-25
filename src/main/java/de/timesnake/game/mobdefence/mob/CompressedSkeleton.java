package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExSkeleton;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.basic.entities.wrapper.ExMobEffects;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class CompressedSkeleton extends MobDefMob<ExSkeleton> {

    public CompressedSkeleton(ExLocation spawn, int currentWave) {
        super(Type.COMBRESSED_RANGED, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {
        this.init();
        super.spawn();
    }

    public void init() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExSkeleton(world, false, ExMobEffects.POISON, 8 * 20, 2);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1.2, breakBlock, BREAK_LEVEL));
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
            entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE),
                    List.of(2)));
            entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
        } else if (this.currentWave < 11) {
            entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE),
                    List.of(4)));
            entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
        } else {
            entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW, List.of(Enchantment.ARROW_DAMAGE),
                    List.of(this.currentWave / 4)));
            entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
        }

        if (this.currentWave > 11) {
            this.entity.setMaxHealth(this.currentWave * 20);
            this.entity.setHealth(this.currentWave * 20);
        } else if (this.currentWave > 6) {
            this.entity.setMaxHealth(160);
            this.entity.setHealth(160);
        }

        this.entity.setSlot(ExEnumItemSlot.HEAD,
                new ExItemStack(Material.LEATHER_HELMET, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
        this.entity.setSlot(ExEnumItemSlot.CHEST,
                new ExItemStack(Material.LEATHER_CHESTPLATE, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
        this.entity.setSlot(ExEnumItemSlot.LEGS,
                new ExItemStack(Material.LEATHER_LEGGINGS, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
        this.entity.setSlot(ExEnumItemSlot.FEET,
                new ExItemStack(Material.LEATHER_BOOTS, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER);
    }
}
