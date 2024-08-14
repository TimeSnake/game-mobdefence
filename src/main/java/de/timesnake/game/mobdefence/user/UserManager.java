/*
 * Copyright (C) 2023 timesnake
 */

package de.timesnake.game.mobdefence.user;

import de.timesnake.basic.bukkit.util.Server;
import de.timesnake.basic.bukkit.util.user.User;
import de.timesnake.basic.bukkit.util.user.event.UserBlockBreakEvent;
import de.timesnake.basic.bukkit.util.user.event.UserBlockPlaceEvent;
import de.timesnake.basic.bukkit.util.user.event.UserDamageByUserEvent;
import de.timesnake.basic.bukkit.util.user.event.UserDropItemEvent;
import de.timesnake.basic.bukkit.util.user.inventory.ExItemStack;
import de.timesnake.game.mobdefence.chat.Plugin;
import de.timesnake.game.mobdefence.kit.MobDefKit;
import de.timesnake.game.mobdefence.main.GameMobDefence;
import de.timesnake.game.mobdefence.mob.map.HeightBlock;
import de.timesnake.game.mobdefence.mob.map.HeightMapManager;
import de.timesnake.game.mobdefence.server.MobDefServer;
import de.timesnake.game.mobdefence.shop.Currency;
import de.timesnake.game.mobdefence.special.CoreRegeneration;
import de.timesnake.game.mobdefence.special.ExplosionManager;
import de.timesnake.game.mobdefence.special.PotionGenerator;
import de.timesnake.game.mobdefence.special.ResistanceAura;
import de.timesnake.game.mobdefence.special.trap.TrapManager;
import de.timesnake.library.chat.ExTextColor;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.text.Component;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

import java.util.List;

public class UserManager implements Listener {

  public static final ExItemStack DEBUG_TOOL = new ExItemStack(Material.BONE)
      .onInteract(e -> {
        User user = e.getUser();

        HeightBlock block = MobDefServer.getMap().getHeightMapManager()
            .getMap(HeightMapManager.MapType.values()[e.getClickedItem().getAmount() - 1])
                .getHeightBlock(user.getExLocation());
        sendHeightLevel(user, block);
      });

  private static final List<Material> ALLOWED_DROPS = List.of(Currency.BRONZE.getItem().getType(),
      Currency.SILVER.getItem().getType(), Currency.GOLD.getItem().getType(),
      Currency.EMERALD.getItem().getType(), Material.COOKED_BEEF, MobDefKit.KELP.getType(),
      Material.GOLDEN_APPLE, Material.OAK_FENCE, Material.OAK_PLANKS, Material.IRON_BARS,
      Material.COBBLESTONE_WALL, Material.OAK_SLAB);

  private static final List<Material> REMOVED_DROPS = List.of(Material.GLASS_BOTTLE,
      Material.BUCKET);

  private static final double BLOCK_VILLAGER_DISTANCE = 3;

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
  }

  public void runTasks() {
    this.potionGenerator.run();
    this.resistanceAura.run();
    this.trapManager.start();
  }

  public void cancelTasks() {
    this.coreRegeneration.cancel();
    this.potionGenerator.cancel();
    this.resistanceAura.cancel();
    this.trapManager.reset();
  }

  @EventHandler
  public void onBlockBreak(UserBlockBreakEvent e) {
    if (e.getUser().isService()) {
      return;
    }

    if (e.getUser() instanceof MobDefUser) {
      if (!((MobDefUser) e.getUser()).isAlive()) {
        e.setCancelled(true);
      }
    }

    Material type = e.getBlock().getType();

    if (!(MobDefServer.BREAKABLE_MATERIALS.contains(type) || e.getBlock().isEmpty() || type.equals(Material.FIRE))) {
      e.setCancelled(true);
    } else {
      ExItemStack item = MobDefKit.BLOCK_ITEM_BY_TYPE.get(type);

      if (item == null) {
        return;
      }

      e.setDropItems(false);
      e.getBlock().getWorld()
          .dropItemNaturally(e.getBlock().getLocation().add(0.5, 0, 0.5), item);
    }
  }

  @EventHandler
  public void onBlockPlace(UserBlockPlaceEvent e) {

    User user = e.getUser();
    Block blockPlaced = e.getBlockPlaced();
    Material type = blockPlaced.getType();

    if (user.isService()) {
      return;
    }

    if (!MobDefServer.BREAKABLE_MATERIALS.contains(type) && !TrapManager.TRAP_MATERIALS.contains(type) && !type.equals(Material.LADDER)) {
      e.setCancelled(true);
      return;
    }

    if (MobDefServer.getMap().getCoreLocation().distanceSquared(blockPlaced.getLocation())
        < BLOCK_VILLAGER_DISTANCE * BLOCK_VILLAGER_DISTANCE) {
      e.setCancelled(true);
      e.getUser().sendPluginMessage(Plugin.MOB_DEFENCE,
          Component.text("You can not place a block here", ExTextColor.WARNING));
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

        Server.runTaskLaterSynchrony(() -> blockPlaced.setType(Material.AIR), 1,
            GameMobDefence.getPlugin());
      }, 1, GameMobDefence.getPlugin());

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

      if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
        e.setCancelled(true);
        e.setDamage(0);
      }
    }

    if (e.getDamager() instanceof Projectile
        && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
      if (!MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
        e.setCancelled(true);
        e.setDamage(0);
      }
    }

    if (e.getDamager().getType().equals(EntityType.CREEPER)
        && MobDefServer.ATTACKER_ENTITY_TYPES.contains(e.getEntityType())) {
      e.setDamage(0);
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityDamageByBlock(EntityDamageByBlockEvent e) {
    if (e.getDamager() == null && (e.getEntityType().equals(EntityType.PLAYER)
        || e.getEntityType().equals(EntityType.VILLAGER))) {
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
    if (e.getUser().isService()) {
      return;
    }

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

    if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && e.getView().getTopInventory()
        .equals(((MobDefUser) user).getShop().getInventory())) {
      e.setResult(Event.Result.DENY);
      e.setCancelled(true);
    }
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

  @EventHandler
  public void onBlockForm(EntityBlockFormEvent e) {
    if (e.getNewState().getType().equals(Material.SNOW)) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onEntityChangeBlock(EntityChangeBlockEvent e) {
    if (e.getBlock().getType().equals(Material.SNOW)) {
      e.setCancelled(true);
    }
  }

  private static void sendHeightLevel(User user, HeightBlock block) {
    user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§sLevel: §v" + (block != null ? String.valueOf(block.level()) :
        "null"));

    if (block == null) {
      return;
    }

    HeightBlock next = block.next();
    if (next != null) {
      Location loc = next.block().getLocation();
      user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§sNext: §v" + loc.getX() + " " + loc.getY() + " " + loc.getZ()
                                                   + "§s Level: §v" + next.level() + "§s Blocks: " + block.blocksToBreakForNext().size() + " " + next.blocksToBreak().size());
    } else {
      user.sendPluginTDMessage(Plugin.MOB_DEFENCE, "§sNext: §vnull");
    }
  }

  public ReviveManager getReviveManager() {
    return reviveManager;
  }

  public CoreRegeneration getCoreRegeneration() {
    return coreRegeneration;
  }
}
