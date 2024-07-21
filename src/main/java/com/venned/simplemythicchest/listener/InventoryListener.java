package com.venned.simplemythicchest.listener;

import com.venned.simplemythicchest.Main;
import com.venned.simplemythicchest.build.MythicChest;
import com.venned.simplemythicchest.build.SubChest;
import com.venned.simplemythicchest.utils.CoinsUtils;
import com.venned.simplemythicchest.utils.MapUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;

import java.sql.SQLException;

public class InventoryListener implements Listener {

    private final MapUtils mapUtils;
    private final Main plugin;

    public InventoryListener(Main plugin, MapUtils mapUtils) {
        this.plugin = plugin;
        this.mapUtils = mapUtils;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedInventory != null && clickedItem != null) {
            MythicChest mythicChest = mapUtils.getMythicChest(player.getUniqueId(), clickedInventory);
            Inventory inventory = mapUtils.getPlayerInventory(player.getUniqueId(), mythicChest);
            if (clickedInventory.equals(inventory) && mythicChest != null) {
                event.setCancelled(true);

                ItemMeta itemMeta = clickedItem.getItemMeta();

                if (itemMeta == null) return;
                int level_mythic_chest = mythicChest.getLevel();

                Integer subChestNumber = itemMeta.getPersistentDataContainer().get(
                        new NamespacedKey(plugin, "subchest"),
                        PersistentDataType.INTEGER);
                Integer coins_price = itemMeta.getPersistentDataContainer().get(
                        new NamespacedKey(plugin, "coins"),
                        PersistentDataType.INTEGER);

                if (subChestNumber != null) {

                    SubChest subChest = null;

                    if (subChestNumber == level_mythic_chest + 1) {
                        double coins = CoinsUtils.getCoinsPlayer(player);
                        if (coins < coins_price) {
                            player.sendMessage(ChatColor.RED + "You do not have enough coins to access this submenu.");
                            return;
                        }
                        CoinsUtils.withDraw(player, coins_price);
                        player.closeInventory();
                        mythicChest.increaseLevel(1);
                        mapUtils.updateMythicChestInMap(mythicChest);
                        player.sendMessage(ChatColor.GREEN + "You have successfully purchased access to this submenu.");
                        return;
                    }

                    if (subChestNumber > level_mythic_chest) {
                        player.sendMessage(ChatColor.RED + "You cannot access this submenu at your current MythicChest level.");
                        return;
                    }


                    for(SubChest subChests : mythicChest.getSubChests()) {
                        if(subChests.getSub_chest() == subChestNumber) {
                            subChest = subChests;
                            break;
                        }
                    }

                    if (subChest == null) {
                        subChest = new SubChest(subChestNumber, mythicChest);
                    }
                    mapUtils.setSubChest(player.getUniqueId(), subChest);
                    player.openInventory(subChest.getInventory());
                }
            }
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();

        SubChest subChest = mapUtils.getSubChest(player.getUniqueId());
        if (subChest != null && closedInventory.equals(subChest.getInventory())) {
            MythicChest mainChest = subChest.getMain_chest();

            SubChest existingSubChest = null;

            for(SubChest sub : mainChest.getSubChests()) {
                if(sub.getSub_chest() == subChest.getSub_chest()) {
                    existingSubChest = sub;
                }
            }
                // Si el SubChest no existe, agregamos el nuevo SubChest
                if (existingSubChest == null) {
                    mainChest.getSubChests().add(subChest);
                } else {
                    existingSubChest.getInventory().setContents(subChest.getInventory().getContents());
                }
                //AGREGAR LA CANTIDAD DEL VALOR DEL COFRE
                subChest.updateValue(mapUtils);


            //    saveSubChestToDatabase(mainChest, subChest);
                mapUtils.mythicChestsMap.put(mainChest.getId(), mainChest);
        }
    }

    private void saveSubChestToDatabase(MythicChest mainChest, SubChest subChest) throws SQLException {
        Inventory inventory = subChest.getInventory();
        int subChestNumber = subChest.getSub_chest();
        int chestId = mainChest.getId();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null) {
                String blockType = item.getType().toString();
                int quantity = item.getAmount();
             //   plugin.getDatabase().saveSubChest(chestId, subChestNumber, blockType, quantity, slot);

            }
        }
    }
}
