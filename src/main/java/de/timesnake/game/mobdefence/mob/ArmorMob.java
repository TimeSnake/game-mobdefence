package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.world.ExLocation;
import de.timesnake.basic.entities.entity.extension.EntityExtension;
import de.timesnake.basic.entities.entity.extension.ExEntityInsentient;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.library.reflection.wrapper.ExEnumItemSlot;
import org.bukkit.Material;
import org.bukkit.entity.Mob;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorMob<M extends Mob & EntityExtension<? extends ExEntityInsentient>> extends MobDefMob<M> {

    public ArmorMob(Type type, HeightMapManager.MapType mapType, int wave, ExLocation spawn, int currentWave) {
        super(type, mapType, wave, spawn, currentWave);
    }

    @Override
    public void spawn() {
        this.equipArmor();
        super.spawn();
    }

    public void equipArmor() {

        if (this.currentWave <= 5) {
            int random = this.random.nextInt(10);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                    break;
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.CHAINMAIL_HELMET));
            }
        } else if (this.currentWave <= 7) {
            int random = this.random.nextInt(10);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.IRON_HELMET));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                    break;

                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                    break;

                case 6:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                case 7:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
                case 8:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.CHAINMAIL_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.GOLDEN_HELMET));
                    break;

                case 9:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.GOLDEN_LEGGINGS));
            }
        } else if (this.currentWave <= 10) {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST, new ItemStack(Material.IRON_CHESTPLATE));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.IRON_HELMET));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
            }
        } else if (this.currentWave <= 16) {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.IRON_HELMET));
                case 1:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.IRON_LEGGINGS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.IRON_BOOTS));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
                case 4:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
            }
        } else {
            int random = this.random.nextInt(6);

            switch (random) {
                case 0:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
                case 1:
                case 2:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
                    break;
                case 3:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.HEAD, new ItemStack(Material.DIAMOND_HELMET));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.FEET, new ItemStack(Material.DIAMOND_BOOTS));
                case 4:
                case 5:
                    this.entity.getExtension().setSlot(ExEnumItemSlot.CHEST,
                            new ItemStack(Material.DIAMOND_CHESTPLATE));
                    this.entity.getExtension().setSlot(ExEnumItemSlot.LEGS, new ItemStack(Material.DIAMOND_LEGGINGS));
            }
        }

    }


}
