package ua.klesaak.simpleconomy.papi;

import lombok.NonNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.SimpleConomyPlugin;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.Objects;

public class PAPIExpansion extends PlaceholderExpansion {
    private final SimpleEconomyManager manager;

    public PAPIExpansion(SimpleEconomyManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean canRegister() {
        SimpleConomyPlugin plugin = (SimpleConomyPlugin) Bukkit.getPluginManager().getPlugin(Objects.requireNonNull(this.getRequiredPlugin()));
        return plugin != null;
    }

    @NonNull @Override
    public String getAuthor() {
        return "klesaak";
    }

    @NonNull @Override
    public String getIdentifier() {
        return "SimpleEconomy";
    }

    @NonNull @Override
    public String getVersion() {
        return this.manager.getPlugin().getDescription().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return this.manager.getPlugin().getDescription().getName();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("user")) {
            return player.getName();
        }

        return "";
    }
}
