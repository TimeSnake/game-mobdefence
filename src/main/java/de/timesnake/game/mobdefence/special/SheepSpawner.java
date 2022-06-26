package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.game.mobdefence.kit.*;
import de.timesnake.library.entities.entity.ExtendedCraftEntity;
import de.timesnake.library.entities.entity.bukkit.ExSheep;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalFloat;
import de.timesnake.library.entities.pathfinder.ExPathfinderGoalRandomLookaround;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalLookAtPlayer;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalPet;
import de.timesnake.library.entities.pathfinder.custom.ExCustomPathfinderGoalRandomStrollLand;
import de.timesnake.library.entities.wrapper.EntityClass;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;

import java.util.ArrayList;
import java.util.List;

public class SheepSpawner extends EntitySpawner {

    public static final ItemLevelType<?> AMOUNT_LEVELS = new ItemLevelType<>("Amount",
            new ExItemStack(Material.SHEEP_SPAWN_EGG), 1, 5, ItemLevel.getLoreNumberLevels("Amount", 1, 0, "Sheep", 2
            , List.of(new ShopPrice(12, ShopCurrency.BRONZE), new ShopPrice(14, ShopCurrency.SILVER),
                    new ShopPrice(24, ShopCurrency.BRONZE), new ShopPrice(16, ShopCurrency.GOLD)), "+1 Sheep",
            List.of(5, 6, 7, 8)));
    public static final LevelItem LEVEL_ITEM = new LevelItem("§6Herd Sheep", new ExItemStack(Material.WHEAT, "§6Herd " +
            "Sheep").setLore("", AMOUNT_LEVELS.getBaseLevelLore(4)), new ExItemStack(Material.WHEAT, "§6Herd Sheep"),
            List.of(AMOUNT_LEVELS));
    private static final int MAX = 4;

    public SheepSpawner() {
        super(LEVEL_ITEM.getItem(), 5 * 20);
    }

    @Override
    public List<ExtendedCraftEntity<?>> getEntities(User user, ExItemStack item) {

        int sheep = 0;
        for (Entity s : user.getExWorld().getEntitiesByClasses(Sheep.class)) {
            if (s.getCustomName() != null && s.getCustomName().equals(user.getName())) {
                sheep++;
            }
        }

        if (sheep >= MAX) {
            user.sendActionBarText("§cToo many are alive");
            return new ArrayList<>();
        }

        int amount = Integer.parseInt(AMOUNT_LEVELS.getValueFromLore(item.getLore()));

        List<ExtendedCraftEntity<?>> entities = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            entities.add(this.getSheep(user));
        }

        return entities;
    }

    private ExSheep getSheep(User user) {
        ExSheep entity = new ExSheep(user.getExWorld().getBukkitWorld(), false);

        entity.addPathfinderGoal(0, new ExPathfinderGoalFloat());
        entity.addPathfinderGoal(1, new ExCustomPathfinderGoalPet(user.getPlayer(), 1.3, 4, 7));
        entity.addPathfinderGoal(2, new ExCustomPathfinderGoalRandomStrollLand(1.1D));
        entity.addPathfinderGoal(3, new ExCustomPathfinderGoalLookAtPlayer(EntityClass.EntityHuman));
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
