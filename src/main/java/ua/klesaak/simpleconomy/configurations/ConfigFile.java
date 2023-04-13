package ua.klesaak.simpleconomy.configurations;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ua.klesaak.simpleconomy.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.TimeUnit;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigFile extends PluginConfig {
    String storage;
    boolean useRedisMessagingUpdater;
    int maxBalance, maxCoins, startBalance, startCoins, minTransactionSum;
    int playerTopMoneyCount, playerTopCoinsCount, playerTopUpdateTickInterval;
    String topFormat;
    String currencyFormatPlural, currencyFormatSingular, currencyFormatPlural2, currencyCoinsFormatPlural, currencyCoinsFormatSingular, currencyCoinsFormatPlural2;


    public ConfigFile(JavaPlugin plugin) {
        super(plugin, "config.yml");
        this.storage = this.getString("storage");
        this.useRedisMessagingUpdater = this.getBoolean("useRedisMessagingUpdater");
        this.maxBalance = this.getInt("maxBalance");
        this.maxCoins = this.getInt("maxCoins");
        this.startBalance = this.getInt("startBalance");
        this.startCoins = this.getInt("startCoins");
        this.minTransactionSum = this.getInt("minTransactionSum");
        this.playerTopMoneyCount = this.getInt("playerTop.moneyCount");
        this.playerTopCoinsCount = this.getInt("playerTop.coinsCount");
        this.playerTopUpdateTickInterval = (int) (NumberUtils.parseTimeFromString(this.getString("playerTop.updateInterval"), TimeUnit.SECONDS) / 20);
        this.topFormat = this.getString("playerTop.topFormat");
        this.currencyFormatPlural = this.getString("currencyFormat.plural");
        this.currencyFormatSingular = this.getString("currencyFormat.singular");
        this.currencyFormatPlural2 = this.getString("currencyFormat.plural2");
        this.currencyCoinsFormatPlural = this.getString("currencyCoinsFormat.plural");
        this.currencyCoinsFormatSingular = this.getString("currencyCoinsFormat.singular");
        this.currencyCoinsFormatPlural2 = this.getString("currencyCoinsFormat.plural2");
    }

    public ConfigurationSection getFileSection() {
        return this.getConfigurationSection("file");
    }

    public ConfigurationSection getMySQLSection() {
        return this.getConfigurationSection("mysql");
    }

    public ConfigurationSection getRedisSection() {
        return this.getConfigurationSection("redis");
    }

    public String format(double amount) {
        return format(BigDecimal.valueOf(amount), 2);
    }

    private String format(BigDecimal amount, int scale) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat format = new DecimalFormat();
        format.setRoundingMode(RoundingMode.HALF_EVEN);
        format.setGroupingUsed(true);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(scale);
        format.setDecimalFormatSymbols(symbols);
        return format.format(amount);
    }
}
