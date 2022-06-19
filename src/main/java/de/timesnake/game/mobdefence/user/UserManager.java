package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.ExItemStack;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.*;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.kit.ShopCurrency;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.MobDefMob;
import de.timesnake.game.mobdefence.mob.map.BlockCheck;
import de.timesnake.game.mobdefence.mob.map.HeightBlock;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.special.*;
import de.timesnake.game.mobdefence.special.trap.TrapManager;
import de.timesnake.library.basic.util.Status;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserManager implements Listener, UserInventoryInteractListener {

    public static final ExItemStack DEBUG_TOOL = new ExItemStack(Material.BONE);

    private static final List<Material> ALLOWED_DROPS = List.of(ShopCurrency.BRONZE.getItem().getType(),
            ShopCurrency.SILVER.getItem().getType(), ShopCurrency.GOLD.getItem().getType(),
            ShopCurrency.EMERALD.getItem().getType(), Material.COOKED_BEEF, MobDefKit.KELP.getType(),
            Material.GOLDEN_APPLE, Material.OAK_FENCE, Material.OAK_PLANKS, Material.IRON_BARS,
            Material.COBBLESTONE_WALL, Material.OAK_SLAB);

    private static final List<Material> REMOVED_DROPS = List.of(Material.GLASS_BOTTLE, Material.BUCKET);

    private static final double BLOCK_VILLAGER_DISTANCE = 3;

    private final Collection<ItemGenerator> itemGenerators = new ArrayList<>();

    private final CoreRegeneration coreRegeneration;
    private final ResistanceAura resistanceAura;

    private final PotionGenerator potionGenerator;


    private final ReviveManager reviveManager;

    private final MobTracker mobTracker;

    private final ExplosionManager explosionManager;

    private final TrapManager trapManager;

    public UserManager() {

        //this.itemGenerators.add(new ItemGenerator(MobDefKit.ARCHER, 1, Speer.SPEER.getItem().cloneWithId()
        // .asQuantity(3), 8));

        this.coreRegeneration = new CoreRegeneration();
        this.resistanceAura = new ResistanceAura();

        this.potionGenerator = new PotionGenerator();

        this.reviveManager = new ReviveManager();

        this.mobTracker = new MobTracker();

        this.explosionManager = new ExplosionManager();

        this.trapManager = new TrapManager();

        Server.registerListener(this, GameMobDefence.getPlugin());
        Server.getInventoryEventManager().addInteractListener(this, DEBUG_TOOL, MobDefKit.KELP);
    }

    public void runTasks() {
        for (ItemGenerator generator : this.itemGenerators) {
            generator.run();
        }
        this.potionGenerator.run();
        this.reviveManager.run();
        this.resistanceAura.run();
        this.trapManager.start();
    }

    public void cancelTasks() {
        for (ItemGenerator generator : this.itemGenerators) {
            generator.cancel();
        }
        this.coreRegeneration.cancel();
        this.potionGenerator.cancel();
        this.reviveManager.stop();
        this.resistanceAura.cancel();

        TeamHealth.MAX_HEALTH.reset();

        this.trapManager.reset();
    }

    @EventHandler
    public void onUserDeath(UserDeathEvent e) {
        if (!(e.getUser() instanceof MobDefUser)) {
            return;
        }

        e.setAutoRespawn(true);

        User user = e.getUser();

        if (MobDefServer.getAliveUsers().size() <= 1) {
            ((MobDefUser) user).joinSpectator();
            MobDefServer.stopGame();
        }

        if (!(user instanceof MobDefUser)) {
            return;
        }

        if (user.getStatus().equals(Status.User.IN_GAME)) {
            ((MobDefUser) user).saveInventory();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(MobDefServer.getMap().getUserSpawn());

        User user = Server.getUser(e.getPlayer());

        if (!(user instanceof MobDefUser)) {
            return;
        }

        if (user.getStatus().equals(Status.User.IN_GAME)) {
            ((MobDefUser) user).joinSpectator();
        }
    }

    @EventHandler
    public void onBlockBreak(UserBlockBreakEvent e) {
        if (e.getUser() instanceof MobDefUser) {
            if (!((MobDefUser) e.getUser()).isAlive()) {
                e.setCancelled(true);
            }
        }

        Material type = e.getBlock().getType();

        if (!(BlockCheck.NORMAL_BREAKABLE.isTagged(type) || BlockCheck.HIGH_BREAKABLE.isTagged(type) || e.getBlock().isEmpty() || type.equals(Material.FIRE))) {
            e.setCancelled(true);
        } else {
            ExItemStack item = MobDefKit.BLOCK_ITEM_BY_TYPE.get(type);

            if (item == null) {
                return;
            }

            e.setDropItems(false);
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0.5, 0, 0.5), item);
        }
    }

    @EventHandler
    public void onBlockPlace(UserBlockPlaceEvent e) {

        User user = e.getUser();
        Block blockPlaced = e.getBlockPlaced();
        Material type = blockPlaced.getType();

        if (!BlockCheck.NORMAL_BREAKABLE.isTagged(type) && !BlockCheck.HIGH_BREAKABLE.isTagged(type) && !TrapManager.TRAP_MATERIALS.contains(type) && !type.equals(Material.LADDER)) {
            e.setCancelled(true);
            return;
        }

        if (MobDefServer.getMap().getCoreLocation().distanceSquared(blockPlaced.getLocation()) < BLOCK_VILLAGER_DISTANCE * BLOCK_VILLAGER_DISTANCE) {
            e.setCancelled(true);
            e.getUser().sendPluginMessage(Plugin.MOB_DEFENCE, "You can not place a block here");
            return;
        }

        Location loc = blockPlaced.getLocation();

        boolean empty = true;

        for (int y = 1; y <= 2; y++) {
            loc = loc.clone().add(0, -y, 0);

            if (!loc.getBlock().isEmpty()) {
                empty = false;
                break;
            }
        }

        if (empty) {
            Server.runTaskLaterSynchrony(() -> {
                blockPlaced.getWorld().spawnFallingBlock(blockPlaced.getLocation().add(0.5, 0, 0.5),
                        blockPlaced.getBlockData());

                Server.runTaskLaterSynchrony(() -> blockPlaced.setType(Material.AIR), 1, GameMobDefence.getPlugin());
            }, 1, GameMobDefence.getPlugin());

        }
    }

    @EventHandler
    public void onInventoryOpen(PlayerInteractEvent e) {
        if (e.getClickedBlock() instanceof InventoryHolder) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            MobDefUser user = ((MobDefUser) Server.getUser(((Player) e.getDamager())));
            if (!user.isAlive()) {
                e.setCancelled(true);
                e.setDamage(0);
            }

            if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
                e.setCancelled(true);
                e.setDamage(0);
            }
        }

        if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
            if (!MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
                e.setCancelled(true);
                e.setDamage(0);
            }
        }

        if (e.getDamager().getType().equals(EntityType.CREEPER) && MobDefMob.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
            e.setDamage(0);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByBlock(EntityDamageByBlockEvent e) {
        if (e.getDamager() == null && (e.getEntityType().equals(EntityType.PLAYER) || e.getEntityType().equals(EntityType.VILLAGER))) {
            e.setDamage(0);
            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onUserDamage(UserDamageByUserEvent e) {
        e.setCancelled(true);
        e.setCancelDamage(true);
    }

    @EventHandler
    public void onUserDropItem(UserDropItemEvent e) {
        Material material = e.getItemStack().getType();

        if (REMOVED_DROPS.contains(material)) {
            e.setCancelled(true);
            Server.runTaskLaterSynchrony(() -> e.getUser().getInventory().remove(material), 1,
                    GameMobDefence.getPlugin());
        }

        if (!ALLOWED_DROPS.contains(material)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        User user = Server.getUser(e.getWhoClicked().getUniqueId());

        if (user == null) {
            return;
        }

        InventoryAction action = e.getAction();

        if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getView().getTopInventory().equals(((MobDefUser) user).getShop().getInventory())) {
            e.setResult(Event.Result.DENY);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onUserDamageByUser(UserDamageByUserEvent e) {
        e.setCancelDamage(true);
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickUpArrow(PlayerPickupArrowEvent e) {
        e.getArrow().remove();
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerShearEntity(PlayerShearEntityEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerShearBlock(PlayerShearBlockEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBread(PlayerInteractEntityEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void onUserInventoryInteract(UserInventoryInteractEvent event) {
        User user = event.getUser();
        ExItemStack item = event.getClickedItem();

        if (item.equals(DEBUG_TOOL)) {
            if (user.getPlayer().isSneaking()) {
                HeightBlock block =
                        MobDefServer.getMap().getHeightMapManager().getMap(HeightMapManager.MapType.WALL_FINDER).getHeightBlock(user.getExLocation());
                this.sendHeightLevel(user, block);
                return;
            }

            HeightBlock block =
                    MobDefServer.getMap().getHeightMapManager().getMap(HeightMapManager.MapType.NORMAL).getHeightBlock(user.getExLocation());
            this.sendHeightLevel(user, block);
        } else if (MobDefKit.KELP.equals(item)) {
            event.setCancelled(true);

            if (user.getPlayer().getFoodLevel() == 20) {
                return;
            }

            user.removeCertainItemStack(MobDefKit.KELP.cloneWithId().asOne());
            double food = user.getPlayer().getFoodLevel();
            user.getPlayer().setFoodLevel(food <= 19 ? ((int) (food + 1)) : 20);
            user.getPlayer().setSaturation(3);
        }
    }

    @EventHandler
    public void onBlockForm(EntityBlockFormEvent e) {
        if (e.getNewState().getType().equals(Material.SNOW)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(EntityChangeBlockEvent e) {
        if (e.getBlock().getType().equals(Material.SNOW)) {
            e.setCancelled(true);
        }
    }

    private void sendHeightLevel(User user, HeightBlock block) {
        String level;
        if (block != null) {
            level = String.valueOf(block.getLevel());
        } else {
            level = "null";
        }
        user.sendPluginMessage(Plugin.MOB_DEFENCE, "Height: " + level);
        if (block != null) {
            HeightBlock next = block.getNext();
            if (next != null) {
                Location loc = next.getLocation();
                user.sendPluginMessage(Plugin.MOB_DEFENCE, "Next: " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
            } else {
                user.sendPluginMessage(Plugin.MOB_DEFENCE, "Next: null");
            }
        }
    }

    public ReviveManager getReviveManager() {
        return reviveManager;
    }

    public CoreRegeneration getCoreRegeneration() {
        return coreRegeneration;
    }
}
