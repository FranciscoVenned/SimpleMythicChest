package com.venned.simplemythicchest.interfaces;

import com.venned.simplemythicchest.build.MythicChest;
import org.bukkit.Location;

import java.sql.SQLException;
import java.util.UUID;

public interface Database {
    void connect() throws SQLException;
    void saveAllChests() throws SQLException;
    void saveAllSubChests() throws SQLException;
    void disconnect() throws SQLException;
    void deleteMythicChestByID(int chestId) throws SQLException;
    void createTables() throws SQLException;
    int getNextChestId() throws SQLException;
    void saveChest(int id, int level, String owner, String location, UUID uuid_island, int value_chest) throws SQLException;
    void saveSubChest(int chestId, int sub_chest, String blockType, int quantity, int slot, int value_sub_chest) throws SQLException;
    void loadChest() throws SQLException;
    MythicChest getByLocation(Location location);
}
