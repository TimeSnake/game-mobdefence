/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.library.entities.entity.ZombieBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.attribute.Attribute;

public class MobDefBabyZombie extends ArmorMob<Zombie> {

  public MobDefBabyZombie(ExLocation spawn, int currentWave) {
    super(Type.OTHER, HeightMapManager.MapType.NORMAL, 1, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    this.entity = new ZombieBuilder(world.getHandle(), false, false, false)
        .setMaxHealth(30)
        .applyOnEntity(e -> {
          e.setBaby(true);
          if (this.currentWave > 13) {
            e.setHealth(30);
          }
          e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
              .setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER);
        })
        .addPathfinderGoal(1, e -> new ZombieAttackGoal(e, this.currentWave < 15 ? 1.2 : 1.3, false))
        .apply(b -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(b.getNMS(), 0.3, false,
              BlockCheck.BREAKABLE_MATERIALS);

          b.addPathfinderGoal(2, e -> getCorePathfinder(e, this.getMapType(), 1.2, breakBlock, BREAK_LEVEL));
          b.addPathfinderGoal(2, e -> breakBlock);
        })
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 1.2))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build();


    super.spawn();
  }
}