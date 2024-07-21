package com.venned.simplemythicchest.database;

import com.venned.simplemythicchest.abstracts.AbstractDatabase;
import com.venned.simplemythicchest.build.MythicChest;
import com.venned.simplemythicchest.build.SubChest;
import com.venned.simplemythicchest.utils.MapUtils;
import com.venned.simplemythicchest.utils.UtilLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLiteDatabase extends AbstractDatabase {

    private final Plugin plugin;
    private final MapUtils mapUtils;

    public SQLiteDatabase(String url, Plugin plugin, MapUtils mapUtils) {
        super(url);
        this.plugin = plugin;
        this.mapUtils = mapUtils;
    }

    @Override
    public void loadChest() throws SQLException {
        Bukkit.getScheduler().runTaskLater(plugin, this::loadMythicChestsIntoMap, 60L);
    }

    private void loadMythicChestsIntoMap() {
        mapUtils.mythicChestsMap.clear();
        try {
            List<MythicChest> loadedChests = loadMythicChests();
            for (MythicChest chest : loadedChests) {
                mapUtils.mythicChestsMap.put(chest.getId(), chest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
      //  System.out.println("Cofres Cargados " + mapUtils.mythicChestsMap.size());
    }

    @Override
    public int getNextChestId() throws SQLException {
        String query = "SELECT MAX(id) AS max_id FROM chests;";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            } else {
                return 1;
            }
        }
    }


    @Override
    public void saveChest(int id, int level, String owner, String location, UUID uuid_island, int value_chest) throws SQLException {
        String insertChest = "INSERT INTO chests (id, level, owner, location, uuid_island, value_chest) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = connection.prepareStatement(insertChest)) {
            stmt.setInt(1, id);
            stmt.setInt(2, level);
            stmt.setString(3, owner);
            stmt.setString(4, location);
            stmt.setString(5, uuid_island.toString());
            stmt.setInt(6, value_chest);
            stmt.executeUpdate();
        }
    }

    @Override
    public void saveSubChest(int chestId, int sub_chest, String blockType, int quantity, int slot, int value_sub_chest) throws SQLException {
        String insertBlock = "INSERT INTO blocks (chest_id, sub_chest, block_type, quantity, slot, value_sub_chest) VALUES (?, ?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = connection.prepareStatement(insertBlock)) {
            stmt.setInt(1, chestId);
            stmt.setInt(2, sub_chest);
            stmt.setString(3, blockType);
            stmt.setInt(4, quantity);
            stmt.setLong(5, slot);
            stmt.setInt(6, value_sub_chest);
            stmt.executeUpdate();
        }
    }

    @Override
    public void saveAllChests() throws SQLException {
        String insertChest = "INSERT OR REPLACE INTO chests (id, level, owner, location, uuid_island, value_chest) VALUES (?, ?, ?, ?, ?, ?);";

        for (MythicChest mythicChest : mapUtils.mythicChestsMap.values()) {
            try (PreparedStatement stmt = connection.prepareStatement(insertChest)) {
                stmt.setInt(1, mythicChest.getId());
                stmt.setInt(2, mythicChest.getLevel());
                stmt.setString(3, mythicChest.getOwner());
                stmt.setString(4, UtilLocation.serializeLocation(mythicChest.getLocation())); // Assuming UtilLocation.serializeLocation() serializes the location
                stmt.setString(5, mythicChest.getUUIDIsland().toString());
                stmt.setInt(6, mythicChest.getValue_chest());
                stmt.executeUpdate();
            }
        }
    }

    public void saveAllSubChests() throws SQLException {
        String insertBlock = "INSERT INTO blocks (chest_id, sub_chest, block_type, quantity, slot, value_sub_chest) " +
                "VALUES (?, ?, ?, ?, ?, ?);";

        String updateBlock = "UPDATE blocks " +
                "SET block_type = ?, quantity = ?, value_sub_chest = ? " +
                "WHERE chest_id = ? AND sub_chest = ? AND slot = ?;";

        for (MythicChest mythicChest : mapUtils.mythicChestsMap.values()) {
            for (SubChest subChest : mythicChest.getSubChests()) {
                Inventory inventory = subChest.getInventory();
                int subChestNumber = subChest.getSub_chest();
                int chestId = mythicChest.getId();
                int valueSubChest = subChest.getValue_sub_chest();

                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    ItemStack item = inventory.getItem(slot);
                    if (item != null) {
                        String blockType = item.getType().toString();
                        int quantity = item.getAmount();

                        // Try to update the row, if no row was updated (i.e., the row doesn't exist), then insert
                        try (PreparedStatement stmtUpdate = connection.prepareStatement(updateBlock)) {
                            stmtUpdate.setString(1, blockType);
                            stmtUpdate.setInt(2, quantity);
                            stmtUpdate.setInt(3, valueSubChest);
                            stmtUpdate.setInt(4, chestId);
                            stmtUpdate.setInt(5, subChestNumber);
                            stmtUpdate.setInt(6, slot);

                            int rowsUpdated = stmtUpdate.executeUpdate();
                            if (rowsUpdated == 0) {
                                try (PreparedStatement stmtInsert = connection.prepareStatement(insertBlock)) {
                                    stmtInsert.setInt(1, chestId);
                                    stmtInsert.setInt(2, subChestNumber);
                                    stmtInsert.setString(3, blockType);
                                    stmtInsert.setInt(4, quantity);
                                    stmtInsert.setInt(5, slot);
                                    stmtInsert.setInt(6, valueSubChest);
                                    stmtInsert.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<MythicChest> loadMythicChests() throws SQLException {
        List<MythicChest> mythicChests = new ArrayList<>();

        String query = "SELECT id, level, owner, location, uuid_island, value_chest FROM chests;";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int level = rs.getInt("level");
                String owner = rs.getString("owner");
                String location = rs.getString("location");
                UUID uuid = UUID.fromString(rs.getString("uuid_island"));
                Location location_deserealizado = UtilLocation.deserializeLocation(location);
                int value_chest = rs.getInt("value_chest");

                MythicChest mythicChest = new MythicChest(id, level, owner, location_deserealizado, uuid, value_chest);

                List<SubChest> subChests = loadSubChests(id);
                for (SubChest subChest : subChests) {
                    subChest.setMain_chest(mythicChest);
                }

                mythicChest.setSubChests(subChests);
                mythicChests.add(mythicChest);

            }
        }

        return mythicChests;
    }

    private List<SubChest> loadSubChests(int chestId) throws SQLException {
        Map<Integer, SubChest> subChestsMap = new HashMap<>();

        String query = "SELECT sub_chest, block_type, quantity, slot, value_sub_chest FROM blocks WHERE chest_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, chestId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int subChestNumber = rs.getInt("sub_chest");
                String blockType = rs.getString("block_type");
                int quantity = rs.getInt("quantity");
                int slot = rs.getInt("slot");
                int value_sub_chest = rs.getInt("value_sub_chest");

                SubChest subChest = subChestsMap.computeIfAbsent(subChestNumber, k -> new SubChest(subChestNumber));
                ItemStack itemStack = new ItemStack(Material.valueOf(blockType), quantity);
                subChest.setValue_sub_chest(value_sub_chest);
                subChest.getInventory().setItem(slot, itemStack);
            }
        }

        return new ArrayList<>(subChestsMap.values());
    }

    @Override
    public MythicChest getByLocation(Location location){
       // System.out.println("Buscando en Location " + location);
        for(MythicChest mythicChest : mapUtils.mythicChestsMap.values()){
         //   System.out.println("Mythical Locations " + mythicChest.getLocation());
            if (mythicChest.getLocation().equals(location)) {

         //       System.out.println("Retornando a un mythic chest encontrado");
                return mythicChest;
            }
        }
        return null;
    }

    @Override
    public void deleteMythicChestByID(int chestId) throws SQLException {
        String deleteSubChestsQuery = "DELETE FROM blocks WHERE chest_id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSubChestsQuery)) {
            stmt.setInt(1, chestId);
            stmt.executeUpdate();
        }

        String deleteChestQuery = "DELETE FROM chests WHERE id = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(deleteChestQuery)) {
            stmt.setInt(1, chestId);
            stmt.executeUpdate();
        }

        mapUtils.mythicChestsMap.remove(chestId);

    }

    public MythicChest getMythicChest(int id) {
        return mapUtils.mythicChestsMap.get(id);
    }

    public List<MythicChest> getAllMythicChests() {
        return new ArrayList<>(mapUtils.mythicChestsMap.values());
    }
}
