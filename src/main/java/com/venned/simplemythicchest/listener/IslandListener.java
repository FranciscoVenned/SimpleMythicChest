package com.venned.simplemythicchest.listener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.math.BigDecimal;

public class IslandListener implements Listener {

    @EventHandler
    public void islandData(BlockPlaceEvent event) {
        Island island = SuperiorSkyblockAPI.getIslandAt(event.getBlock().getLocation());
        if(island != null) {;
        }
    }
}
