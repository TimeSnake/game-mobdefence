/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.special.entity;

import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.shop.LevelType;
import de.timesnake.game.mobdefence.shop.Price;
import de.timesnake.game.mobdefence.shop.UpgradeableItem;
import de.timesnake.library.chat.ExTextColor;
import de.timesnake.library.entities.entity.bukkit.ExSheep;
import de.timesnake.library.entities.entity.bukkit.HumanEntity;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalPet;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;

public class SheepSpawner extends EntitySpawner {

    public static final LevelType.Builder AMOUNT_LEVELS = new LevelType.Builder()
            .name("Amount")
            .display(new ExItemStack(Material.SHEEP_SPAWN_EGG))
            .baseLevel(1)
            .levelDescription("+1 Sheep")
            .levelLoreLine(1)
            .levelLoreName("Sheeps")
            .addLoreLvl(null, 4)
            .addLoreLvl(new Price(12, Currency.BRONZE), 5)
            .addLoreLvl(new Price(14, Currency.SILVER), 6)
            .addLoreLvl(new Price(24, Currency.BRONZE), 7)
            .addLoreLvl(new Price(16, Currency.GOLD), 8);

    public static final UpgradeableItem.Builder LEVEL_ITEM = new UpgradeableItem.Builder()
            .name("§6Herd Sheep")
            .display(new ExItemStack(Material.WHEAT, "§6Sheeps"))
            .baseItem(new ExItemStack(Material.WHEAT, "§6Herd Sheeps"))
            .addLvlType(AMOUNT_LEVELS);

    private static final int MAX = 4;

    public SheepSpawner() {
        super(LEVEL_ITEM.getBaseItem(), 5 * 20);
    }

    @Override
    public List<? extends de.timesnake.library.entities.entity.extension.Entity> getEntities(
            User user, ExItemStack item) {

        int sheep = 0;
        for (Entity s : user.getExWorld().getEntitiesByClasses(Sheep.class)) {
            if (s.getCustomName() != null && s.getCustomName().equals(user.getName())) {
                sheep++;
            }
        }

        if (sheep >= MAX) {
            user.sendActionBarText(Component.text("Too many are alive", ExTextColor.WARNING));
            return new ArrayList<>();
        }

        int amount = AMOUNT_LEVELS.getNumberFromLore(item, Integer::valueOf);

        List<de.timesnake.library.entities.entity.extension.Entity> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(this.getSheep(user));
        }

        return entities;
    }

    private ExSheep getSheep(User user) {
        ExSheep entity = new ExSheep(user.getExWorld().getBukkitWorld(), false, false);

        entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        entity.addPathfinderGoal(1, new ExCustomPathfinderGoalPet(user.getPlayer(), 1.3, 4, 7));
        entity.addPathfinderGoal(2, new ExPathfinderGoalRandomStrollLand(1.1D));
        entity.addPathfinderGoal(3, new ExPathfinderGoalLookAtPlayer(HumanEntity.class, 6.0F));
        entity.addPathfinderGoal(4, new ExPathfinderGoalRandomLookaround());

        entity.setCustomName(user.getName());
        entity.setCustomNameVisible(false);

        entity.setMaxHealth(20);
        entity.setHealth(20);

        entity.setGlowing(true);
        entity.setInvisible(true);

        return entity;
    }
}
