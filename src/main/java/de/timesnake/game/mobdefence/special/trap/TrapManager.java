/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.trap;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.world.ExBlock;
import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TrapManager implements Listener {

  public static final HashMap<ExItemStack, Trap> TRAPS_BY_ITEM = new HashMap<>();

  public static final Set<Material> PRESSURE_TYPES = Tag.PRESSURE_PLATES.getValues();

  public static final Set<Material> TRAP_MATERIALS = new HashSet<>();

  static {
    TRAP_MATERIALS.addAll(Tag.PRESSURE_PLATES.getValues());
    TRAP_MATERIALS.addAll(Tag.BUTTONS.getValues());
    TRAP_MATERIALS.add(Material.DISPENSER);
  }

  private final Set<Trap> pressureTraps = new HashSet<>();
  private final HashMap<ExLocation, RangedTrap> rangedTrapsByLocation = new HashMap<>();

  private BukkitTask rangedTrapTask;

  public TrapManager() {
    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  public void start() {
    this.runRangedTrapTrigger();
  }

  public void reset() {
    if (this.rangedTrapTask != null) {
      this.rangedTrapTask.cancel();
    }

    this.pressureTraps.clear();
    this.rangedTrapsByLocation.clear();
  }

  public void addRessureTrap(Trap trap) {
    this.pressureTraps.add(trap);
  }

  public void addRangedTrap(RangedTrap trap) {
    this.rangedTrapsByLocation.put(trap.getLocation(), trap);
  }

  @EventHandler
  public void onUserBlockPlace(UserBlockPlaceEvent e) {
    if (e.isCancelled()) {
      return;
    }

    if (!TRAP_MATERIALS.contains(e.getBlockPlaced().getType())) {
      return;
    }

    ExItemStack item = new ExItemStack(e.getItemInHand());
    ExBlock block = new ExBlock(e.getBlockPlaced());

    for (TrapMaker trapMaker : TrapMaker.values()) {
      if (trapMaker.getItem().equals(item)) {
        Trap trap = trapMaker.newInstance(block);

        if (trap instanceof RangedTrap) {
          this.rangedTrapsByLocation.put(trap.getLocation(), ((RangedTrap) trap));
        } else {
          this.pressureTraps.add(trap);
        }
      }
    }
  }

  private void runRangedTrapTrigger() {
    this.rangedTrapTask = Server.runTaskTimerSynchrony(() -> {
      Set<ExLocation> triggeredTraps = new HashSet<>();
      for (Map.Entry<ExLocation, RangedTrap> entry : this.rangedTrapsByLocation.entrySet()) {
        ExLocation loc = entry.getKey();
        RangedTrap trap = entry.getValue();

        Collection<LivingEntity> entities = ((Collection) loc.getWorld()
            .getNearbyEntities(loc,
                trap.getRange(), 1, trap.getRange(),
                (e) -> MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getType())));

        if (entities.size() >= trap.getMobAmount()) {
          if (trap.trigger(entities)) {
            loc.getBlock().setType(Material.AIR);
            triggeredTraps.add(loc);
          }
        }
      }

      for (ExLocation loc : triggeredTraps) {
        this.rangedTrapsByLocation.remove(loc);
      }

    }, 0, 20, GameMobDefence.getPlugin());
  }


  @EventHandler
  public void onPressurePlate(EntityInteractEvent e) {
    if (!PRESSURE_TYPES.contains(e.getBlock().getType())) {
      return;
    }

    if (e.getEntityType().equals(EntityType.PLAYER)
        || !(e.getEntity() instanceof LivingEntity)) {
      return;
    }

    ExBlock block = new ExBlock(e.getBlock());

    Set<Trap> triggeredTraps = new HashSet<>();

    for (Trap trap : this.pressureTraps) {
      if (trap.getBlock().equals(block)) {
        if (trap.trigger(List.of(((LivingEntity) e.getEntity())))) {
          block.getBlock().setType(Material.AIR);
          triggeredTraps.add(trap);
        }
      }
    }

    for (Trap trap : triggeredTraps) {
      this.pressureTraps.remove(trap);
    }
  }
}
