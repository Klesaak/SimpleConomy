package ua.klesaak.simpleconomy.papi;

import lombok.NonNull;
import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.klesaak.simpleconomy.SimpleConomyPlugin;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.Objects;

public class PAPIExpansion extends PlaceholderExpansion {
    private static final String COINS_PLAYER_TOP_IDENTIFIER = "coins_player_top_";
    private static final String COINS_TOP_IDENTIFIER = "coins_top_";
    private static final String MONEY_PLAYER_TOP_IDENTIFIER = "money_player_top_";
    private static final String MONEY_TOP_IDENTIFIER = "money_top_";
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
        val storage = this.manager.getStorage();
        val configFile = this.manager.getConfigFile();
        val playerName = player.getName().toLowerCase();
        if (configFile.isTopEnabled()) {
            if (identifier.startsWith(COINS_PLAYER_TOP_IDENTIFIER)) {
                int coinsPlayerIndex = this.parseInt(identifier.split(COINS_PLAYER_TOP_IDENTIFIER)[1]) - 1;
                val coinsTop = this.manager.getTopManager().getCoinsTopData();
                if (!coinsTop.isEmpty() && coinsPlayerIndex <= configFile.getPlayerTopCoinsCount() && coinsPlayerIndex < coinsTop.size()) {
                    return coinsTop.get(coinsPlayerIndex).getNickName();
                }
            }

            if (identifier.startsWith(COINS_TOP_IDENTIFIER)) {
                int coinsPlayerIndex = this.parseInt(identifier.split(COINS_TOP_IDENTIFIER)[1]) - 1;
                val coinsTop = this.manager.getTopManager().getCoinsTopData();
                if (!coinsTop.isEmpty() && coinsPlayerIndex <= configFile.getPlayerTopCoinsCount() && coinsPlayerIndex < coinsTop.size()) {
                    return String.valueOf(coinsTop.get(coinsPlayerIndex).getSum());
                }
            }

            if (identifier.startsWith(MONEY_PLAYER_TOP_IDENTIFIER)) {
                int moneyPlayerIndex = this.parseInt(identifier.split(MONEY_PLAYER_TOP_IDENTIFIER)[1]) - 1;
                val moneyTop = this.manager.getTopManager().getMoneyTopData();
                if (!moneyTop.isEmpty() && moneyPlayerIndex <= configFile.getPlayerTopMoneyCount() && moneyPlayerIndex < moneyTop.size()) {
                    return moneyTop.get(moneyPlayerIndex).getNickName();
                }
            }

            if (identifier.startsWith(MONEY_TOP_IDENTIFIER)) {
                int moneyPlayerIndex = this.parseInt(identifier.split(MONEY_TOP_IDENTIFIER)[1]) - 1;
                val moneyTop = this.manager.getTopManager().getMoneyTopData();
                if (!moneyTop.isEmpty() && moneyPlayerIndex <= configFile.getPlayerTopMoneyCount() && moneyPlayerIndex < moneyTop.size()) {
                    return String.valueOf(moneyTop.get(moneyPlayerIndex).getSum());
                }
            }
        }
        return switch (identifier) {
            case COINS -> String.valueOf(storage.getCoinsBalance(playerName));
            case MONEY -> String.valueOf(storage.getMoneyBalance(playerName));
            case COINS_FORMATTED -> configFile.formatCoins(storage.getCoinsBalance(playerName));
            case MONEY_FORMATTED -> configFile.formatMoney(storage.getMoneyBalance(playerName));
            default -> "<N/A>";
        };
    }

    private int parseInt(String string) {
        int sum;
        try {
            sum = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
        return sum;
    }
}
