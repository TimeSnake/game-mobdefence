/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.ExplosionManager;
import de.timesnake.library.entities.entity.bukkit.ExZombie;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.entity.extension.Mob;
import de.timesnake.library.entities.entity.extension.Monster;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalZombieAttack;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalBreakBlock;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

public class ZombieBreaker extends ArmorMob<ExZombie> {

  public ZombieBreaker(ExLocation spawn, int currentWave) {
    super(Type.MELEE, HeightMapManager.MapType.WALL_FINDER, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    World world = MobDefServer.getMap().getWorld().getBukkitWorld();

    this.entity = new ExZombie(world, false, false);

    ExCustomPathfinderGoalBreakBlock breakBlock = getBreakPathfinder(0.7, true,
        ExplosionManager.EXPLODEABLE);

    double speed = this.currentWave > 6 ? 1.3 : 1.2;

    this.entity.addPathfinderGoal(1, breakBlock);
    this.entity.addPathfinderGoal(2,
        getCorePathfinder(this.getMapType(), speed, breakBlock, 5));
    this.entity.addPathfinderGoal(2, new ExPathfinderGoalZombieAttack(speed, false));
    this.entity.addPathfinderGoal(3, new ExPathfinderGoalRandomStrollLand(speed));
    this.entity.addPathfinderGoal(4, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 8.0F));
    this.entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

    this.entity.addPathfinderGoal(1, new ExPathfinderGoalHurtByTarget(Monster.class));

    for (Class<? extends Mob> entityClass : MobDefMob.FIRST_DEFENDER_CLASSES) {
      this.entity.addPathfinderGoal(2,
          new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
              true, 8D));
    }
    this.entity.addPathfinderGoal(3,
        new ExCustomPathfinderGoalNearestAttackableTarget(HumanEntity.class,
            true,
            true, 8D));

    for (Class<? extends Mob> entityClass : MobDefMob.SECOND_DEFENDER_CLASSES) {
      this.entity.addPathfinderGoal(3,
          new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, true,
              true, 8D));
    }

    entity.setSlot(ExEnumItemSlot.MAIN_HAND, new ItemStack(Material.IRON_PICKAXE));

    if (this.currentWave > 11) {
      this.entity.setMaxHealth(this.currentWave * 5);
      this.entity.setHealth(this.currentWave * 5);
    } else if (this.currentWave > 6) {
      this.entity.setMaxHealth(40);
      this.entity.setHealth(40);
    }

    this.entity.getBukkitAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.3);

    this.entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
        .setBaseValue(2 + this.currentWave / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);

    super.spawn();
  }
}
