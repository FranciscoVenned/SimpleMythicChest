package com.venned.simplemythicchest.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.venned.simplemythicchest.Main;
import com.venned.simplemythicchest.build.MythicChest;
import com.venned.simplemythicchest.build.SubChest;
import com.venned.simplemythicchest.utils.ItemUtil;
import com.venned.simplemythicchest.utils.MapUtils;
import com.venned.simplemythicchest.utils.Util;
import com.venned.simplemythicchest.utils.UtilLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerListener implements Listener {

    private final Main plugin;
    private final MapUtils mapUtils;
    private final ItemUtil itemUtil;


    public PlayerListener(Main plugin, MapUtils mapUtils) {
        this.plugin = plugin;
        this.mapUtils = mapUtils;
        this.itemUtil = new ItemUtil(plugin);
    }

    @EventHandler
    public void chestPlace(BlockPlaceEvent event) {
        Island island = SuperiorSkyblockAPI.getIslandAt(event.getBlock().getLocation());
        if(island != null) {
            UUID UUID_Island = island.getUniqueId();
            if (event.getItemInHand().getType() == Material.ENDER_CHEST) {
                ItemStack item = event.getItemInHand();
                ItemMeta meta = item.getItemMeta();
                if(island.isMember(SuperiorSkyblockAPI.getPlayer(event.getPlayer())) ||
                island.getOwner().equals(SuperiorSkyblockAPI.getPlayer(event.getPlayer()))) {
                    if (meta != null) {
                        if (meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "mythical"), PersistentDataType.INTEGER)) {
                            int level = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "mythical"), PersistentDataType.INTEGER);
                            BlockState state = event.getBlock().getState();
                            TileState tileState = (TileState) state;
                            tileState.getPersistentDataContainer().set(new NamespacedKey(plugin, "mythical"), PersistentDataType.INTEGER, level);
                            tileState.update();

                            String location = UtilLocation.serializeLocation(event.getBlock().getLocation());

                            try {
                                int id = plugin.getDatabase().getNextChestId();
                                plugin.getDatabase().saveChest(id, level, event.getPlayer().getName(), location, UUID_Island, 0);
                                mapUtils.mythicChestsMap.put(id, new MythicChest(id, level, event.getPlayer().getName(), event.getBlock().getLocation(), UUID_Island, 0));
                            } catch (SQLException e) {
                                plugin.getLogger().log(Level.SEVERE, "Error saving mythical chest to database", e);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                Island island = SuperiorSkyblockAPI.getIslandAt(clickedBlock.getLocation());
                if (island != null) {

                    SuperiorPlayer superiorPlayer = island.getOwner();
                    if(superiorPlayer == null) return;
                    Player player_bukkit = superiorPlayer.asPlayer();

                    if(event.getPlayer().getUniqueId().equals(player_bukkit.getUniqueId()) || isPlayerMember(event.getPlayer(), island)
                            || island.isMember(SuperiorSkyblockAPI.getPlayer(event.getPlayer())) || event.getPlayer().isOp() ) {
                        if (clickedBlock.getType() == Material.ENDER_CHEST) {
                            BlockState state = clickedBlock.getState();
                            if (state instanceof TileState) {
                                TileState tileState = (TileState) state;
                                if (tileState.getPersistentDataContainer().has(new NamespacedKey(plugin, "mythical"), PersistentDataType.INTEGER)) {
                                    int level = tileState.getPersistentDataContainer().get(new NamespacedKey(plugin, "mythical"), PersistentDataType.INTEGER);
                                    event.setCancelled(true);
                                    MythicChest mythicChest = plugin.getDatabase().getByLocation(clickedBlock.getLocation());
                                    if (mythicChest != null) {
                                        openMainChestMenu(event.getPlayer(), mythicChest.getLevel(), clickedBlock.getLocation());
                                    } else {
                                        openMainChestMenu(event.getPlayer(), level, clickedBlock.getLocation());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isPlayerMember(Player player, Island island){
        for(SuperiorPlayer superiorPlayer : island.getCoopPlayers()){
            Player bukkit_player = superiorPlayer.asPlayer();
            if(bukkit_player.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    private void openMainChestMenu(Player player, int level, Location location) {
        Inventory mainMenu = Bukkit.createInventory(null, 54, "Main Menu - Mythic Chest Level " + level);
        MythicChest mythicChest = plugin.getDatabase().getByLocation(location);

        if (mythicChest == null) {
            player.sendMessage("Error: MythicChest not found at location: " + location);
            return;
        }

        mapUtils.savePlayerInventory(player.getUniqueId(), mythicChest, mainMenu);

        int[] enderChestSlots = {22, 23, 24, 31, 32, 33};
        int sub_menu = 0;

        for (int slot : enderChestSlots) {
            sub_menu++;
            ItemStack subChestItem;
            if (sub_menu <= mythicChest.getLevel()) {
                subChestItem = new ItemStack(Material.ENDER_CHEST);
                ItemMeta subChestMeta = subChestItem.getItemMeta();
                if (subChestMeta != null) {
                    String title_sub_chest = Util.colorize(plugin.getConfig().getString("title-sub-chest-available").replace("%number_submenu%", String.valueOf(sub_menu)));
                    subChestMeta.setDisplayName(title_sub_chest);
                    subChestMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "subchest"),
                            PersistentDataType.INTEGER,
                            sub_menu);
                    subChestMeta.setLore(Util.replacePlaceholders(plugin.getConfig().getStringList("lore-sub-chest-available"), "%number_submenu%", String.valueOf(sub_menu)));
                    subChestItem.setItemMeta(subChestMeta);
                }
            } else {
                subChestItem = new ItemStack(Material.BARRIER);
                ItemMeta barrierMeta = subChestItem.getItemMeta();
                if (barrierMeta != null) {
                    String title_sub_chest_locked = Util.colorize(plugin.getConfig().getString("title-sub-chest-locked").replace("%number_submenu%", String.valueOf(sub_menu)));
                    barrierMeta.setDisplayName(title_sub_chest_locked);
                    int coins = plugin.getConfig().getInt("prices." + sub_menu + ".coins");
                    barrierMeta.setLore(Util.replacePlaceholders(
                            plugin.getConfig().getStringList("lore-sub-chest-locked"),
                            new String[]{"%number_submenu%", "%coins%"},
                            new String[]{String.valueOf(sub_menu), String.valueOf(coins)}));
                    barrierMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "coins"),
                            PersistentDataType.INTEGER,
                            coins);
                    barrierMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "subchest"),
                            PersistentDataType.INTEGER,
                            sub_menu);
                    subChestItem.setItemMeta(barrierMeta);
                }
            }
            mainMenu.setItem(slot - 1, subChestItem);

        }

        for (int i = 0; i < mainMenu.getSize(); i++) {
            if (mainMenu.getItem(i) == null) {
                ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta glassMeta = blackGlass.getItemMeta();
                if (glassMeta != null) {
                    glassMeta.setDisplayName(" ");
                    blackGlass.setItemMeta(glassMeta);
                }
                mainMenu.setItem(i, blackGlass);
            }
        }

        player.openInventory(mainMenu);
    }

    @EventHandler
    public void mythicalChestBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();

        if(event.getBlock().getType() == Material.ENDER_CHEST) {
            MythicChest mythicChest = plugin.getDatabase().getByLocation(event.getBlock().getLocation());
            if (mythicChest != null) {

                Island island = SuperiorSkyblockAPI.getIslandAt(mythicChest.getLocation());

                if (island != null) {

                    ItemStack mythicItem = itemUtil.getItemMythic(mythicChest.getLevel());

                    Block block = event.getBlock();
                    block.setType(Material.AIR);

                    player.getWorld().dropItemNaturally(block.getLocation(), mythicItem);
                    int value = mythicChest.getValue_chest();
                    BigDecimal updatedWorth = island.getBonusWorth().subtract(BigDecimal.valueOf(value));
                    island.setBonusWorth(updatedWorth);

                    mapUtils.mythicChestsMap.remove(mythicChest.getId());
                    try {
                        plugin.getDatabase().deleteMythicChestByID(mythicChest.getId());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
