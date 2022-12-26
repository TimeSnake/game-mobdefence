/*
 * Copyright (C) 2022 timesnake
 */

package de.timesnake.game.mobdefence.special.weapon;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class Trident extends SpecialWeapon {

    public static final ExItemStack ITEM = new ExItemStack(Material.TRIDENT).addExEnchantment(Enchantment.LOYALTY, 3);

    public Trident() {
        super(ITEM);
    }
}
