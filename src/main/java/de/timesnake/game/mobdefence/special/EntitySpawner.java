package de.timesnake.game.mobdefence.special;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractEvent;
import de.timesnake.basic.bukkit.util.user.event.UserInventoryInteractListener;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.special.weapon.SpecialWeapon;
import de.timesnake.library.entities.EntityManager;
import de.timesnake.library.entities.entity.extension.EntityExtension;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class EntitySpawner extends SpecialWeapon implements UserInventoryInteractListener {

    private final int cooldown;
    private final Set<User> cooldownUsers = new HashSet<>();

    public EntitySpawner(ExItemStack item, int cooldown) {
        super(item);
        this.cooldown = cooldown;
        Server.getInventoryEventManager().addInteractListener(this, this.getItem());
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {

        User user = event.getUser();

        if (this.cooldownUsers.contains(user)) {
            user.sendActionBarText("§cPlease wait");
            return;
        }

        List<? extends EntityExtension<?>> entities = this.getEntities(user, event.getClickedItem());

        Location loc = user.getLocation();

        for (EntityExtension<?> entity : entities) {
            entity.getExtension().setPosition(loc.getX(), loc.getY(), loc.getZ());
            EntityManager.spawnExEntity(user.getExWorld().getBukkitWorld(), entity);
        }

        this.cooldownUsers.add(user);

        Server.runTaskLaterSynchrony(() -> this.cooldownUsers.remove(user), this.cooldown, GameMobDefence.getPlugin());
    }

    public abstract List<? extends EntityExtension<?>> getEntities(User user, ExItemStack item);
}
