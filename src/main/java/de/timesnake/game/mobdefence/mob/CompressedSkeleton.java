package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExSkeleton;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalBowShoot;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import de.timesnake.library.entities.wrapper.ExMobEffects;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;

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

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), 1.2, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalRandomStrollLand(0.9D));
        this.entity.addPathfinderGoal(4, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }


        if (this.currentWave < 3) {
            entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                    2));
            entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
        } else if (this.currentWave < 11) {
            entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                    4));
            entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.TURTLE_HELMET));
            this.entity.addPathfinderGoal(1, new ExPathfinderGoalBowShoot(1.2, 10, 30.0F));
        } else {
            entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.BOW).addExEnchantment(Enchantment.ARROW_DAMAGE,
                    this.currentWave / 4));
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
                ExItemStack.getLeatherArmor(Material.LEATHER_HELMET, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
        this.entity.setSlot(ExEnumItemSlot.CHEST,
                ExItemStack.getLeatherArmor(Material.LEATHER_CHESTPLATE, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
        this.entity.setSlot(ExEnumItemSlot.LEGS,
                ExItemStack.getLeatherArmor(Material.LEATHER_LEGGINGS, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));
        this.entity.setSlot(ExEnumItemSlot.FEET,
                ExItemStack.getLeatherArmor(Material.LEATHER_BOOTS, Color.GREEN).addExEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5));

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5D * MobManager.MOB_DAMAGE_MULTIPLIER);
    }
}
