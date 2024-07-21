package com.venned.simplemythicchest.build;


import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.UUID;

public class MythicChest {
    private final int id;
    private int level;
    private final String owner;
    private final List<SubChest> subChests;
    private final Location location;
    private final UUID uuid;
    private int value_chest;

    public MythicChest(int id, int level, String owner, Location location, UUID uuid, int value_chest) {
        this.id = id;
        this.level = level;
        this.owner = owner;
        this.subChests = new ArrayList<>();
        this.location = location;
        this.uuid = uuid;
        this.value_chest = value_chest;
    }

    public void setValue_chest(int value_chest) {
        this.value_chest = value_chest;
    }

    public int getValue_chest() {
        return value_chest;
    }

    public UUID getUUIDIsland() {
        return uuid;
    }

    public Location getLocation() {
        return location;
    }

    public void setSubChests(List<SubChest> subChests) {
        this.subChests.addAll(subChests);
    }

    public int getId() {
        return id;
    }

    public void increaseLevel(int level) {
        this.level += level;
    }

    public int getLevel() {
        return level;
    }

    public String getOwner() {
        return owner;
    }

    public List<SubChest> getSubChests() {
        return subChests;
    }


    public int getTotalValue_SubChests() {
        int totalValue = 0;
        for (SubChest subChest : subChests) {
            totalValue += subChest.getValue_sub_chest();
        }
        return totalValue;
    }
}
