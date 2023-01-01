/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExIllusioner;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalBowShoot;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStroll;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalIllagerIllusionerCastSpellBlindness;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalIllagerIllusionerCastSpellDisapear;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalIllagerWizardNoneSpell;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;

public class Illusioner extends MobDefMob<ExIllusioner> {

    public Illusioner(ExLocation spawn, int currentWave) {
        super(Type.BOSS, HeightMapManager.MapType.NORMAL, 5, spawn, currentWave);
    }

    @Override
    public void spawn() {
        World world = MobDefServer.getMap().getWorld().getBukkitWorld();

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false,
                BlockCheck.BREAKABLE_MATERIALS);

        this.entity = new ExIllusioner(world, false, false);

        this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalIllagerWizardNoneSpell());
        this.entity.addPathfinderGoal(4,
                new ExCustomPathfinderGoalIllagerIllusionerCastSpellDisapear());
        this.entity.addPathfinderGoal(5,
                new ExCustomPathfinderGoalIllagerIllusionerCastSpellBlindness());
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalBowShoot(0.5D, 20, 15.0F));
        this.entity.addPathfinderGoal(7,
                getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(8, new ExPathfinderGoalRandomStroll(0.6D));
        this.entity.addPathfinderGoal(9,
                new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 3.0F, 1.0F));
        this.entity.addPathfinderGoal(10, new ExPathfinderGoalLookAtPlayer(Mob.class, 8.0F));

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

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                new ExItemStack(Material.CROSSBOW).addExEnchantment(Enchantment.QUICK_CHARGE, 3)
                        .addExEnchantment(Enchantment.ARROW_DAMAGE, 2));

        this.entity.setMaxHealth(80);
        this.entity.setHealth(80);

        super.spawn();
    }
}
