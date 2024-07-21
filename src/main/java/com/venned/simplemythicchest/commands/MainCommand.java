package com.venned.simplemythicchest.commands;

import com.venned.simplemythicchest.Main;
import com.venned.simplemythicchest.utils.Util;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private final Main plugin;

    public MainCommand(Main plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(args.length == 0) {
            sender.sendMessage("§c§l(!) §7Usage /mythicalchest give");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if(sender.hasPermission("simplemythicchest.give")) {
                if (args.length != 4) {
                    sender.sendMessage("§c§l(!) §7Usage /mythicalchest give <player> <level> <amount>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                assert target != null;
                int level = 0;
                int amount = 0;
                try {
                    level = Integer.parseInt(args[2]);
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("You have entered an invalid number");
                }

                ItemStack chest_end = new ItemStack(Material.ENDER_CHEST);
                ItemMeta meta = chest_end.getItemMeta();
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "mythical"),
                        PersistentDataType.INTEGER,
                        level);

                String display_name = Util.colorize(plugin.getConfig().getString("display-name-mythic")
                        .replace("%level%", String.valueOf(level)));
                List<String> lore_config = plugin.getConfig().getStringList("lore-mythic");
                List<String> lore_new = new ArrayList<>();
                for (String lore : lore_config) {
                    lore_new.add(Util.colorize(lore));
                }

                meta.setDisplayName(display_name);
                meta.setLore(lore_new);

                chest_end.setItemMeta(meta);

                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(chest_end);
                }
            }

        }

        return false;
    }

}
