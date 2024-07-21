package com.venned.simplemythicchest;

import com.venned.simplemythicchest.commands.MainCommand;
import com.venned.simplemythicchest.database.SQLiteDatabase;
import com.venned.simplemythicchest.interfaces.Database;
import com.venned.simplemythicchest.listener.InventoryListener;
import com.venned.simplemythicchest.listener.IslandListener;
import com.venned.simplemythicchest.listener.PlayerListener;
import com.venned.simplemythicchest.placeholder.UtilPlaceHolder;
import com.venned.simplemythicchest.utils.MapUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class Main extends JavaPlugin {
    private Database database;
    private MapUtils mapUtils;

    @Override
    public void onEnable() {
        this.mapUtils = new MapUtils(this);
        saveDefaultConfig();

        try {
            database = new SQLiteDatabase("jdbc:sqlite:" + getDataFolder() + "/chests.db", this, mapUtils);
            database.connect();
            database.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new UtilPlaceHolder(this).register();
        }


        getCommand("mythicalchest").setExecutor(new MainCommand(this));
        getServer().getPluginManager().registerEvents(new IslandListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this, mapUtils), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this, mapUtils), this);
    }


    @Override
    public void onDisable() {
        try {
            database.saveAllChests();
            database.saveAllSubChests();
            database.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void createPluginFolder() {
        File pluginFolder = getDataFolder();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
    }

    public Database getDatabase() {
        return database;
    }
}
