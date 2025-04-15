package ua.klesaak.simpleconomy.papi;

import lombok.NonNull;
import lombok.val;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ua.klesaak.simpleconomy.SimpleConomyPlugin;
import ua.klesaak.simpleconomy.manager.SimpleEconomyManager;

import java.util.Locale;
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
        String identifierLC = identifier.toLowerCase(Locale.ROOT);
        if (configFile.isTopEnabled()) {
            String COINS_PLAYER_TOP_IDENTIFIER = "coins_top_player_";
            if (identifierLC.startsWith(COINS_PLAYER_TOP_IDENTIFIER)) {
                int coinsPlayerIndex = this.parseInt(identifierLC.split(COINS_PLAYER_TOP_IDENTIFIER)[1]) - 1;
                val coinsTop = this.manager.getTopManager().getCoinsTopData();
                if (!coinsTop.isEmpty() && coinsPlayerIndex <= configFile.getPlayerTopCoinsCount() && coinsPlayerIndex <= coinsTop.size()) {
                    return coinsTop.get(coinsPlayerIndex).getNickName();
                }
            }

            String COINS_TOP_FORMATTED_IDENTIFIER = "coins_top_formatted_";
            if (identifierLC.startsWith(COINS_TOP_FORMATTED_IDENTIFIER)) {
                int coinsPlayerIndex = this.parseInt(identifierLC.split(COINS_TOP_FORMATTED_IDENTIFIER)[1]) - 1;
                val coinsTop = this.manager.getTopManager().getCoinsTopData();
                if (!coinsTop.isEmpty() && coinsPlayerIndex <= configFile.getPlayerTopCoinsCount() && coinsPlayerIndex <= coinsTop.size()) {
                    return configFile.formatCoins(coinsTop.get(coinsPlayerIndex).getSum());
                }
            }

            String COINS_TOP_IDENTIFIER = "coins_top_";
            if (identifierLC.startsWith(COINS_TOP_IDENTIFIER)) {
                int coinsPlayerIndex = this.parseInt(identifierLC.split(COINS_TOP_IDENTIFIER)[1]) - 1;
                val coinsTop = this.manager.getTopManager().getCoinsTopData();
                if (!coinsTop.isEmpty() && coinsPlayerIndex <= configFile.getPlayerTopCoinsCount() && coinsPlayerIndex <= coinsTop.size()) {
                    return String.valueOf(coinsTop.get(coinsPlayerIndex).getSum());
                }
            }

            String MONEY_PLAYER_TOP_IDENTIFIER = "money_top_player_";
            if (identifierLC.startsWith(MONEY_PLAYER_TOP_IDENTIFIER)) {
                int moneyPlayerIndex = this.parseInt(identifierLC.split(MONEY_PLAYER_TOP_IDENTIFIER)[1]) - 1;
                val moneyTop = this.manager.getTopManager().getMoneyTopData();
                if (!moneyTop.isEmpty() && moneyPlayerIndex <= configFile.getPlayerTopMoneyCount() && moneyPlayerIndex <= moneyTop.size()) {
                    return moneyTop.get(moneyPlayerIndex).getNickName();
                }
            }

            String MONEY_TOP_FORMATTED_IDENTIFIER = "money_top_formatted_";
            if (identifierLC.startsWith(MONEY_TOP_FORMATTED_IDENTIFIER)) {
                int moneyPlayerIndex = this.parseInt(identifierLC.split(MONEY_TOP_FORMATTED_IDENTIFIER)[1]) - 1;
                val moneyTop = this.manager.getTopManager().getMoneyTopData();
                if (!moneyTop.isEmpty() && moneyPlayerIndex <= configFile.getPlayerTopMoneyCount() && moneyPlayerIndex <= moneyTop.size()) {
                    return configFile.formatMoney(moneyTop.get(moneyPlayerIndex).getSum());
                }
            }

            String MONEY_TOP_IDENTIFIER = "money_top_";
            if (identifierLC.startsWith(MONEY_TOP_IDENTIFIER)) {
                int moneyPlayerIndex = this.parseInt(identifierLC.split(MONEY_TOP_IDENTIFIER)[1]) - 1;
                val moneyTop = this.manager.getTopManager().getMoneyTopData();
                if (!moneyTop.isEmpty() && moneyPlayerIndex <= configFile.getPlayerTopMoneyCount() && moneyPlayerIndex <= moneyTop.size()) {
                    return String.valueOf(moneyTop.get(moneyPlayerIndex).getSum());
                }
            }
        }
        return switch (identifierLC) {
            case "coins" -> String.valueOf(storage.getCoinsBalance(playerName));
            case "money" -> String.valueOf(storage.getMoneyBalance(playerName));
            case "coins_formatted" -> configFile.formatCoins(storage.getCoinsBalance(playerName));
            case "money_formatted" -> configFile.formatMoney(storage.getMoneyBalance(playerName));
            default -> "";
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
