package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.library.basic.util.chat.ExTextColor;
import de.timesnake.library.entities.entity.ExtendedCraftEntity;
import de.timesnake.library.entities.entity.bukkit.ExWolf;
import de.timesnake.library.entities.pathfinder.*;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalHurtByTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalNearestAttackableTarget;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.wrapper.EntityClass;
import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wolf;

import java.util.ArrayList;
import java.util.List;

public class DogSpawner extends EntitySpawner {

    public static final ItemLevelType<?> AMOUNT_LEVELS = new ItemLevelType<>("Amount",
            new ExItemStack(Material.WOLF_SPAWN_EGG), 1, 5, ItemLevel.getLoreNumberLevels("Amount", 1, 0, "Dogs", 2,
            List.of(new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(14, ShopCurrency.SILVER), new ShopPrice(24,
                    ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.GOLD)), "+1 Dog", List.of(5, 6, 7, 8)));
    public static final LevelItem LEVEL_ITEM = new LevelItem("§6Call Dogs", new ExItemStack(Material.BONE, "§6Call " +
            "Dogs").setLore("", AMOUNT_LEVELS.getBaseLevelLore(4)), new ExItemStack(Material.BONE, "§6Call Dogs"),
            List.of(AMOUNT_LEVELS));
    private static final int MAX = 4;

    public DogSpawner() {
        super(LEVEL_ITEM.getItem(), 20 * 60);
    }

    @Override
    public List<? extends ExtendedCraftEntity<?>> getEntities(User user, ExItemStack item) {

        int dogs = 0;
        for (Entity wolf : user.getWorld().getEntitiesByClasses(Wolf.class)) {
            if (wolf instanceof Wolf && ((Wolf) wolf).getOwnerUniqueId() != null && ((Wolf) wolf).getOwnerUniqueId().equals(user.getUniqueId())) {
                dogs++;
            }
        }

        if (dogs >= MAX) {
            user.sendActionBarText(Component.text("Too many are alive", ExTextColor.WARNING));
            return new ArrayList<>();
        }

        int amount = Integer.parseInt(AMOUNT_LEVELS.getValueFromLore(item.getLore()));

        List<ExtendedCraftEntity<?>> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(this.getDog(user));
        }

        return entities;
    }

    private ExWolf getDog(User user) {
        ExWolf entity = new ExWolf(user.getExWorld().getBukkitWorld(), false);

        entity.setTamed(true);
        entity.setOwnerUUID(user.getUniqueId());
        entity.setWillSit(false);

        entity.addPathfinderGoal(1, new ExPathfinderGoalFloat());
        entity.addPathfinderGoal(4, new ExPathfinderGoalLeapAtTarget(0.4F));
        entity.addPathfinderGoal(5, new ExPathfinderGoalMeleeAttack(1.0D));
        entity.addPathfinderGoal(6, new ExPathfinderGoalFollowOwner(1.0D, 10.0F, 2.0F, false));
        entity.addPathfinderGoal(8, new ExCustomPathfinderGoalRandomStrollLand(1.0D));
        entity.addPathfinderGoal(10, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
        entity.addPathfinderGoal(10, new ExPathfinderGoalRandomLookaround());

        entity.addPathfinderGoal(1, new ExPathfinderGoalOwnerHurtByTarget());
        entity.addPathfinderGoal(2, new ExPathfinderGoalOwnerHurtTarget());

        entity.addPathfinderGoal(3, new ExCustomPathfinderGoalHurtByTarget(MobDefMob.DEFENDER_CLASSES));
        for (EntityClass<? extends EntityLiving> entityClass : MobDefMob.ATTACKER_ENTTIY_ENTITY_CLASSES) {
            entity.addPathfinderGoal(4, new ExCustomPathfinderGoalNearestAttackableTarget(entityClass, 10, true,
                    false));
        }


        entity.setMaxHealth(30);
        entity.setHealth(30);

        entity.getBukkitAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);

        return entity;
    }
}
