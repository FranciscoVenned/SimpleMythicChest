package com.venned.simplemythicchest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class UtilLocation {

    public static String serializeLocation(Location location) {
        if (location == null) return null;
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static Location deserializeLocation(String serializedLocation) {
        if (serializedLocation == null || serializedLocation.isEmpty()) return null;
        String[] parts = serializedLocation.split(",");
        if (parts.length != 4) return null;

        String worldName = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(world, x, y, z);
    }
}
