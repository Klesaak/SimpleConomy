package ua.klesaak.simpleconomy.configurations;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import ua.klesaak.simpleconomy.utils.NumberUtils;
import ua.klesaak.simpleconomy.utils.UtilityMethods;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigFile extends PluginConfig {
    String storage;
    int maxBalance, maxCoins, startBalance, startCoins, minTransactionSum;
    int playerTopMoneyCount, playerTopCoinsCount, playerTopUpdateTickInterval;
    String topFormat;
    boolean payCommandEnabled;
    String currencyFormatPlural, currencyFormatSingular, currencyFormatPlural2, currencyCoinsFormatPlural, currencyCoinsFormatSingular, currencyCoinsFormatPlural2;


    public ConfigFile(JavaPlugin plugin) {
        super(plugin, "config.yml");
        this.storage = this.getString("storage");
        this.payCommandEnabled = this.getBoolean("payCommandEnabled");
        this.maxBalance = this.getInt("maxBalance");
        this.maxCoins = this.getInt("maxCoins");
        this.startBalance = this.getInt("startBalance");
        this.startCoins = this.getInt("startCoins");
        this.minTransactionSum = this.getInt("minTransactionSum");
        this.playerTopMoneyCount = this.getInt("playerTop.moneyCount");
        this.playerTopCoinsCount = this.getInt("playerTop.coinsCount");
        this.playerTopUpdateTickInterval = (int) (NumberUtils.parseTimeFromString(Objects.requireNonNull(this.getString("playerTop.updateInterval")), TimeUnit.SECONDS) / 20);
        this.topFormat = UtilityMethods.color(this.getString("playerTop.topFormat"));
        this.currencyFormatPlural = this.getString("currencyFormat.plural");
        this.currencyFormatSingular = this.getString("currencyFormat.singular");
        this.currencyFormatPlural2 = this.getString("currencyFormat.plural2");
        this.currencyCoinsFormatPlural = this.getString("currencyCoinsFormat.plural");
        this.currencyCoinsFormatSingular = this.getString("currencyCoinsFormat.singular");
        this.currencyCoinsFormatPlural2 = this.getString("currencyCoinsFormat.plural2");
    }

    public String formatTopLine(String index, String player, String balance) {
        String format = this.topFormat;
        format = UtilityMethods.replaceAll(MessagesFile.INDEX_PATTERN, format, ()-> index);
        format = UtilityMethods.replaceAll(MessagesFile.PLAYER_PATTERN, format, ()-> player);
        format = UtilityMethods.replaceAll(MessagesFile.BALANCE_PATTERN, format, ()-> balance);
        return format;
    }

    public ConfigurationSection getMySQLSection() {
        return this.getConfigurationSection("mysql");
    }

    public ConfigurationSection getRedisSection() {
        return this.getConfigurationSection("redis");
    }

    public String format(double amount) {
        return format(BigDecimal.valueOf(amount));
    }

    private String format(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat format = new DecimalFormat();
        format.setRoundingMode(RoundingMode.HALF_EVEN);
        format.setGroupingUsed(true);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(2);
        format.setDecimalFormatSymbols(symbols);
        return format.format(amount);
    }

    public String formatMoney(double amount) {
        return NumberUtils.formatting((int)amount, this.currencyFormatSingular, this.currencyFormatPlural, this.currencyFormatPlural2);
    }

    public String formatCoins(int amount) {
        return NumberUtils.formatting(amount, this.currencyCoinsFormatSingular, this.currencyCoinsFormatPlural, this.currencyCoinsFormatPlural2);
    }
}
