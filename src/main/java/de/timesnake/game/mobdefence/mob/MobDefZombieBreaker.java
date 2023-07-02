/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.bukkit.util.world.ExWorld;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.ExplosionManager;
import de.timesnake.library.entities.entity.ZombieBuilder;
import de.timesnake.library.entities.pathfinder.BreakBlockGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;

public class MobDefZombieBreaker extends ArmorMob<Zombie> {

  public MobDefZombieBreaker(ExLocation spawn, int currentWave) {
    super(Type.MELEE, HeightMapManager.MapType.WALL_FINDER, 0, spawn, currentWave);
  }

  @Override
  public void spawn() {
    ExWorld world = MobDefServer.getMap().getWorld();

    float health = 20;

    if (this.currentWave > 11) {
      health = this.currentWave * 5;
    } else if (this.currentWave > 6) {
      health = 40;
    }

    this.entity = new ZombieBuilder(world.getHandle(), false, false)
        .setMaxHealthAndHealth(health)
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
            .setBaseValue(2 + (this.currentWave - this.wave) / 5. * MobManager.MOB_DAMAGE_MULTIPLIER))
        .applyOnEntity(e -> e.getBukkitCreature().getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.3))
        .applyOnEntity(e -> e.setItemSlot(EquipmentSlot.MAINHAND, new ExItemStack(Material.IRON_PICKAXE).getHandle()))
        .apply(b -> {
          BreakBlockGoal breakBlock = getBreakPathfinder(b.getNMS(), 0.7, true,
              ExplosionManager.EXPLODEABLE);

          b.addPathfinderGoal(4, e -> getCorePathfinder(e, this.getMapType(), this.currentWave > 6 ? 1.3 : 1.2, breakBlock, 5));
          b.addPathfinderGoal(4, e -> breakBlock);
        })
        .addPathfinderGoal(2, e -> new ZombieAttackGoal(e, this.currentWave > 6 ? 1.3 : 1.2, false))
        .addPathfinderGoal(3, e -> new RandomStrollGoal(e, 1.2))
        .addPathfinderGoal(4, e -> new LookAtPlayerGoal(e, Player.class, 8.0F))
        .addPathfinderGoal(4, e -> new RandomLookAroundGoal(e))
        .apply(this::applyDefaultTargetGoals)
        .build();

    super.spawn();
  }
}
