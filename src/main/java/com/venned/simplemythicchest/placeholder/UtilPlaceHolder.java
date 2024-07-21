package com.venned.simplemythicchest.placeholder;

import com.venned.simplemythicchest.Main;
import com.venned.simplemythicchest.utils.IslandUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UtilPlaceHolder extends PlaceholderExpansion {

    private final Main plugin;

    public UtilPlaceHolder(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mythicalchest";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("value")) {
            int value = IslandUtils.getValue(player);
            return String.valueOf(value);
        }



        return null;
    }
}
