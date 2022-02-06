package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpecialManager implements Listener {

    public SpecialManager() {
        Server.registerListener(this, GameMobDefence.getPlugin());
    }


    @EventHandler
    public void onEntityMove(EntityMoveEvent e) {
    }

}
