package com.venned.simplemythicchest.utils;

import com.venned.simplemythicchest.build.MythicChest;
import com.venned.simplemythicchest.build.SubChest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MapUtils {

    private final Plugin plugin;

    public MapUtils(Plugin plugin) {
        this.plugin = plugin;
        loadBlockValues();
    }

    public Map<UUID, Map<MythicChest, Inventory>> playerInventory = new ConcurrentHashMap<>();
    public Map<UUID, SubChest> subChest = new ConcurrentHashMap<>();
    public Map<Integer, MythicChest> mythicChestsMap = new ConcurrentHashMap<>();
    private Map<String, Integer> blockValues = new HashMap<>();

    private void loadBlockValues() {
        ConfigurationSection blocksValueSection = plugin.getConfig().getConfigurationSection("blocks-value");
        if (blocksValueSection != null) {
            for (String key : blocksValueSection.getKeys(false)) {
                int value = blocksValueSection.getInt(key);
                blockValues.put(key, value);
            }
        }
    }

    public int getBlockValue(String blockType) {
        return blockValues.getOrDefault(blockType, 0);
    }

    public void updateMythicChestInMap(MythicChest mythicChest) {
        mythicChestsMap.put(mythicChest.getId(), mythicChest);
    }

    public MythicChest getMythicChest(int id) {
        return mythicChestsMap.get(id);
    }

    public void setSubChest(UUID uuid, SubChest subChest) {
        this.subChest.put(uuid, subChest);
    }

    public SubChest getSubChest(UUID uuid) {
        return this.subChest.get(uuid);
    }

    public Map<UUID, SubChest> getSubChest() {
        return subChest;
    }

    public void savePlayerInventory(UUID playerId, MythicChest mythicChest, Inventory inventory) {
        // Obtener o crear el mapa de inventarios del jugador
        Map<MythicChest, Inventory> playerInventories = playerInventory.computeIfAbsent(playerId, k -> new HashMap<>());

        // Mensaje de depuración para verificar que el mythicChest no es null
     //   System.out.println("Guardando inventario para MythicChest ID: " + mythicChest.getId());

        // Guardar la relación entre el cofre mítico y el inventario
        playerInventories.put(mythicChest, inventory);

     //   System.out.println("Inventario guardado para el jugador " + playerId + ": " + playerInventories);
     //   System.out.println("Estructura completa de playerInventory: " + playerInventory);
    }

    public MythicChest getMythicChest(UUID playerId, Inventory inventory) {
    //    System.out.println("Buscando MythicChest para playerId: " + playerId + " con inventory: " + inventory);

    //    System.out.println("Estructura completa de playerInventory antes de buscar: " + playerInventory);


        Map<MythicChest, Inventory> playerInventories = playerInventory.get(playerId);
    //    System.out.println("playerInventories: " + playerInventories);
        if (playerInventories != null) {
            for (Map.Entry<MythicChest, Inventory> entry : playerInventories.entrySet()) {
      //          System.out.println("Comparando: " + entry.getValue() + " con " + inventory);
                if (entry.getValue().equals(inventory)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public Inventory getPlayerInventory(UUID playerId){
        Map<MythicChest, Inventory> playerInventories = playerInventory.get(playerId);
        if (playerInventories != null) {

            for(Map.Entry<MythicChest, Inventory> entry : playerInventories.entrySet()){
                return entry.getValue();
            }

        }
        return null;
    }


    public Inventory getPlayerInventory(UUID playerId, MythicChest mythicChest) {
        Map<MythicChest, Inventory> playerInventories = playerInventory.get(playerId);
        if (playerInventories != null) {
            return playerInventories.get(mythicChest);
        }
        return null;
    }

    public void updateValueMythical(MythicChest mythicChest){
        int value_before = mythicChest.getValue_chest();
        int new_value = mythicChest.getTotalValue_SubChests();
     //   System.out.println("Valor total de mythical " + new_value);
        mythicChest.setValue_chest(new_value);
        IslandUtils.updateValue(mythicChest.getLocation(), new_value, value_before);

    }

    public void removePlayerInventory(UUID playerId, MythicChest mythicChest) {
        Map<MythicChest, Inventory> playerInventories = playerInventory.get(playerId);
        if (playerInventories != null) {
            playerInventories.remove(mythicChest);
            if (playerInventories.isEmpty()) {
                playerInventory.remove(playerId);
            }
        }
    }

}
