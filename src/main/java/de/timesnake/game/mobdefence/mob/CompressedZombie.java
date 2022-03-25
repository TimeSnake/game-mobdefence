package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExZombie;
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

public class CompressedZombie extends MobDefMob<ExZombie> {

    public CompressedZombie(ExLocation spawn, int currentWave) {
        super(Type.COMPRESSED_MELEE, HeightMapManager.MapType.NORMAL, 0, spawn, currentWave);
    }

    @Override
    public void spawn() {
        this.init();
        super.spawn();
    }

    public void init() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        this.entity = new ExZombie(world, false);

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.8, false, BlockCheck.BREAKABLE_MATERIALS);

        double speed;
        if (this.currentWave > 13) speed = 1.3;
        else if (this.currentWave > 10) speed = 1.2;
        else if (this.currentWave > 5) speed = 1.1;
        else speed = 1;

        double random = Math.random();

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalZombieAttack(speed));
        this.entity.addPathfinderGoal(2, getCorePathfinder(this.getMapType(), speed, breakBlock, BREAK_LEVEL));

        this.entity.addPathfinderGoal(2, breakBlock);
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(speed));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalRandomLookaround());

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman, true,
                true, 1D));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass, true, true, 16D));
        }

        if (this.currentWave > 11) {
            this.entity.setMaxHealth(this.currentWave * 20);
            this.entity.setHealth(this.currentWave * 20);
        } else if (this.currentWave > 6) {
            this.entity.setMaxHealth(160);
            this.entity.setHealth(160);
        }

        this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2 + this.currentWave / 5. * MobManager.MOB_DAMAGE_MULTIPLIER * 2);

        this.entity.setSlot(ExEnumItemSlot.HEAD, new ExItemStack(Material.NETHERITE_HELMET));
        this.entity.setSlot(ExEnumItemSlot.CHEST, new ExItemStack(Material.NETHERITE_CHESTPLATE));
        this.entity.setSlot(ExEnumItemSlot.LEGS, new ExItemStack(Material.NETHERITE_LEGGINGS));
        this.entity.setSlot(ExEnumItemSlot.FEET, new ExItemStack(Material.NETHERITE_BOOTS));

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                new ExItemStack(Material.NETHERITE_SHOVEL).addExEnchantment(Enchantment.DAMAGE_ALL, 10));

    }
}
