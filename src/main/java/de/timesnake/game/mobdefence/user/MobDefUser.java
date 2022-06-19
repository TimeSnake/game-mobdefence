package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.loungebridge.util.user.GameUser;
import de.timesnake.game.mobdefence.kit.KitShop;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.TeamHealth;
import de.timesnake.library.basic.util.Status;
import de.timesnake.library.entities.entity.bukkit.ExPlayer;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class MobDefUser extends GameUser {

    private boolean alive;
    private ItemStack[] invItems;

    private KitShop shop;

    private ExPlayer deadBody;
    private MobDefUser beingRevivedUser = null;

    public MobDefUser(Player player) {
        super(player);
    }

    @Override
    public void joinGame() {
        this.teleport(MobDefServer.getMap().getUserSpawn());
        this.lockLocation(true);
        this.setBossBar(MobDefServer.getCoreHealthBar());
        this.setGameMode(GameMode.SURVIVAL);

        this.alive = true;

        this.loadGameSideboard();

        this.getPlayer().setMaxHealth(TeamHealth.MAX_HEALTH.getMaxHealth());

        MobDefServer.updateSideboardPlayers();

        this.setStatistic(Statistic.MOB_KILLS, 0);
    }

    public void startGame() {
        this.lockLocation(false);
    }

    @Override
    public boolean setKitItems() {
        boolean loaded = super.setKitItems();

        if (!loaded) {
            return false;
        }

        this.loadKit();

        return true;
    }

    public void loadKit() {
        this.shop = ((MobDefKit) this.kit).getShop(this);


        if (this.kit.equals(MobDefKit.ALCHEMIST)) {
            Server.runTaskSynchrony(() -> this.addPotionEffect(PotionEffectType.FIRE_RESISTANCE, 0),
                    GameMobDefence.getPlugin());
            this.setPvpMode(true, 6);
        } else if (this.kit.equals(MobDefKit.KNIGHT)) {
            this.setPvpMode(true);
        } else {
            this.setPvpMode(false);
        }

        if (this.kit.equals(MobDefKit.LUMBERJACK)) {
            AttributeInstance speed = this.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
            AttributeInstance damage = this.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);

            speed.setBaseValue(6);
            damage.setBaseValue(2);
        }
    }

    public void saveInventory() {
        this.invItems = this.getInventory().getContents();
    }

    @Override
    public void joinSpectator() {
        this.alive = false;

        if (this.getStatus().equals(Status.User.IN_GAME) || this.getStatus().equals(Status.User.OUT_GAME)) {
            this.deadBody = MobDefServer.getMobDefUserManager().getReviveManager().addDeadUser(this,
                    this.getLocation());
        }

        super.joinSpectator();

        MobDefServer.updateSideboardPlayers();

        this.setGameMode(GameMode.CREATIVE);
    }

    @Override
    public void rejoinGame() {
        if (this.getStatus().equals(Status.User.SPECTATOR)) {
            return;
        }

        super.rejoinGame();

        this.alive = true;
        this.beingRevivedUser = null;

        this.setDefault();
        this.setStatus(Status.User.IN_GAME);

        MobDefServer.getGameTablist().addEntry(this);

        for (User user : Server.getUsers()) {
            user.showUser(this);

            if (user.getStatus().equals(Status.User.OUT_GAME) || user.getStatus().equals(Status.User.SPECTATOR)) {
                this.hideUser(user);
            }
        }

        MobDefServer.getSpectatorChat().removeWriter(this);
        MobDefServer.getSpectatorChat().removeListener(this);
        MobDefServer.getGlobalChat().addWriter(this);

        this.setGameMode(GameMode.SURVIVAL);

        if (this.kit.equals(MobDefKit.ALCHEMIST)) {
            Server.runTaskSynchrony(() -> this.addPotionEffect(PotionEffectType.FIRE_RESISTANCE, 0),
                    GameMobDefence.getPlugin());
        }

        if (this.deadBody != null) {
            this.teleport(this.deadBody.getLocation());
            this.deadBody = null;

            this.getPlayer().setInvulnerable(true);
            Server.runTaskLaterSynchrony(() -> this.getPlayer().setInvulnerable(false), 3 * 20,
                    GameMobDefence.getPlugin());
        } else {
            this.teleport(MobDefServer.getMap().getUserSpawn());
        }

        this.getInventory().setContents(this.invItems);

        MobDefServer.updateSideboardPlayers();

        this.loadGameSideboard();
    }

    public void loadGameSideboard() {
        this.setSideboard(MobDefServer.getSideboard());
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public KitShop getShop() {
        return shop;
    }

    public void setShop(KitShop shop) {
        this.shop = shop;
        this.shop.setUser(this);
    }

    public boolean isBeingRevived() {
        return this.beingRevivedUser != null;
    }

    public MobDefUser getBeingRevivedUser() {
        return this.beingRevivedUser;
    }

    public void setBeingRevivedUser(MobDefUser user) {
        this.beingRevivedUser = user;
    }

    public ExPlayer getDeadBody() {
        return deadBody;
    }
}
