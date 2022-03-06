package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.bukkit.ExIllagerIllusioner;
import de.timesnake.basic.entities.pathfinder.*;
import de.timesnake.basic.entities.wrapper.EntityClass;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;

public class Illusioner extends MobDefMob<ExIllagerIllusioner> {

    public Illusioner(ExLocation spawn, int currentWave) {
        super(Type.BOSS, HeightMapManager.MapType.NORMAL, 5, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        ExPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity = new ExIllagerIllusioner(world, false);

        this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, new ExPathfinderGoalIllagerWizardNoneSpell());
        this.entity.addPathfinderGoal(4, new ExPathfinderGoalIllagerIllusionerCastSpellDisapear());
        this.entity.addPathfinderGoal(5, new ExPathfinderGoalIllagerIllusionerCastSpellBlindness());
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalBowShoot(0.5D, 20, 15.0F));
        this.entity.addPathfinderGoal(7, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(8, new ExPathfinderGoalRandomStroll(0.6D));
        this.entity.addPathfinderGoal(9, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(10, new ExPathfinderGoalLookAtPlayer(EntityClass.EntityInsentient));

        this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExPathfinderGoalNearestAttackableTarget(entityClass));
        }

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ExItemStack(Material.CROSSBOW).addExEnchantment(Enchantment.QUICK_CHARGE, 3).addExEnchantment(Enchantment.ARROW_DAMAGE, 2));

        this.entity.setMaxHealth(80);
        this.entity.setHealth(80);

        super.spawn();
    }
}
