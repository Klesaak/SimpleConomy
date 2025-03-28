package ua.klesaak.simpleconomy.papi;

import lombok.NonNull;
import lombok.val;
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
        return "sc";
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
        val storage = this.manager.getStorage();
        val configFile = this.manager.getConfigFile();
        val playerName = player.getName().toLowerCase();
        return switch (identifier.toLowerCase()) {
            case "coins" -> String.valueOf(storage.getCoinsBalance(playerName));
            case "money" -> String.valueOf(storage.getMoneyBalance(playerName));
            case "coins_formatted" -> configFile.formatCoins(storage.getCoinsBalance(playerName));
            case "money_formatted" -> configFile.formatMoney(storage.getMoneyBalance(playerName));
            default -> "";
        };
    }
}
