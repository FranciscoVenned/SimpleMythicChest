package com.venned.simplemythicchest.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    private final Plugin plugin;

    public ItemUtil(Plugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack getItemMythic(int level){
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

        return chest_end;
    }
}
