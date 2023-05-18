/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.entity.Blaze;
import de.timesnake.game.mobdefence.special.entity.DogSpawner;
import de.timesnake.game.mobdefence.special.entity.IronGolem;
import de.timesnake.game.mobdefence.special.entity.SheepSpawner;
import de.timesnake.game.mobdefence.special.entity.Snowman;
import de.timesnake.game.mobdefence.special.weapon.bullet.BulletManager;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class WeaponManager implements Listener {

  private final BulletManager bulletManager;

  private final HashMap<ExItemStack, SpecialWeapon> weaponByItem = new HashMap<>();

  private final Iceball iceballGenerator;

  public WeaponManager() {
    this.bulletManager = new BulletManager();

    this.iceballGenerator = new Iceball();

    this.addWeapon(new BoomerangAxe());
    this.addWeapon(new FireStaff());
    this.addWeapon(new SplashBow());
    this.addWeapon(new Wand());
    this.addWeapon(new IronGolem());
    this.addWeapon(new FireHoe());
    this.addWeapon(new RocketCrossBow());
    this.addWeapon(new WaterBottle());
    this.addWeapon(new Sword());
    this.addWeapon(new Snowman());
    this.addWeapon(new Blaze());
    this.addWeapon(new PoisonArrow());
    this.addWeapon(new SheepSpawner());
    this.addWeapon(new DogSpawner());
    this.addWeapon(new LumberAxe());
    this.addWeapon(new SwingSword());
    this.addWeapon(new PotionBow());
    this.addWeapon(this.iceballGenerator);
    this.addWeapon(new SafeSphere());

    Server.registerListener(this, GameMobDefence.getPlugin());
  }

  private void addWeapon(SpecialWeapon weapon) {
    this.weaponByItem.put(weapon.getItem(), weapon);
  }

  public BulletManager getBulletManager() {
    return bulletManager;
  }
}
