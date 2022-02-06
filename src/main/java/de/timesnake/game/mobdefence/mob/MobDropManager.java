package de.timesnake.game.mobdefence.mob;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.kit.ShopPrice;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobDropManager implements Listener {

    private static final double EMERALD_CHANCE = 0.08;
    private static final double GOLD_CHANCE = 0.15;
    private static final double SILVER_CHANCE = 0.4;
    private static final double BRONZE_CHANCE = 0.6;

    public MobDropManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }

    @EventHandler
    public void onItemDrop(EntityDeathEvent e) {
        e.setDroppedExp(0);
        e.getDrops().clear();

        if (MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
            if (e.getEntityType().equals(EntityType.ENDERMITE) || e.getEntityType().equals(EntityType.SILVERFISH)) {
                return;
            }

            if (Math.random() < EMERALD_CHANCE / MobDefServer.getPlayerAmount()) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.EMERALD).asItem());
            }
            if (Math.random() < GOLD_CHANCE) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.GOLD).asItem());
            }
            if (Math.random() < SILVER_CHANCE) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.SILVER).asItem());
            }
            if (Math.random() < BRONZE_CHANCE) {
                e.getDrops().add(new ShopPrice(1, ShopCurrency.BRONZE).asItem());
            }
        }
    }
}
