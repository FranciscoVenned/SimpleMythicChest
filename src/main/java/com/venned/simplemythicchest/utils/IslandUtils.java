package com.venned.simplemythicchest.utils;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class IslandUtils {

    public static int getValue(Player player) {
        Island island = SuperiorSkyblockAPI.getIslandAt(player.getLocation());
        if (island != null) {
            BigDecimal bonusWorth = island.getBonusWorth();
            BigDecimal worth = island.getRawWorth();
            BigDecimal totalValue = bonusWorth.add(worth);
            return totalValue.intValue();
        }
        return 0;
    }

    public static void updateValue(Location location, int new_value, int before_value){
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        if (island != null) {
            BigDecimal bonusWorth = island.getBonusWorth();
            BigDecimal updatedWorth = bonusWorth.subtract(BigDecimal.valueOf(before_value))
                    .add(BigDecimal.valueOf(new_value));
            island.setBonusWorth(updatedWorth);
       //     System.out.println("Updated island bonus worth to: " + updatedWorth);
        }
    }
}
