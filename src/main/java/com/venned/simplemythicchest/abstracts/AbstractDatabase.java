package com.venned.simplemythicchest.abstracts;

import com.venned.simplemythicchest.interfaces.Database;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDatabase implements Database {
    protected Connection connection;
    protected String url;

    public AbstractDatabase(String url) {
        this.url = url;
    }

    @Override
    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url);
        loadChest();
    }

    @Override
    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public void createTables() throws SQLException {
        String createChestsTable = "CREATE TABLE IF NOT EXISTS chests (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "level INTEGER NOT NULL," +
                "owner TEXT NOT NULL," +
                "location TEXT NOT NULL," +
                "uuid_island TEXT NOT NULL," +
                "value_chest INTEGER NOT NULL" +
                ");";

        String createBlocksTable = "CREATE TABLE IF NOT EXISTS blocks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chest_id INTEGER NOT NULL, " +
                "sub_chest INTEGER NOT NULL, " +
                "block_type TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "slot INTEGER NOT NULL, " +
                "value_sub_chest INTEGER NOT NULL, " +
                "FOREIGN KEY (chest_id) REFERENCES chests(id)" +
                ");";

        try (PreparedStatement stmt1 = connection.prepareStatement(createChestsTable);
             PreparedStatement stmt2 = connection.prepareStatement(createBlocksTable)) {
            stmt1.execute();
            stmt2.execute();
        }
    }
}
