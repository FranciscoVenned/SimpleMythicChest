package com.venned.simplemythicchest.build;

import com.venned.simplemythicchest.utils.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SubChest {
    private final Inventory inventory;
    private final int sub_chest;
    private MythicChest main_chest;
    private int value_sub_chest;

    public SubChest(int sub_chest, MythicChest main_chest) {
        this.inventory = Bukkit.createInventory(null, 54, "SubChest " + sub_chest);
        this.sub_chest = sub_chest;
        this.main_chest = main_chest;
    }

    public SubChest(int sub_chest){
        this.inventory = Bukkit.createInventory(null, 54, "SubChest " + sub_chest);
        this.sub_chest = sub_chest;
    }

    public void setMain_chest(MythicChest main_chest) {
        this.main_chest = main_chest;
    }

    public int getValue_sub_chest() {
        return value_sub_chest;
    }

    public void setValue_sub_chest(int value_sub_chest) {
        this.value_sub_chest = value_sub_chest;
    }

    public void updateValue(MapUtils mapUtils) {
        int totalValue = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null) {
                String blockType = item.getType().toString();
                int quantity = item.getAmount();
                totalValue += mapUtils.getBlockValue(blockType) * quantity;
            }
        }
        setValue_sub_chest(totalValue);
        mapUtils.updateValueMythical(this.getMain_chest());
    }

    public MythicChest getMain_chest() {
        return main_chest;
    }

    public int getSub_chest() {
        return sub_chest;
    }

    public Inventory getInventory() {
        return inventory;
    }
}