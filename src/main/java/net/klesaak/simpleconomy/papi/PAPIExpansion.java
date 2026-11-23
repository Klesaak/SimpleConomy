package net.klesaak.simpleconomy.papi;

import lombok.NonNull;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.klesaak.simpleconomy.SimpleConomyPlugin;
import net.klesaak.simpleconomy.configurations.ConfigFile;
import net.klesaak.simpleconomy.manager.SimpleEconomyManager;
import net.klesaak.simpleconomy.storage.AbstractStorage;

import java.util.Objects;

public class PAPIExpansion extends PlaceholderExpansion {
    private static final String COINS = "coins";
    private static final String COINS_FORMATTED = "coins_formatted";
    private static final String MONEY = "money";
    private static final String MONEY_FORMATTED = "money_formatted";


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
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        AbstractStorage storage = this.manager.getStorage();
        ConfigFile configFile = ConfigFile.get();
        String playerName = player.getName().toLowerCase();
        return switch (identifier) {
            case COINS -> String.valueOf(storage.getCoinsBalance(playerName));
            case MONEY -> String.valueOf(storage.getMoneyBalance(playerName));
            case COINS_FORMATTED -> configFile.formatCoins(storage.getCoinsBalance(playerName));
            case MONEY_FORMATTED -> configFile.formatMoney(storage.getMoneyBalance(playerName));
            default -> "<N/A>";
        };
    }
}
