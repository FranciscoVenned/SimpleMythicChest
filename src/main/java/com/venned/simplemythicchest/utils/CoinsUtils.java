package com.venned.simplemythicchest.utils;

import me.dev.faresmahdi.skycoins.EconomyManager;
import me.dev.faresmahdi.skycoins.SkyCoins;
import org.bukkit.entity.Player;

public class CoinsUtils {

    public static double getCoinsPlayer(Player player){
        EconomyManager economyManager = new EconomyManager(player);
        if(economyManager != null) {
            return economyManager.getBalance();
        }
        return 0.0;
    }

    public static void withDraw(Player player, double amount){
        EconomyManager economyManager = new EconomyManager(player);
        if(economyManager != null) {
            economyManager.setBalance(economyManager.getBalance() - amount);
        }
    }
}
