/*
 * game-mobdefence.main
 * Copyright (C) 2022 timesnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see <http://www.gnu.org/licenses/>.
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.bukkit.ExIllusioner;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalBowShoot;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.custom.*;
import de.timesnake.library.entities.wrapper.EntityClass;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import net.minecraft.world.entity.EntityInsentient;
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

        ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.4, false, BlockCheck.BREAKABLE_MATERIALS);

        this.entity = new ExIllusioner(world, false, false);

        this.entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalIllagerWizardNoneSpell());
        this.entity.addPathfinderGoal(4, new ExCustomPathfinderGoalIllagerIllusionerCastSpellDisapear());
        this.entity.addPathfinderGoal(5, new ExCustomPathfinderGoalIllagerIllusionerCastSpellBlindness());
        this.entity.addPathfinderGoal(6, new ExPathfinderGoalBowShoot(0.5D, 20, 15.0F));
        this.entity.addPathfinderGoal(7, getCorePathfinder(this.getMapType(), 1, breakBlock, BREAK_LEVEL));
        this.entity.addPathfinderGoal(8, new ExCustomPathfinderGoalRandomStroll(0.6D));
        this.entity.addPathfinderGoal(9, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        this.entity.addPathfinderGoal(10, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityInsentient));

        this.entity.addPathfinderGoal(1, new ExCustomPathfinderGoalHurtByTarget(EntityClass.EntityMonster));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(2, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }
        this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(EntityClass.EntityHuman));

        for (EntityClass<? extends EntityInsentient> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
            this.entity.addPathfinderGoal(3, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass));
        }

        this.entity.setSlot(ExEnumItemSlot.MAIN_HAND,
                new ExItemStack(Material.CROSSBOW).addExEnchantment(Enchantment.QUICK_CHARGE, 3).addExEnchantment(Enchantment.ARROW_DAMAGE, 2));

        this.entity.setMaxHealth(80);
        this.entity.setHealth(80);

        super.spawn();
    }
}
